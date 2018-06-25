package levspb666.ru.alphabet.util;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

import levspb666.ru.alphabet.Game;
import levspb666.ru.alphabet.R;

import static levspb666.ru.alphabet.Game.letter;
import static levspb666.ru.alphabet.Game.lisn;
import static levspb666.ru.alphabet.Game.start1;
import static levspb666.ru.alphabet.util.BalloonUtil.startLevel;

public class SoundUtil {

    public static volatile boolean b = false;
    public static List<MediaPlayer> mpList = new ArrayList<>();

    public static void playMusic(final Context context, final int resource, final int pause) {
//        new Thread(() -> {
            final MediaPlayer mp = MediaPlayer.create(context, resource);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setOnPreparedListener(mediaPlayer -> {
                try {
                    Thread.sleep(pause);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                mpList.add(mediaPlayer);
            });
            mp.setOnCompletionListener(mediaPlayer -> {
                mpList.remove(mediaPlayer);
                mediaPlayer.release();
                if (resource == R.raw.say){
                    start1();
                }else
                if (b){
                    if (resource == R.raw.click){
                        playMusicThis(context, 200, true);
                    }else {
                        playMusicThis(context, 100, false);
                    }
                }
            });
//        }).start();
    }

    public static void playMusicNo(final Context context1, final int resource1) {
        new Thread(() -> {
            final MediaPlayer mp = MediaPlayer.create(context1, resource1);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.start();
                mpList.add(mediaPlayer);
            });
            mp.setOnCompletionListener(mediaPlayer -> {
                mpList.remove(mediaPlayer);
                mediaPlayer.release();
                start1();
            });
        }).start();
    }

    public static void playMusicThis(final Context context2, final int pause1, boolean next) {
        int letterSound = getLetterSound(letter);
        if (letterSound != 0) {
                final MediaPlayer mp = MediaPlayer.create(context2, R.raw.thisis);
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setOnPreparedListener(mediaPlayer -> {
                    try {
                        Thread.sleep(pause1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                    mpList.add(mediaPlayer);
                });
                mp.setOnCompletionListener(mediaPlayer -> {
                    mpList.remove(mediaPlayer);
                    mediaPlayer.release();
                    if (b) {
                        b = false;
                        final MediaPlayer mpLetter = MediaPlayer.create(context2, letterSound);
                        mpLetter.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mpLetter.setOnPreparedListener(media -> {
                            media.start();
                            mpList.add(media);
                        });
                        mpLetter.setOnCompletionListener(media -> {
                            mpList.remove(media);
                            media.release();
                            if (next) {
//                                try {
//                                    Thread.sleep(500);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }

                                lisn(context2);

                            } else {
                                startLevel(context2);
                            }
                        });
                    }
                });
        }
    }


    private static int getLetterSound(String letter) {
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




    public static void playMusicThis2(final Context context2, final int pause1, boolean next) {
        b = true;
        int letterSound = getLetterSound(letter);
        if (letterSound != 0) {
            new Thread(() -> {
                try {
                    Thread.sleep(pause1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final MediaPlayer mp = MediaPlayer.create(context2, R.raw.thisis);
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.start();
                mpList.add(mp);
                try {
                    Thread.sleep(1100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (b) {
                    b = false;
                    final MediaPlayer mpLetter = MediaPlayer.create(context2, letterSound);
                    mpLetter.setAudioStreamType(AudioManager.STREAM_MUSIC);

                    mpLetter.start();
                    mpList.add(mpLetter);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mpList.remove(mpLetter);
                    mpLetter.release();
                    if (next) {

                        Intent intent = new Intent(context2, Game.class);
                        context2.startActivity(intent);

                    } else {
                        startLevel(context2);
                    }

                }
//                });
            }).start();
        }
    }
}
