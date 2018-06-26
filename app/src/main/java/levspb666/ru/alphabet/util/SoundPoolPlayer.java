package levspb666.ru.alphabet.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;

/* SoundPoolPlayer:
   custom extention from SoundPool with setOnCompletionListener
   without the low-efficiency drawback of MediaPlayer
   author: kenliu
*/
public class SoundPoolPlayer extends SoundPool {
    private Context context;
    private int soundId;
    private int streamId;
    private int resId;
    private long duration;
    private boolean isPlaying = false;
    private boolean loaded = false;
    private MediaPlayer.OnCompletionListener listener;
    private Runnable runnable = new Runnable(){
        @Override
        public void run(){
            if(isPlaying){
                isPlaying = false;
                Log.d("debug", "ending..");
                if(listener != null){
                    listener.onCompletion(null);
                }
            }
        }
    };

    //timing related
    private Handler handler;
    private long startTime;
    private long timeSinceStart = 0;

    public void pause(){
        if(streamId > 0){
            long endTime = System.currentTimeMillis();
            timeSinceStart += endTime - startTime;
            super.pause(streamId);
            if(handler != null){
                handler.removeCallbacks(runnable);
            }
            isPlaying = false;
        }
    }

    public void stop(){
        if(streamId > 0){
            timeSinceStart = 0;
            super.stop(streamId);
            if(handler != null){
                handler.removeCallbacks(runnable);
            }
            isPlaying = false;
        }
    }

    public void play(){
        if(!loaded){
            loadAndPlay();
        }
        else{
            playIt();
        }
    }

    public static SoundPoolPlayer create(Context context, int resId){
        SoundPoolPlayer player = new SoundPoolPlayer(1, AudioManager.STREAM_MUSIC, 0);
        player.context = context;
        player.resId = resId;
        return player;
    }

    public SoundPoolPlayer(int maxStreams, int streamType, int srcQuality){
        super(3, streamType, srcQuality);
    }

    public boolean isPlaying(){
        return isPlaying;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener){
        this.listener = listener;
    }

    private void loadAndPlay(){
        duration = getSoundDuration(resId);
        soundId = super.load(context, resId, 1);
        setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            loaded = true;
            playIt();
        });
    }

    private void playIt(){
        if(loaded && !isPlaying){
            Log.d("debug", "start playing..");
            if(timeSinceStart == 0){
                streamId = super.play(soundId, 1f, 1f, 1, 0, 1f);
            }
            else{
                super.resume(streamId);
            }
            startTime = System.currentTimeMillis();
            handler = new Handler();
            handler.postDelayed(runnable, duration - timeSinceStart);
            isPlaying = true;
        }
    }

    private long getSoundDuration(int rawId){
        MediaPlayer player = MediaPlayer.create(context, rawId);
        return player.getDuration();
    }
}