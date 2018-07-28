package com.levspb666.alphabet;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.levspb666.alphabet.Action.NOTHING;
import static com.levspb666.alphabet.Action.START;
import static com.levspb666.alphabet.Action.YES;
import static com.levspb666.alphabet.Settings.colorLetter;
import static com.levspb666.alphabet.Settings.countBalloons;
import static com.levspb666.alphabet.Settings.drawableFon;
import static com.levspb666.alphabet.Settings.excludeLetters;
import static com.levspb666.alphabet.Settings.fon;
import static com.levspb666.alphabet.Settings.infoFon;
import static com.levspb666.alphabet.Settings.infoOn;
import static com.levspb666.alphabet.util.BalloonUtil.balloonLauncher;
import static com.levspb666.alphabet.util.BalloonUtil.observer;
import static com.levspb666.alphabet.util.GameUtil.alphabetHard;
import static com.levspb666.alphabet.util.GameUtil.alphabetLight;
import static com.levspb666.alphabet.util.GameUtil.getCroppedBitmap;
import static com.levspb666.alphabet.util.GameUtil.getErrorText;
import static com.levspb666.alphabet.util.GameUtil.getletter;
import static com.levspb666.alphabet.util.GameUtil.match;
import static com.levspb666.alphabet.util.SoundUtil.muteAudio;
import static com.levspb666.alphabet.util.SoundUtil.play;
import static com.levspb666.alphabet.util.SoundUtil.poolPlayers;

public class Game extends AppCompatActivity implements
        RecognitionListener, Balloon.BalloonListener {

    private static final int NUM_PARTICLES = 30;
    private static final int REQUEST_RECORD_PERMISSION = 100;

    public static List<Balloon> mBalloons = new ArrayList<>();
    public static ViewGroup mContentView;
    private AtomicInteger mBalloonsPopped = new AtomicInteger(0);

    private TextView returnedText;
    private static ProgressBar progressBar;
    private static SpeechRecognizer speech = null;
    private static Intent recognizerIntent;
    private static Button next;
    public static String letter;
    private static TextView ext;
    private static Animation anim;
    private static String recording = "";
    private static String LOG_TAG = "GAME";
    private static ImageView mic;
    public static boolean canContinue;
    public static boolean closeView;
    public static boolean isNextClick;
    public static Activity activityGame;
    public static AudioManager audioManager;
    private static Handler restart = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.game);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if (fon) {
            ImageView view = findViewById(R.id.gameFon);
            view.setImageDrawable(drawableFon);
        }
        mContentView = findViewById(R.id.speachId);
        mic = findViewById(R.id.mic);
        next = findViewById(R.id.next);
        returnedText = findViewById(R.id.infoText);
        progressBar = findViewById(R.id.progressBar1);
        ext = findViewById(R.id.letter);
        ext.setTextColor(colorLetter);

        if (infoOn) {
            returnedText.setVisibility(View.VISIBLE);
            if (infoFon) {
                returnedText.setBackgroundColor(Color.argb(0, 0, 0, 0));
            } else {
                returnedText.setBackgroundColor(Color.WHITE);
            }
        } else {
            returnedText.setVisibility(View.INVISIBLE);
        }

        observer();
        progressBar.setVisibility(View.INVISIBLE);
        mic.setVisibility(View.INVISIBLE);
        closeView = false;
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "PackageName");
        anim = AnimationUtils.loadAnimation(Game.this, R.anim.letter);
        activityGame = Game.this;
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        goLetter(Game.this);
    }

    public void next(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
        } else {
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        }
        canContinue = false;
        isNextClick = true;
        next.setEnabled(false);
        stopRecord();
        speech.cancel();
        Animation anim = AnimationUtils.loadAnimation(Game.this, R.anim.click);
        new Thread(() -> play(Game.this, R.raw.click, YES)).start();
        next.startAnimation(anim);
    }

    public static void goLetter(Context context) {
        canContinue = true;
        isNextClick = false;
        next.setAlpha(0.1f);
        next.setEnabled(false);
        if (excludeLetters) {
            letter = getletter(alphabetLight);
        } else {
            letter = getletter(alphabetHard);
        }
        ext.setText(letter);
        anim.setStartOffset(700);
        ext.startAnimation(anim);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (poolPlayers != null && !poolPlayers.isEmpty()) {
                for (int i = 0; i < poolPlayers.size(); i++) {
                    poolPlayers.get(i).stop();
                    poolPlayers.get(i).release();
                }
            }
            play(context, R.raw.say, START);
        }).start();
    }

    public static void start1() {
        next.setAlpha(1f);
        recording = "";
        Log.i(LOG_TAG, "start");
        restart.postDelayed(Game::startRecord, 500);
        isNextClick = false;
        next.setEnabled(true);
    }

    private void verification() {
        if (!isNextClick && canContinue) {
            if (recording != null && !recording.isEmpty() && recording.length() > 0) {
                recording = recording.toUpperCase();
                switch (letter) {
                    case "Й":
                        if (recording.contains("Й") ||
                                (recording.contains("И") && recording.contains("КРАТКОЕ"))) {
                            right();
                        } else notRight();
                        break;
                    case "Ь":
                        if (recording.contains("МЯГКИЙ") && recording.contains("ЗНАК")) {
                            right();
                        } else notRight();
                        break;
                    case "Ъ":
                        if ((recording.contains("ТВЕРДЫЙ") || recording.contains("ТВЁРДЫЙ"))
                                && recording.contains("ЗНАК")) {
                            right();
                        } else notRight();
                        break;
                    default:
                        if (recording.contains(letter)) {
                            right();
                        } else {
                            notRight();
                        }
                }
            } else {
                Toast.makeText(this, "No", Toast.LENGTH_SHORT).show();
                if (canContinue) {
                    start1();
                }
            }
        }
    }

    private void right() {
        Log.i(LOG_TAG, "TRUE");
        next.setAlpha(0.1f);
        next.setEnabled(false);
        Toast.makeText(this, "Yes", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            muteAudio(false);
            if (canContinue) {
                play(Game.this, R.raw.yes, YES);
            }
        }).start();
    }

    private void notRight() {
        Log.i(LOG_TAG, "NO");
        Toast.makeText(this, "No", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            muteAudio(false);
            if (canContinue) {
                play(Game.this, R.raw.no, START);
            }
        }).start();
    }

    @Override
    protected void onStop() {
        muteAudio(false);
        canContinue = false;
        closeView = true;
        if (speech != null) {
            speech.stopListening();
            stopRecord();
            speech.cancel();
            speech.destroy();
        }
        if (mBalloons != null && !mBalloons.isEmpty()) {
            balloonLauncher.cancel(true);
            List<Balloon> balloonSet = new ArrayList<>();
            balloonSet.addAll(mBalloons);
            for (Balloon balloon : balloonSet) {
                balloon.cancelBalloon();
            }
            balloonSet.clear();
            mBalloons.clear();
        }
        if (poolPlayers != null && !poolPlayers.isEmpty()) {
            for (int i = 0; i < poolPlayers.size(); i++) {
                poolPlayers.get(i).stop();
                poolPlayers.get(i).release();
            }
        }
        super.onStop();
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(9);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
//        Log.i(LOG_TAG, "onBufferReceived: " + Arrays.toString(buffer));
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        if (canContinue && !isNextClick) {
            if (errorCode == SpeechRecognizer.ERROR_CLIENT) {
                speech.cancel();
            } else {
                stopRecord();
                muteAudio(false);
                if (errorCode == SpeechRecognizer.ERROR_NO_MATCH && next.isEnabled()) {
                    returnedText.setText(errorMessage);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION, new ArrayList<>());
                    onResults(bundle);
                }
                if (errorCode == SpeechRecognizer.ERROR_NETWORK) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Game.this);
                    builder.setTitle("ERROR_NETWORK!")
                            .setMessage("Проверьте подключение к интернету!")
                            .setCancelable(false)
                            .setNegativeButton("ОК", (dialog, id) -> {
                                dialog.cancel();
                                onBackPressed();
                                finish();
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                if (errorCode == SpeechRecognizer.ERROR_AUDIO) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Game.this);
                    builder.setTitle("ERROR_AUDIO!")
                            .setMessage("Ошибка аудио потока!")
                            .setCancelable(false)
                            .setNegativeButton("ОК", (dialog, id) -> {
                                dialog.cancel();
                                onBackPressed();
                                finish();
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                if (errorCode == SpeechRecognizer.ERROR_SERVER) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Game.this);
                    builder.setTitle("ERROR_SERVER!")
                            .setMessage("Ошибка подключения к серверу Google. \nПопробуйте перезапустить приложение!")
                            .setCancelable(false)
                            .setNegativeButton("ОК", (dialog, id) -> {
                                dialog.cancel();
                                onBackPressed();
                                finish();
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                if (errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    stopRecord();
                    muteAudio(false);
                    Toast.makeText(this, "ERROR_SPEECH_TIMEOUT", Toast.LENGTH_LONG).show();
                    next(new View(this));
                }
            }
        } else {
            stopRecord();
            muteAudio(false);
        }
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        stopRecord();
        speech.startListening(recognizerIntent);
        speech.cancel();
        StringBuilder text = new StringBuilder();
        if (matches != null) {
            for (String result : matches) {
                if (!result.isEmpty()) {
                    text.append(match(result)).append("\n");
                }
            }
        }
        recording = text.toString();
        recording.replace("  ", "");
        returnedText.setText(recording);
        verification();
    }

    @Override
    public void onRmsChanged(float rmsdB) {
//        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }

    @Override
    public void popBalloon(Balloon balloon, boolean userTouch) {
        if (userTouch) {
            play(Game.this, R.raw.touch, NOTHING);
            Bitmap bitmap = Bitmap.createBitmap(1, 1,
                    Bitmap.Config.ARGB_8888);
            bitmap = getCroppedBitmap(bitmap, balloon.getColor());
            new ParticleSystem(this, NUM_PARTICLES, bitmap, 500)
                    .setSpeedRange(0.1f, 0.5f)
                    .oneShot(balloon, NUM_PARTICLES);
        }
        mBalloonsPopped.getAndIncrement();
        mContentView.removeView(balloon);
        mBalloons.remove(balloon);
        if (mBalloonsPopped.get() >= countBalloons) {
            mBalloonsPopped.set(0);
            if (canContinue) {
                goLetter(this);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        onStop();
        super.onSaveInstanceState(outState);
    }

    private static boolean record = false;

    private static void startRecord() {
        record = true;
        muteAudio(true);
        mic.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        ActivityCompat.requestPermissions
                (activityGame,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_PERMISSION);
        Log.i(LOG_TAG, "START LISTENING");
        speech.startListening(recognizerIntent);
    }

    private void stopRecord() {
        if (record) {
            record = false;
            speech.stopListening();
            speech.cancel();
            mic.setVisibility(View.INVISIBLE);
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.INVISIBLE);
            Log.i(LOG_TAG, "STOP LISTENING");
        }
    }
}
