package levspb666.ru.alphabet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import static levspb666.ru.alphabet.Action.HI;
import static levspb666.ru.alphabet.Action.NOFING;
import static levspb666.ru.alphabet.Game.closeView;
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
                play(MainActivity.this, R.raw.click, NOFING);
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
