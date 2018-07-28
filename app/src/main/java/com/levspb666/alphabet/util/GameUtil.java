package com.levspb666.alphabet.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.levspb666.alphabet.Game.letter;
import static com.levspb666.alphabet.Settings.countLetters;

public class GameUtil {

    private static final int RADIUS = 20;
    private static List<String> old = new ArrayList<>();
    public static String[] alphabetLight = new String[]{"А", "Б", "В", "Г", "Д", "Е", "Ё", "Ж", "З", "И", "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Э", "Ю", "Я"};
    public static String[] alphabetHard = new String[]{"А", "Б", "В", "Г", "Д", "Е", "Ё", "Ж", "З", "И", "Й", "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Ъ", "Ы", "Ь", "Э", "Ю", "Я"};

    public static int[] mBalloonColors = new int[4];

    static {
        mBalloonColors[0] = Color.argb(255, 255, 0, 255);
        mBalloonColors[1] = Color.argb(255, 0, 255, 0);
        mBalloonColors[2] = Color.argb(255, 0, 0, 255);
        mBalloonColors[3] = Color.argb(255, 0, 255, 255);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    public static String getletter(String[] alphabet) {
        while (true) {
            int number = (int) (Math.random() * (alphabet.length));
            String letter = alphabet[number];
            if (old.size() > 0) {
                while (old.size() > 11) {
                    old.remove(0);
                }
                if (!old.contains(letter)) {
                    old.add(letter);
                    return letter;
                }
            } else {
                old.add(letter);
                return letter;
            }
        }
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap, int color) {
        int diam = RADIUS << 1;
        Bitmap targetBitmap = Bitmap.createBitmap(diam, diam, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(RADIUS, RADIUS, RADIUS, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, RADIUS, RADIUS, paint);
        return targetBitmap;
    }

    public static String match(String result) {
        StringBuilder text = new StringBuilder();
        List<String> list = Arrays.asList(result.split(" "));
        for (int i = 0; i < list.size(); i++) {
            List<Character> characters = new ArrayList<>();
            char[] chars = list.get(i).toCharArray();
            for (char c : chars) {
                if (!characters.contains(c)) {
                    characters.add(c);
                }
            }
            if (characters.size() <= countLetters) {
                text.append(list.get(i)).append(" ");
            }
            switch (letter) {
                case "Й":
                    if (countLetters < 6 && "краткое".equalsIgnoreCase(list.get(i))) {
                        text.append(list.get(i)).append(" ");
                    }
                    break;
                case "Ь":
                    if (countLetters < 6 && "мягкий".equalsIgnoreCase(list.get(i))) {
                        text.append(list.get(i)).append(" ");
                    }
                    if (countLetters < 4 && "знак".equalsIgnoreCase(list.get(i))) {
                        text.append(list.get(i)).append(" ");
                    }
                    break;
                case "Ъ":
                    if (countLetters < 7 &&
                            ("твердый".equalsIgnoreCase(list.get(i))) ||
                            "твёрдый".equalsIgnoreCase(list.get(i))) {
                        text.append(list.get(i)).append(" ");
                    }
                    if (countLetters < 4 && "знак".equalsIgnoreCase(list.get(i))) {
                        text.append(list.get(i)).append(" ");
                    }
                    break;
            }
        }
        return text.toString();
    }
}
