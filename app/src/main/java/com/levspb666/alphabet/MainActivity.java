package com.levspb666.alphabet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import static android.Manifest.permission.RECORD_AUDIO;
import static com.levspb666.alphabet.Action.HI;
import static com.levspb666.alphabet.Action.NOTHING;
import static com.levspb666.alphabet.Game.closeView;
import static com.levspb666.alphabet.Settings.ALPHABET_SETTINGS;
import static com.levspb666.alphabet.Settings.COLOR;
import static com.levspb666.alphabet.Settings.COUNT_BALLOONS;
import static com.levspb666.alphabet.Settings.COUNT_LETTERS;
import static com.levspb666.alphabet.Settings.EXCLUDE_LETTERS;
import static com.levspb666.alphabet.Settings.INFO_FON;
import static com.levspb666.alphabet.Settings.INFO_ON;
import static com.levspb666.alphabet.Settings.SPEED_BALLOONS;
import static com.levspb666.alphabet.Settings.USER_FON;
import static com.levspb666.alphabet.Settings.USER_FON_NAME;
import static com.levspb666.alphabet.Settings.colorLetter;
import static com.levspb666.alphabet.Settings.countBalloons;
import static com.levspb666.alphabet.Settings.countLetters;
import static com.levspb666.alphabet.Settings.drawableFon;
import static com.levspb666.alphabet.Settings.excludeLetters;
import static com.levspb666.alphabet.Settings.fon;
import static com.levspb666.alphabet.Settings.infoFon;
import static com.levspb666.alphabet.Settings.infoOn;
import static com.levspb666.alphabet.Settings.speedBalloons;
import static com.levspb666.alphabet.util.SoundUtil.play;

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
            infoOn = mSettings.getBoolean(INFO_ON, true);
        }
        if (mSettings.contains(INFO_FON)) {
            infoFon = mSettings.getBoolean(INFO_FON, false);
        }
        if (!hasPermissions()) {
            askForPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        on = findViewById(R.id.on1);
        settings = findViewById(R.id.settings);
        settings.setClickable(true);
        on.setClickable(true);
        closeView = false;
    }

    public void on(View view) {
        on.setClickable(false);
        settings.setClickable(false);
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
        on.setClickable(false);
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

    private void askForPermission() {
        String[] permissions = new String[]{RECORD_AUDIO};
        ActivityCompat.requestPermissions(this, permissions, 120);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 120) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if (permission.equals(RECORD_AUDIO)) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestDialog();
                        }
                    }
                }
            }
        } else requestDialog();
    }

    private void requestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("ВНИМАНИЕ!")
                .setMessage("ВКЛЮЧИТЕ ВСЕ НЕОБХОДИМЫЕ РАЗРЕШЕНИЯ ПРИЛОЖЕНИЯ!")
                .setCancelable(false)
                .setNegativeButton("ОК",
                        (dialog, id) -> {
                            openApplicationSettings();
                            dialog.cancel();
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, 121);
    }

    private boolean hasPermissions() {
        int permissionCheck = ActivityCompat.checkSelfPermission(this, RECORD_AUDIO);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 121:
                if (!hasPermissions()) {
                    requestDialog();
                }
        }
    }
}
