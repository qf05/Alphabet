package levspb666.ru.alphabet.util.settings;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import levspb666.ru.alphabet.R;

public class AdvColorPickerDialog extends Dialog {

    private static int width;
    private static int height;
    private static boolean screen;

    public interface OnColorChangedListener {
        void colorChanged(int color);
    }

    private OnColorChangedListener mListener;
    private int mInitialColor;

    private static class ColorPickerView extends View {
        private Paint mPaint;
        private Paint mCenterPaint;
        //------------------------------------------------------------
        private Paint mMyPaint;
        //------------------------------------------------------------

        private final int[] mColors;
        private OnColorChangedListener mListener;

        ColorPickerView(Context c, OnColorChangedListener l, int color, int img) {
            super(c);
            setBackgroundResource(img);
            mListener = l;
            mCurColor = color;
            // массив цветов для радужного кольца (задает цветовой тон)
            mColors = new int[]{
                    0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00,
                    0xFFFFFF00, 0xFFFF0000
            };
            // градиент для кольца
            Shader s = new SweepGradient(0, 0, mColors, null);

            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setShader(s);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(CENTER_RADIUS);

            mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mCenterPaint.setColor(color);
            mCenterPaint.setStrokeWidth(5);

            //------------------------------------------------------------
            R = screen ? CENTER_X - mPaint.getStrokeWidth() * 0.6f + 50 : CENTER_Y - mPaint.getStrokeWidth() * 1.3f;
            // шкала с линейным градиентом (черный - текущий цвет - белый
            // для изменения насыщенности и яркости
            mMyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Shader s1 = new LinearGradient(-R - 10, 0, R + 10, 0, new int[]{0xFF000000, mCurColor, 0xFFFFFFFF}, null, Shader.TileMode.CLAMP);
            mMyPaint.setShader(s1);
            mMyPaint.setStyle(Paint.Style.FILL);
            //------------------------------------------------------------
        }

        private boolean mTrackingCenter;
        private boolean mHighlightCenter;

        @Override
        protected void onDraw(Canvas canvas) {
            if (screen) {
                canvas.translate(width / 21 * 10, CENTER_Y - CENTER_RADIUS / 2);
            } else {
                canvas.translate(width / 4 + 50, CENTER_Y + CENTER_RADIUS / 2);
            }
            // выводим цветовое кольцо
            canvas.drawOval(new RectF(-R, -R, R, R), mPaint);
            // выводим область выбора
            canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);
            // выводим шкалу
            if (screen) {
                canvas.drawRect(new RectF(-R - 10, R + 10 + CENTER_RADIUS, R + 10, R + 160 + CENTER_RADIUS), mMyPaint);
                Shader s1 = new LinearGradient(-R - 10, 0, R + 10, 0, new int[]{0xFF000000, mCurColor, 0xFFFFFFFF}, null, Shader.TileMode.CLAMP);
                mMyPaint.setShader(s1);
            } else {
                canvas.drawRect(new RectF(R + CENTER_RADIUS * 2, -R - CENTER_RADIUS / 3, R + 150 + CENTER_RADIUS * 2, R + CENTER_RADIUS / 3), mMyPaint);
                Shader s1 = new LinearGradient(0, -R - 10, 0, R + 10, new int[]{0xFF000000, mCurColor, 0xFFFFFFFF}, null, Shader.TileMode.CLAMP);
                mMyPaint.setShader(s1);
            }

            // выбран центр ?
            if (mTrackingCenter) {
                int c = mCenterPaint.getColor();
                mCenterPaint.setStyle(Paint.Style.STROKE);

                if (mHighlightCenter) {
                    mCenterPaint.setAlpha(0xFF);
                } else {
                    mCenterPaint.setAlpha(0x80);
                }
                canvas.drawCircle(0, 0,
                        CENTER_RADIUS + mCenterPaint.getStrokeWidth(),
                        mCenterPaint);

                mCenterPaint.setStyle(Paint.Style.FILL);
                mCenterPaint.setColor(c);
            }
            invalidate();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (screen) {
                setMeasuredDimension(CENTER_X * 300 / 106, CENTER_Y * 19 / 10);
            } else {
                setMeasuredDimension(CENTER_X * 22 / 10, CENTER_Y * 21 / 10);
            }
        }

        private int CENTER_X = width / 3;
        private int CENTER_Y = height / 26 * 10;
        private int CENTER_RADIUS = screen ? height / 10 : width / 14;
        //------------------------------------------------------------
        private float R;
        //private float mHue = 0;
        private int mCurColor;
        //------------------------------------------------------------

        private int ave(int s, int d, float p) {
            return s + java.lang.Math.round(p * (d - s));
        }

        private int interpColor(int colors[], float unit) {
            if (unit <= 0) {
                return colors[0];
            }
            if (unit >= 1) {
                return colors[colors.length - 1];
            }

            float p = unit * (colors.length - 1);
            int i = (int) p;
            p -= i;

            // now p is just the fractional part [0...1) and i is the index
            int c0 = colors[i];
            int c1 = colors[i + 1];
            int a = ave(Color.alpha(c0), Color.alpha(c1), p);
            int r = ave(Color.red(c0), Color.red(c1), p);
            int g = ave(Color.green(c0), Color.green(c1), p);
            int b = ave(Color.blue(c0), Color.blue(c1), p);

            return Color.argb(a, r, g, b);
        }

        private static final float PI = 3.1415926f;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // получаем координаты нажатия
            float x = screen ? event.getX() - width / 21 * 10 : event.getX() - width / 4 - 50;
            float y = screen ? event.getY() - CENTER_Y + CENTER_RADIUS / 2 : event.getY() - CENTER_Y - CENTER_RADIUS / 2;

            // расстояние от центра до точки нажатия
            double hypotenuse = java.lang.Math.sqrt(x * x + y * y);

            // устаналиваем соответствующий флаг в зависимости от зоны нажатия
            // в центре (область выбора)
            boolean inCenter = hypotenuse <= CENTER_RADIUS;
            // радужное колесо
            boolean inMainSelect = (hypotenuse > CENTER_RADIUS) && (hypotenuse <= R + CENTER_RADIUS / 2);
            // нижняя шкала
            boolean inAdvSelect;
            if (screen) {
                inAdvSelect = ((x >= -R - 10) && (x <= R + 10)) && ((y >= (R + 10 + CENTER_RADIUS)) && (y <= (R + 160 + CENTER_RADIUS)));
            } else {
                inAdvSelect = ((x >= R + CENTER_RADIUS * 2) && (x <= R + 150 + CENTER_RADIUS * 2)) && ((y >= -R - CENTER_RADIUS / 3) && (y <= R + CENTER_RADIUS / 3));
            }
            // вызываем перерисовку. без этого нижняя шкала почему-то не перерисовывается после выбора цвета на колесе
            invalidate();
            // обработка событий
            switch (event.getAction()) {
                // касание
                case MotionEvent.ACTION_DOWN:
                    mTrackingCenter = inCenter;
                    // если в центре - подсвечиваем центр
                    if (inCenter) {
                        mHighlightCenter = true;
                        invalidate();
                        break;
                    }
                    // движение
                case MotionEvent.ACTION_MOVE:
                    if (mTrackingCenter) {
                        if (mHighlightCenter != inCenter) {
                            mHighlightCenter = inCenter;
                        }
                    }
                    // в колесе
                    else if (inMainSelect) {
                        // вычисляем цвет
                        float angle = (float) java.lang.Math.atan2(y, x);
                        // need to turn angle [-PI ... PI] into unit [0....1]
                        float unit = angle / (2 * PI);
                        if (unit < 0) {
                            unit += 1;
                        }
                        // запоминаем его и устанавливаем для центрального круга
                        mCurColor = interpColor(mColors, unit);
                        mCenterPaint.setColor(mCurColor);
                    }
                    // на шкале
                    else if (inAdvSelect) {
                        float val;
                        // преобразуем текущий цвет центра в HSV
                        float[] hsv = color2HSV(mCenterPaint.getColor());
                        float c = screen ? x : y;
                        // в левой части шкалы (между черным и выбранным цветом)
                        if (c <= 0) {
                            // вычисляем и меняем значение яркости, тон не трогаем, насыщенность - 1
                            val = Math.abs(R + 10 + c) / (R + 10);
                            // преобразовываем из HSV в RGB и устанавливаем полученный цвет для центра
                            mCenterPaint.setColor(Color.HSVToColor(0xFF, new float[]{hsv[0], 1, val}));
                        }
                        // в правой части шкалы (между цветом и белым)
                        else {
                            // вычисляем и меняем значение насыщенности, тон не трогаем, яркость - 1
                            val = 1 - Math.abs(c) / (R + 10);
                            // преобразовываем из HSV в RGB и устанавливаем полученный цвет для центра
                            mCenterPaint.setColor(Color.HSVToColor(0xFF, new float[]{hsv[0], val, 1}));
                        }
                    }
                    // перерисовываем
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    if (mTrackingCenter) {
                        if (inCenter) {
                            mListener.colorChanged(mCenterPaint.getColor());
                        }
                        mTrackingCenter = false;    // so we draw w/o halo
                        invalidate();
                    }
                    break;
            }
            return true;
        }
    }

    static float[] color2HSV(int color) {
        float[] hsv = new float[3];

        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        Color.RGBToHSV(red, green, blue, hsv);

        return hsv;
    }

    public AdvColorPickerDialog(Context context,
                                OnColorChangedListener listener,
                                int initialColor,
                                int height,
                                int width,
                                boolean screen) {
        super(context);
        AdvColorPickerDialog.screen = screen;
        AdvColorPickerDialog.width = width;
        AdvColorPickerDialog.height = height;
        mListener = listener;
        mInitialColor = initialColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnColorChangedListener l = color -> {
            mListener.colorChanged(color);
            dismiss();
        };
        int img = R.drawable.dialog_fon1;
        setContentView(new ColorPickerView(getContext(), l, mInitialColor, img));
        TextView tv = new TextView(getContext());
        tv.setText("ВЫБОР ЦВЕТА БУКВ");
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setPadding(0, 70, 0, 0);
        tv.setTextSize(20);
        tv.setTypeface(null, Typeface.BOLD);
        addContentView(tv, (new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT)));
    }
}