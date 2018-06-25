package levspb666.ru.alphabet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
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
import android.widget.ToggleButton;

import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static levspb666.ru.alphabet.util.BalloonUtil.*;
import static levspb666.ru.alphabet.util.GameUtil.*;
import static levspb666.ru.alphabet.util.SoundUtil.*;

public class Game extends AppCompatActivity implements
        RecognitionListener, Balloon.BalloonListener {

    private static final int NUM_PARTICLES = 30;
    private static final int REQUEST_RECORD_PERMISSION = 100;

    public static List<Balloon> mBalloons = new ArrayList<>();
    public static ViewGroup mContentView;
    private int mBalloonsPopped;

    private TextView returnedText;
    private static ToggleButton toggleButton;
    private ProgressBar progressBar;
    private static SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    public static Button next;
    public static String letter;
    private static TextView ext;
    private static Animation anim;
    private static String recording = "";
    private static String LOG_TAG = "GAME";
    private ImageView mic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.game);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }
        mContentView = findViewById(R.id.speachId);
        mic = findViewById(R.id.mic);
        next = findViewById(R.id.next);
        returnedText = findViewById(R.id.textView1);
        progressBar = findViewById(R.id.progressBar1);
        toggleButton = findViewById(R.id.toggleButton1);
        ext = findViewById(R.id.letter);

        observer();
        progressBar.setVisibility(View.INVISIBLE);

        next.setClickable(false);
        mic.setVisibility(View.INVISIBLE);
        toggleButton.setVisibility(View.INVISIBLE);
        toggleButton.setChecked(false);

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        anim = AnimationUtils.loadAnimation(Game.this, R.anim.letter);

        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mic.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
                ActivityCompat.requestPermissions
                        (Game.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                REQUEST_RECORD_PERMISSION);
            } else {
                mic.setVisibility(View.INVISIBLE);
                progressBar.setIndeterminate(false);
                progressBar.setVisibility(View.INVISIBLE);
                speech.stopListening();
            }
        });
        lisn(Game.this);
    }

    public void next(View view) {
        next.setClickable(false);
        toggleButton.setChecked(false);
        Animation anim = AnimationUtils.loadAnimation(Game.this, R.anim.click);
        next.startAnimation(anim);
        b=true;
        playMusic(Game.this, R.raw.click, 90);
    }

    public static void lisn(Context context) {
        b = false;
        letter = getletter(alphabatLight);
        ext.setText(letter);
        anim.setStartOffset(1000);
        ext.startAnimation(anim);
        playMusic(context, R.raw.say, 700);
    }

    public static void start1() {
        if(!b) {
            recording = "";
            Log.i(LOG_TAG, "start");
            toggleButton.setChecked(true);
            next.setVisibility(View.VISIBLE);
            next.setClickable(true);
        }
    }


    private void right() {
        toggleButton.setChecked(false);
        if (recording != null && !recording.isEmpty() && recording.length() > 0) {
            recording = recording.toUpperCase();
            if (recording.contains(letter)) {
                toggleButton.setChecked(false);
                Toast.makeText(this, "Yes", Toast.LENGTH_SHORT).show();
                next.setVisibility(View.INVISIBLE);
                next.setClickable(false);
                b = true;
                playMusic(Game.this, R.raw.yes, 0);
            } else {
                Toast.makeText(this, "No", Toast.LENGTH_SHORT).show();
                playMusicNo(Game.this, R.raw.no);
            }
        } else {
            Toast.makeText(this, "No", Toast.LENGTH_SHORT).show();
            start1();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(Game.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onStop() {
//        b = false;
        toggleButton.setChecked(false);
//        next.setClickable(false);
//        next.setVisibility(View.INVISIBLE);
        if (speech != null) {
            speech.cancel();
            speech.destroy();
        }
        if (mpList!=null&&!mpList.isEmpty()){
            for (int i = 0; i < mpList.size(); i++) {
                mpList.get(i).stop();
                mpList.get(i).release();
            }
        }
        super.onStop();
        finish();
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + Arrays.toString(buffer));
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
        toggleButton.setChecked(false);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        toggleButton.setChecked(false);
        if (errorCode == SpeechRecognizer.ERROR_NO_MATCH) {
            start1();
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
        StringBuilder text = new StringBuilder();
        if (matches != null) {
            for (String result : matches) {
                text.append(match(result)).append("\n");
            }
        }
        recording = text.toString();
        returnedText.setText(recording);
        right();
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }

    @Override
    public void popBalloon(Balloon balloon, boolean userTouch) {
        if (userTouch) {
            playMusic(Game.this, R.raw.touch, 0);
            Bitmap bitmap = Bitmap.createBitmap(1, 1,
                    Bitmap.Config.ARGB_8888);
            bitmap = getCroppedBitmap(bitmap, balloon.getColor());
            new ParticleSystem(this, NUM_PARTICLES, bitmap, 500)
                    .setSpeedRange(0.1f, 0.5f)
                    .oneShot(balloon, NUM_PARTICLES);
        }
        mBalloonsPopped++;
        mContentView.removeView(balloon);
        mBalloons.remove(balloon);
        if (mBalloonsPopped == BALLOONS_PER_LEVEL) {
            new Thread(() -> {
                try {
                    Thread.sleep(900);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(Game.this, Game.class);
                startActivity(intent);
                finish();
            }).start();
        }
    }
}
