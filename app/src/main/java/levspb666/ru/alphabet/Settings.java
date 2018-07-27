package levspb666.ru.alphabet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import levspb666.ru.alphabet.util.settings.AdvColorPickerDialog;
import levspb666.ru.alphabet.util.settings.FileManager;

import static levspb666.ru.alphabet.Action.NOTHING;
import static levspb666.ru.alphabet.util.SoundUtil.play;


public class Settings extends AppCompatActivity implements AdvColorPickerDialog.OnColorChangedListener {

    public static int colorLetter = Color.RED;
    public static int countLetters = 2;
    public static boolean excludeLetters = true;
    public static boolean fon = false;
    public static int countBalloons = 15;
    public static int speedBalloons = 4;
    public static boolean infoOn = true;
    public static boolean infoFon = false;

    public static String USER_FON_PATH;
    public static final String USER_FON_NAME = "/userFon.jpg";
    public static Drawable drawableFon;

    public static final String ALPHABET_SETTINGS = "alphabetSettings";
    public static final String COLOR = "COLOR";
    public static final String COUNT_LETTERS = "COUNT_LETTERS";
    public static final String EXCLUDE_LETTERS = "EXCLUDE_LETTERS";
    public static final String USER_FON = "USER_FON";
    public static final String COUNT_BALLOONS = "COUNT_BALLOONS";
    public static final String SPEED_BALLOONS = "SPEED_BALLOONS";
    public static final String INFO_ON = "INFO_ON";
    public static final String INFO_FON = "INFO_FON";

    private SharedPreferences settings;
    private AlertDialog dialog;
    private TextView countLettersView;
    private TextView recommended;
    private CheckBox exclude;
    private ImageView imageView;
    private TextView countBalloonsView;
    private TextView speedBalloonsView;
    private CheckBox infoFonBox;
    private List<View> buttons;
    boolean screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.settings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        USER_FON_PATH = getApplicationInfo().dataDir + "/fon";
        settings = getSharedPreferences(ALPHABET_SETTINGS, MODE_PRIVATE);
        imageView = findViewById(R.id.settingsFon);
        if (fon) {
            imageView.setImageDrawable(drawableFon);
        }
        TextView t2 = (TextView) findViewById(R.id.police);
        t2.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onResume() {
        super.onResume();
        buttons = new ArrayList<>();
        buttons.add((View) findViewById(R.id.level));
        buttons.add((View) findViewById(R.id.fon));
        buttons.add((View) findViewById(R.id.color));
        buttons.add((View) findViewById(R.id.balloons));
        buttons.add((View) findViewById(R.id.info));
        buttons.add((View) findViewById(R.id.reset));
        buttons.add((View) findViewById(R.id.back));
        for (View i : buttons) {
            i.setClickable(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        buttons.clear();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
        buttons.clear();
        super.onSaveInstanceState(outState);
    }

    public void changeLevel(View view) {
        for (View i : buttons) {
            i.setClickable(false);
        }
        view.setClickable(false);
        Animation anim = AnimationUtils.loadAnimation(Settings.this, R.anim.click);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                play(Settings.this, R.raw.click, NOTHING);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                LayoutInflater li = LayoutInflater.from(Settings.this);
                View levelView = li.inflate(R.layout.level, null);
                builder.setView(levelView);
                dialog = builder.create();
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
                SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.bar);
                seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
                countLettersView = dialog.findViewById(R.id.countLettersView);
                recommended = dialog.findViewById(R.id.recommended);
                exclude = dialog.findViewById(R.id.excludeLetters);
                exclude.setChecked(excludeLetters);
                seekBar.setProgress(countLetters - 1);
                setText(countLettersView);
            }
        });
        view.startAnimation(anim);
    }

    public void excludeLetters(View view) {
        excludeLetters = exclude.isChecked();
    }

    public void okLevel(View view) {
        view.setClickable(false);
        for (View i : buttons) {
            i.setClickable(true);
        }
        Animation anim = AnimationUtils.loadAnimation(Settings.this, R.anim.click);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                play(Settings.this, R.raw.click, NOTHING);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(EXCLUDE_LETTERS, excludeLetters);
                editor.putInt(COUNT_LETTERS, countLetters);
                editor.apply();
                dialog.cancel();
                dialog = null;
            }
        });
        view.startAnimation(anim);
    }

    public void negativeButton(View view) {
        view.setClickable(false);
        for (View i : buttons) {
            i.setClickable(true);
        }
        Animation anim = AnimationUtils.loadAnimation(Settings.this, R.anim.click);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                play(Settings.this, R.raw.click, NOTHING);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dialog.cancel();
                dialog = null;
                view.setClickable(true);
            }
        });
        view.startAnimation(anim);
    }

    public void changeFon(View view) {
        for (View i : buttons) {
            i.setClickable(false);
        }
        Animation anim = AnimationUtils.loadAnimation(Settings.this, R.anim.click);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                play(Settings.this, R.raw.click, NOTHING);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                for (View i : buttons) {
                    i.setClickable(true);
                }
            }
        });
        view.startAnimation(anim);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImageUri = data.getData();
                FileManager.copyImg(selectedImageUri, getContentResolver());
                if (!fon) {
                    fon = true;
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(USER_FON, true);
                    editor.apply();
                }
                drawableFon = Drawable.createFromPath(getApplicationInfo().dataDir + "/fon" + USER_FON_NAME);
                imageView.setImageDrawable(drawableFon);
                Intent intent = new Intent(Settings.this, Settings.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public void color(View view) {
        for (View i : buttons) {
            i.setClickable(false);
        }
        Animation anim = AnimationUtils.loadAnimation(Settings.this, R.anim.click);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                play(Settings.this, R.raw.click, NOTHING);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
                screen = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
                int width = displaymetrics.widthPixels;
                int height = displaymetrics.heightPixels;
                AdvColorPickerDialog dialog = new AdvColorPickerDialog(Settings.this, Settings.this, colorLetter, height, width, screen);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
            }
        });
        view.startAnimation(anim);
    }

    @Override
    public void colorChanged(int color) {
        for (View i : buttons) {
            i.setClickable(true);
        }
        colorLetter = color;
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(COLOR, color);
        editor.apply();
    }

    private void setText(TextView view) {
        switch (view.getId()) {
            case R.id.balloonCount:
                countBalloonsView.setText(countBalloons + "");
                break;
            case R.id.balloonSpeed:
                speedBalloonsView.setText("x " + speedBalloons);
                break;
            case R.id.countLettersView:
                switch (countLetters) {
                    case 2:
                        countLettersView.setText("" + countLetters);
                        recommended.setText("(Рекомендовано)");
                        recommended.setVisibility(View.VISIBLE);
                        break;
                    case 7:
                        countLettersView.setText(DecimalFormatSymbols.getInstance().getInfinity() + "");
                        recommended.setText("(Любые слова)");
                        recommended.setVisibility(View.VISIBLE);
                        break;
                    default:
                        countLettersView.setText(countLetters + "");
                        recommended.setVisibility(View.INVISIBLE);
                }
        }
    }

    public void balloons(View view) {
        for (View i : buttons) {
            i.setClickable(false);
        }
        Animation anim = AnimationUtils.loadAnimation(Settings.this, R.anim.click);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                play(Settings.this, R.raw.click, NOTHING);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                LayoutInflater li = LayoutInflater.from(Settings.this);
                View levelView = li.inflate(R.layout.balloon_settings, null);
                builder.setView(levelView);
                dialog = builder.create();
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
                SeekBar seekBarSpeed = (SeekBar) dialog.findViewById(R.id.balloonSpeedBar);
                seekBarSpeed.setOnSeekBarChangeListener(seekBarChangeListener);
                SeekBar seekBarCount = (SeekBar) dialog.findViewById(R.id.balloonCountBar);
                seekBarCount.setOnSeekBarChangeListener(seekBarChangeListener);
                countBalloonsView = dialog.findViewById(R.id.balloonCount);
                speedBalloonsView = dialog.findViewById(R.id.balloonSpeed);
                seekBarCount.setProgress((countBalloons / 3) - 1);
                seekBarSpeed.setProgress(speedBalloons - 1);
                setText(countBalloonsView);
                setText(speedBalloonsView);
            }
        });
        view.startAnimation(anim);
    }

    public void okBalloon(View view) {
        view.setClickable(false);
        for (View i : buttons) {
            i.setClickable(true);
        }
        Animation anim = AnimationUtils.loadAnimation(Settings.this, R.anim.click);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                play(Settings.this, R.raw.click, NOTHING);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(COUNT_BALLOONS, countBalloons);
                editor.putInt(SPEED_BALLOONS, speedBalloons);
                editor.apply();
                dialog.cancel();
                dialog = null;
            }
        });
        view.startAnimation(anim);
    }

    public void info(View view) {
        for (View i : buttons) {
            i.setClickable(false);
        }
        Animation anim = AnimationUtils.loadAnimation(Settings.this, R.anim.click);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                play(Settings.this, R.raw.click, NOTHING);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                LayoutInflater li = LayoutInflater.from(Settings.this);
                View levelView = li.inflate(R.layout.info_settings, null);
                builder.setView(levelView);
                dialog = builder.create();
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
                TextView textView = dialog.findViewById(R.id.use);
                textView.setText("ВНИМАНИЕ: Для расознования речи используется \"Голосовой ввод Google\"!\n" +
                        "Для отслеживания работы голосового ввода вы можете подключить эту опцию: ");
                CheckBox infoOnBox = dialog.findViewById(R.id.infoOn);
                infoFonBox = dialog.findViewById(R.id.infoFon);
                infoOnBox.setChecked(infoOn);
                infoFonBox.setChecked(infoFon);
                hideInfoFonBox();
                infoOnBox.setOnClickListener(v -> {
                    infoOn = infoOnBox.isChecked();
                    hideInfoFonBox();
                });
                infoFonBox.setOnClickListener(v -> infoFon = infoFonBox.isChecked());
            }
        });
        view.startAnimation(anim);
    }

    private void hideInfoFonBox() {
        if (infoOn) {
            infoFonBox.setEnabled(true);
        } else {
            infoFonBox.setEnabled(false);
        }
    }

    public void okInfo(View view) {
        view.setClickable(false);
        for (View i : buttons) {
            i.setClickable(true);
        }
        Animation anim = AnimationUtils.loadAnimation(Settings.this, R.anim.click);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                play(Settings.this, R.raw.click, NOTHING);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(INFO_ON, infoOn);
                editor.putBoolean(INFO_FON, infoFon);
                editor.apply();
                dialog.cancel();
                dialog = null;
            }
        });
        view.startAnimation(anim);
    }

    public void reset(View view) {
        for (View i : buttons) {
            i.setClickable(false);
        }
        Animation anim = AnimationUtils.loadAnimation(Settings.this, R.anim.click);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                play(Settings.this, R.raw.click, NOTHING);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                LayoutInflater li = LayoutInflater.from(Settings.this);
                View levelView = li.inflate(R.layout.reset, null);
                builder.setView(levelView);
                dialog = builder.create();
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
            }
        });
        view.startAnimation(anim);
    }

    public void okReset(View view) {
        view.setClickable(false);
        for (View i : buttons) {
            i.setClickable(true);
        }
        Animation anim = AnimationUtils.loadAnimation(Settings.this, R.anim.click);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                play(Settings.this, R.raw.click, NOTHING);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                settings.edit().clear().apply();
                FileManager.deleteFile(getApplicationInfo().dataDir + "/fon" + USER_FON_NAME);
                FileManager.deleteFile(getApplicationInfo().dataDir + "/shared_prefs/" + ALPHABET_SETTINGS + ".xml");
                dialog.cancel();
                dialog = null;
                colorLetter = Color.RED;
                countLetters = 2;
                excludeLetters = true;
                fon = false;
                countBalloons = 15;
                speedBalloons = 4;
                infoOn = true;
                infoFon = false;
                Intent intent = new Intent(Settings.this, MainActivity.class);
                startActivity(intent);
            }
        });
        view.startAnimation(anim);
    }

    public void back(View view) {
        for (View i : buttons) {
            i.setClickable(false);
        }
        Animation anim = AnimationUtils.loadAnimation(Settings.this, R.anim.click);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                play(Settings.this, R.raw.click, NOTHING);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onBackPressed();
                finish();
            }
        });
        view.startAnimation(anim);
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.bar:
                    countLetters = progress + 1;
                    setText(countLettersView);
                    break;
                case R.id.balloonCountBar:
                    countBalloons = (progress + 1) * 3;
                    setText(countBalloonsView);
                    break;
                case R.id.balloonSpeedBar:
                    speedBalloons = progress + 1;
                    setText(speedBalloonsView);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
