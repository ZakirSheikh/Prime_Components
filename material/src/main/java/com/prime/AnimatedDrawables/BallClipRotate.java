package com.prime.AnimatedDrawables;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;

public class BallClipRotate extends AbstractAnimatedDrawable {

    private float mScale = 1.0f;
    private float mDegrees;

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        float circleSpacing = 12;
        float x = (getWidth()) / 2;
        float y = (getHeight()) / 2;
        canvas.translate(x, y);
        canvas.scale(mScale, mScale);
        canvas.rotate(mDegrees);
        RectF
                rectF =
                new RectF(-x + circleSpacing,
                        -y + circleSpacing,
                        0 + x - circleSpacing,
                        0 + y - circleSpacing);
        canvas.drawArc(rectF, -45, 270, false, paint);
    }

    @Override
    public ArrayList<ValueAnimator> onCreateAnimators() {
        ArrayList<ValueAnimator> animators = new ArrayList<>();
        ValueAnimator scaleAnim = ValueAnimator.ofFloat(1, 0.6f, 0.5f, 1);
        scaleAnim.setDuration(750);
        scaleAnim.setRepeatCount(-1);
        addUpdateListener(scaleAnim, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mScale = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        ValueAnimator rotateAnim = ValueAnimator.ofFloat(0, 180, 360);
        rotateAnim.setDuration(750);
        rotateAnim.setRepeatCount(-1);
        addUpdateListener(rotateAnim, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDegrees = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animators.add(scaleAnim);
        animators.add(rotateAnim);
        return animators;
    }
}
