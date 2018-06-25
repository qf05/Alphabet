package levspb666.ru.alphabet.util;


import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import levspb666.ru.alphabet.Action;
import levspb666.ru.alphabet.R;

import static levspb666.ru.alphabet.Action.LETTER;
import static levspb666.ru.alphabet.Action.NEXT;
import static levspb666.ru.alphabet.Action.NOFING;
import static levspb666.ru.alphabet.Game.letter;
import static levspb666.ru.alphabet.Game.lisn;
import static levspb666.ru.alphabet.util.BalloonUtil.startLevel;
import static levspb666.ru.alphabet.util.SoundPoolPlayer.*;

public class SoundUtils {

    public static AtomicBoolean next = new AtomicBoolean(false);

    public static List<SoundPoolPlayer> poolPlayers = new ArrayList<>();

    public static void play(Context context, int resId, Action action){
        SoundPoolPlayer mPlayer = create(context, resId);
        poolPlayers.add(mPlayer);
        mPlayer.setOnCompletionListener(
                mp -> {    //mp will be null here
                    Log.d("debug", "completed");
                    poolPlayers.remove(mPlayer);
                    actionWhenComlet(context, action, mp);
                }
        );
        mPlayer.play();
    }

    private static void actionWhenComlet(Context context, Action action, MediaPlayer mp){
        if (mp!=null) {
            mp.release();
        }
        switch (action){
            case NO: play(context, R.raw.no,NOFING);break;
            case HI: play(context,R.raw.hi,NOFING); break;
            case YES: play(context, R.raw.thisis,LETTER); break;
            case LETTER: play(context,getLetterSound(),NEXT); break;
            case NEXT: if (next.get()){
                lisn(context);
            }else {
                startLevel(context);
            }
            break;
            case NOFING: break;
        }
    }

    private static int getLetterSound() {
        switch (letter) {
            case ("А"):
                return R.raw.a;
            case ("Б"):
                return R.raw.b;
            case ("В"):
                return R.raw.v;
            default:
                return R.raw.b;
        }
    }
}
