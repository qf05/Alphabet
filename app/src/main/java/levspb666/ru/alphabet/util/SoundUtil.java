package levspb666.ru.alphabet.util;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import levspb666.ru.alphabet.Action;
import levspb666.ru.alphabet.R;

import static levspb666.ru.alphabet.Action.BALLOON;
import static levspb666.ru.alphabet.Action.LETTER;
import static levspb666.ru.alphabet.Action.NEXT;
import static levspb666.ru.alphabet.Action.NOTHING;
import static levspb666.ru.alphabet.Game.audioManager;
import static levspb666.ru.alphabet.Game.canContinue;
import static levspb666.ru.alphabet.Game.closeView;
import static levspb666.ru.alphabet.Game.goLetter;
import static levspb666.ru.alphabet.Game.isNextClick;
import static levspb666.ru.alphabet.Game.letter;
import static levspb666.ru.alphabet.Game.start1;
import static levspb666.ru.alphabet.util.BalloonUtil.startLevel;
import static levspb666.ru.alphabet.util.SoundPoolPlayer.create;

public class SoundUtil {

    public static List<SoundPoolPlayer> poolPlayers = new ArrayList<>();

    public static void play(Context context, int resId, Action action) {
        SoundPoolPlayer mPlayer = create(context, resId);
        poolPlayers.add(mPlayer);
        mPlayer.setOnCompletionListener(
                mp -> {    //mp will be null here
                    Log.d("debug", "voice completed");
                    poolPlayers.remove(mPlayer);
                    mPlayer.stop();
                    mPlayer.release();
                    actionWhenComplete(context, action);
                }
        );
        mPlayer.play();
    }

    private static void actionWhenComplete(Context context, Action action) {
        if (!closeView) {
            switch (action) {
                case HI:
                    play(context, R.raw.hi, NOTHING);
                    break;
                case YES:
                    play(context, R.raw.thisis, LETTER);
                    break;
                case LETTER:
                    if (canContinue) {
                        play(context, alphabetMap.get(letter), BALLOON);
                    } else {
                        play(context, alphabetMap.get(letter), NEXT);
                    }
                    break;
                case NEXT:
                    goLetter(context);
                    break;
                case START:
                    if (!isNextClick) {
                        start1();
                    }
                    break;
                case BALLOON:
                    startLevel(context);
                    break;
                case NOTHING:
                    break;
            }
        }
    }

    private static Map<String, Integer> alphabetMap = new HashMap<>();

    static {
        alphabetMap.put("А", R.raw.a1);
        alphabetMap.put("Б", R.raw.a2);
        alphabetMap.put("В", R.raw.a3);
        alphabetMap.put("Г", R.raw.a4);
        alphabetMap.put("Д", R.raw.a5);
        alphabetMap.put("Е", R.raw.a6);
        alphabetMap.put("Ё", R.raw.a7);
        alphabetMap.put("Ж", R.raw.a8);
        alphabetMap.put("З", R.raw.a9);
        alphabetMap.put("И", R.raw.a1);
        alphabetMap.put("Й", R.raw.a11);
        alphabetMap.put("К", R.raw.a12);
        alphabetMap.put("Л", R.raw.a13);
        alphabetMap.put("М", R.raw.a14);
        alphabetMap.put("Н", R.raw.a15);
        alphabetMap.put("О", R.raw.a16);
        alphabetMap.put("П", R.raw.a17);
        alphabetMap.put("Р", R.raw.a18);
        alphabetMap.put("С", R.raw.a19);
        alphabetMap.put("Т", R.raw.a20);
        alphabetMap.put("У", R.raw.a2);
        alphabetMap.put("Ф", R.raw.a22);
        alphabetMap.put("Х", R.raw.a23);
        alphabetMap.put("Ц", R.raw.a24);
        alphabetMap.put("Ч", R.raw.a25);
        alphabetMap.put("Ш", R.raw.a26);
        alphabetMap.put("Щ", R.raw.a27);
        alphabetMap.put("Ъ", R.raw.a28);
        alphabetMap.put("Ы", R.raw.a29);
        alphabetMap.put("Ь", R.raw.a30);
        alphabetMap.put("Э", R.raw.a31);
        alphabetMap.put("Ю", R.raw.a32);
        alphabetMap.put("Я", R.raw.a33);
    }


    public static void muteAudio(Boolean mute) {
        try {
            // mute (or) un mute audio based on status
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, mute ? AudioManager.ADJUST_MUTE : AudioManager.ADJUST_UNMUTE, 0);
            } else {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, mute);
            }
        } catch (Exception e) {
            Log.e("MUTE", e.getMessage());
            if (audioManager == null) return;

            // un mute the audio if there is an exception
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
            } else {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            }
        }
    }
}
