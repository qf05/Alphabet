package levspb666.ru.alphabet.util;

import android.content.Context;
import android.os.AsyncTask;
import android.view.ViewTreeObserver;

import java.util.Random;

import levspb666.ru.alphabet.Balloon;

import static levspb666.ru.alphabet.Game.mBalloons;
import static levspb666.ru.alphabet.Game.mContentView;
import static levspb666.ru.alphabet.util.GameUtil.mBalloonColors;

public class BalloonUtil {

    private static final int MIN_ANIMATION_DELAY = 500;
    private static final int MAX_ANIMATION_DELAY = 2500;
    private static final int MIN_ANIMATION_DURATION = 1000;
    private static final int MAX_ANIMATION_DURATION = 8000;
    public static final int BALLOONS_PER_LEVEL = 15;
    private static int mNextColor;
    private static int mScreenHeight;
    private static int mScreenWidth;

    public static void observer() {
        ViewTreeObserver viewTreeObserver = mContentView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScreenHeight = mContentView.getHeight();
                    mScreenWidth = mContentView.getWidth();
                }
            });
        }
    }

    public static void startLevel(Context context) {
        BalloonUtil.BalloonLauncher balloonLauncher = new BalloonUtil.BalloonLauncher(context);
        balloonLauncher.execute(BALLOONS_PER_LEVEL);
    }

    public static class BalloonLauncher extends AsyncTask<Integer, Integer, Void> {

        private Context context;
        BalloonLauncher(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            if (params.length != 1) {
                throw new AssertionError(
                        "Expected 1 param for current level");
            }

            int level = params[0];
            int maxDelay = Math.max(MIN_ANIMATION_DELAY,
                    (MAX_ANIMATION_DELAY - ((level - 1) * 300)));
            int minDelay = maxDelay / 2;

            int balloonsLaunched = 0;
            while (balloonsLaunched < BALLOONS_PER_LEVEL) {

//              Get a random horizontal position for the next balloon
                Random random = new Random();
                int xPosition = random.nextInt(mScreenWidth - 200);
                publishProgress(xPosition);
                balloonsLaunched++;

//              Wait a random number of milliseconds before looping
                int delay = random.nextInt(minDelay) + minDelay;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int xPosition = values[0];
            launchBalloon(xPosition, context);
        }

    }

    private static void launchBalloon(int x, Context context) {

        Balloon balloon = new Balloon(context, mBalloonColors[mNextColor], 150);
        mBalloons.add(balloon);

        if (mNextColor + 1 == mBalloonColors.length) {
            mNextColor = 0;
        } else {
            mNextColor++;
        }

//      Set balloon vertical position and dimensions, add to container
        balloon.setX(x);
        balloon.setY(mScreenHeight + balloon.getHeight());
        mContentView.addView(balloon);

//      Let 'er fly
        int duration = Math.max(MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION - (1000));
        balloon.releaseBalloon(mScreenHeight, duration);

    }
}
