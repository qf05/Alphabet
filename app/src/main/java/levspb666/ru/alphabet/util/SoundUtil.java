package levspb666.ru.alphabet.util;

import android.content.Context;
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
import static levspb666.ru.alphabet.Game.canContinue;
import static levspb666.ru.alphabet.Game.closeView;
import static levspb666.ru.alphabet.Game.goLetter;
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
                    start1();
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
        alphabetMap.put("А", R.raw.a);
        alphabetMap.put("Б", R.raw.b);
        alphabetMap.put("В", R.raw.v);
        alphabetMap.put("Г", R.raw.b);
        alphabetMap.put("Д", R.raw.a);
        alphabetMap.put("Е", R.raw.b);
        alphabetMap.put("Ё", R.raw.a);
        alphabetMap.put("Ж", R.raw.b);
        alphabetMap.put("З", R.raw.a);
        alphabetMap.put("И", R.raw.b);
        alphabetMap.put("Й", R.raw.a);
        alphabetMap.put("К", R.raw.b);
        alphabetMap.put("Л", R.raw.a);
        alphabetMap.put("М", R.raw.b);
        alphabetMap.put("Н", R.raw.a);
        alphabetMap.put("О", R.raw.b);
        alphabetMap.put("П", R.raw.a);
        alphabetMap.put("Р", R.raw.b);
        alphabetMap.put("С", R.raw.a);
        alphabetMap.put("Т", R.raw.b);
        alphabetMap.put("У", R.raw.a);
        alphabetMap.put("Ф", R.raw.b);
        alphabetMap.put("Х", R.raw.a);
        alphabetMap.put("Ц", R.raw.b);
        alphabetMap.put("Ч", R.raw.a);
        alphabetMap.put("Ш", R.raw.b);
        alphabetMap.put("Щ", R.raw.a);
        alphabetMap.put("Ъ", R.raw.b);
        alphabetMap.put("Ы", R.raw.a);
        alphabetMap.put("Ь", R.raw.b);
        alphabetMap.put("Э", R.raw.a);
        alphabetMap.put("Ю", R.raw.b);
        alphabetMap.put("Я", R.raw.v);
    }
}
