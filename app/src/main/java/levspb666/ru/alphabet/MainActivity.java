package levspb666.ru.alphabet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import static levspb666.ru.alphabet.Action.HI;
import static levspb666.ru.alphabet.Action.NOTHING;
import static levspb666.ru.alphabet.Game.closeView;
import static levspb666.ru.alphabet.Settings.ALPHABET_SETTINGS;
import static levspb666.ru.alphabet.Settings.COLOR;
import static levspb666.ru.alphabet.Settings.COUNT_BALLOONS;
import static levspb666.ru.alphabet.Settings.COUNT_LETTERS;
import static levspb666.ru.alphabet.Settings.EXCLUDE_LETTERS;
import static levspb666.ru.alphabet.Settings.INFO_FON;
import static levspb666.ru.alphabet.Settings.INFO_ON;
import static levspb666.ru.alphabet.Settings.SPEED_BALLOONS;
import static levspb666.ru.alphabet.Settings.USER_FON;
import static levspb666.ru.alphabet.Settings.USER_FON_NAME;
import static levspb666.ru.alphabet.Settings.colorLetter;
import static levspb666.ru.alphabet.Settings.countBalloons;
import static levspb666.ru.alphabet.Settings.countLetters;
import static levspb666.ru.alphabet.Settings.drawableFon;
import static levspb666.ru.alphabet.Settings.excludeLetters;
import static levspb666.ru.alphabet.Settings.fon;
import static levspb666.ru.alphabet.Settings.infoFon;
import static levspb666.ru.alphabet.Settings.infoOn;
import static levspb666.ru.alphabet.Settings.speedBalloons;
import static levspb666.ru.alphabet.util.SoundUtil.play;

public class MainActivity extends AppCompatActivity {

    private Button on;
    private Button settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        SharedPreferences mSettings = getSharedPreferences(ALPHABET_SETTINGS, MODE_PRIVATE);
        if (mSettings.contains(COLOR)) {
            colorLetter = mSettings.getInt(COLOR, Color.RED);
        }
        if (mSettings.contains(COUNT_LETTERS)) {
            countLetters = mSettings.getInt(COUNT_LETTERS, 2);
        }
        if (mSettings.contains(EXCLUDE_LETTERS)) {
            excludeLetters = mSettings.getBoolean(EXCLUDE_LETTERS, true);
        }
        ImageView view = findViewById(R.id.mainFon);
        if (mSettings.contains(USER_FON)) {
            fon = mSettings.getBoolean(USER_FON, false);
            if (fon) {
                drawableFon = Drawable.createFromPath(getApplicationInfo().dataDir + "/fon" + USER_FON_NAME);
                view.setImageDrawable(drawableFon);
            } else {
                view.setImageResource(R.drawable.play2);
            }
        } else {
            view.setImageResource(R.drawable.play2);
        }
        if (mSettings.contains(SPEED_BALLOONS)) {
            speedBalloons = mSettings.getInt(SPEED_BALLOONS, 4);
        }
        if (mSettings.contains(COUNT_BALLOONS)) {
            countBalloons = mSettings.getInt(COUNT_BALLOONS, 15);
        }
        if (mSettings.contains(INFO_ON)) {
            infoOn = mSettings.getBoolean(INFO_ON, false);
        }
        if (mSettings.contains(INFO_FON)) {
            infoFon = mSettings.getBoolean(INFO_FON, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        on = findViewById(R.id.on1);
        settings = findViewById(R.id.settings);
        on.setClickable(true);
        closeView = false;
    }

    public void on(View view) {
        on.setClickable(false);
        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.click);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                closeView = false;
                play(MainActivity.this, R.raw.click, HI);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Thread(() -> {
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(MainActivity.this, Game.class);
                    startActivity(intent);
                }).start();
            }
        });
        on.startAnimation(anim);
    }

    public void settings(View view) {
        settings.setClickable(false);
        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.click);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                play(MainActivity.this, R.raw.click, NOTHING);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });
        settings.startAnimation(anim);
    }
}
