package com.star.typeevaluatortest;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

public class MyAnimView extends View {

    public static final float RADIUS = 50f;

    private PointF mCurrentPoint;

    private Paint mPaint;

    private int mColor;

    public MyAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
        mPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCurrentPoint == null) {
            mCurrentPoint = new PointF(RADIUS, RADIUS);
            startAnimation();
        } else {
            drawCircle(canvas);
        }

    }

    private void drawCircle(Canvas canvas) {
        float x = mCurrentPoint.x;
        float y = mCurrentPoint.y;
        canvas.drawCircle(x, y, RADIUS, mPaint);
    }

    private void startAnimation() {
        PointF startPoint = new PointF(RADIUS, RADIUS);
        PointF endPoint = new PointF(getWidth() - RADIUS, getHeight() - RADIUS);

        ValueAnimator valueAnimator = ValueAnimator.ofObject(new PointFEvaluator(),
                startPoint, endPoint);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentPoint = (android.graphics.PointF) animation.getAnimatedValue();
                invalidate();
            }
        });

        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(this, "color", new ColorEvaluator(),
                Color.BLUE, Color.RED);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(valueAnimator).with(objectAnimator);
        animatorSet.setDuration(5000);
        animatorSet.start();
    }

    private class PointFEvaluator implements TypeEvaluator {

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {

            PointF startPointF = (PointF) startValue;
            PointF endPointF = (PointF) endValue;

            float x = startPointF.x + fraction * (endPointF.x - startPointF.x);
            float y = startPointF.y + fraction * (endPointF.y - startPointF.y);

            return new PointF(x, y);
        }
    }

    private class ColorEvaluator implements TypeEvaluator {

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            int startColor = (int) startValue;
            int endColor = (int) endValue;

            int alpha = (int) (Color.alpha(startColor) + fraction *
                    (Color.alpha(endColor) - Color.alpha(startColor)));

            int red = (int) (Color.red(startColor) + fraction *
                    (Color.red(endColor) - Color.red(startColor)));

            int green = (int) (Color.green(startColor) + fraction *
                    (Color.green(endColor) - Color.green(startColor)));

            int blue = (int) (Color.blue(startColor) + fraction *
                    (Color.blue(endColor) - Color.blue(startColor)));

            return Color.argb(alpha, red, green, blue);
        }
    }
}
