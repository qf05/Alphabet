package levspb666.ru.alphabet;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static levspb666.ru.alphabet.Action.NOTHING;
import static levspb666.ru.alphabet.Action.START;
import static levspb666.ru.alphabet.Action.YES;
import static levspb666.ru.alphabet.Settings.colorLetter;
import static levspb666.ru.alphabet.Settings.countBalloons;
import static levspb666.ru.alphabet.Settings.drawableFon;
import static levspb666.ru.alphabet.Settings.excludeLetters;
import static levspb666.ru.alphabet.Settings.fon;
import static levspb666.ru.alphabet.Settings.infoFon;
import static levspb666.ru.alphabet.Settings.infoOn;
import static levspb666.ru.alphabet.util.BalloonUtil.balloonLauncher;
import static levspb666.ru.alphabet.util.BalloonUtil.observer;
import static levspb666.ru.alphabet.util.GameUtil.alphabetHard;
import static levspb666.ru.alphabet.util.GameUtil.alphabetLight;
import static levspb666.ru.alphabet.util.GameUtil.getCroppedBitmap;
import static levspb666.ru.alphabet.util.GameUtil.getErrorText;
import static levspb666.ru.alphabet.util.GameUtil.getletter;
import static levspb666.ru.alphabet.util.GameUtil.match;
import static levspb666.ru.alphabet.util.SoundUtil.play;
import static levspb666.ru.alphabet.util.SoundUtil.poolPlayers;

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
        anim = AnimationUtils.loadAnimation(Game.this, R.anim.letter);
        activityGame = Game.this;
        goLetter(Game.this);
    }

    public void next(View view) {
        canContinue = false;
        isNextClick = true;
        next.setEnabled(false);
//        speech.stopListening();
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
        startRecord();
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
        stopRecord();
        Toast.makeText(this, "Yes", Toast.LENGTH_SHORT).show();
        play(Game.this, R.raw.yes, YES);
    }

    private void notRight() {
        Toast.makeText(this, "No", Toast.LENGTH_SHORT).show();
        play(Game.this, R.raw.no, START);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_RECORD_PERMISSION:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    speech.startListening(recognizerIntent);
//                } else {
//                    Toast.makeText(Game.this, "Permission Denied!", Toast
//                            .LENGTH_SHORT).show();
//                }
//        }
//    }

    @Override
    protected void onStop() {
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
        progressBar.setMax(8);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + Arrays.toString(buffer));
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
//        stopRecord();
    }
    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        if (canContinue && !isNextClick) {
            if (errorCode == SpeechRecognizer.ERROR_NO_MATCH) {
                stopRecord();
                returnedText.setText(errorMessage);
                start1();
            }
        } else {
            stopRecord();
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
        StringBuilder text = new StringBuilder();
        if (matches != null) {
            for (String result : matches) {
                if (!result.isEmpty()) {
                    text.append(match(result)).append("\n");
                }
            }
        }
        recording = text.toString();
        recording.replace("  ","");
        returnedText.setText(recording);
        speech.startListening(recognizerIntent);
        speech.cancel();
        verification();
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
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
//            speech.destroy();
//            speech = SpeechRecognizer.createSpeechRecognizer(this);
//            speech.setRecognitionListener(this);
//            speech.startListening(recognizerIntent);
//            speech.cancel();
            mic.setVisibility(View.INVISIBLE);
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.INVISIBLE);
//            speech.stopListening();
            Log.i(LOG_TAG, "STOP LISTENING");
        }
    }
}
