package com.prime.AnimatedDrawables;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.ArrayList;

public class BallRotate extends AbstractAnimatedDrawable {

    float mScale = 0.5f;

    float mDegrees;

    private Matrix mMatrix;

    public BallRotate() {
        mMatrix = new Matrix();
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        float radius = getWidth() / 10;
        float x = getWidth() / 2;
        float y = getHeight() / 2;

        /*mMatrix.preTranslate(-centerX(), -centerY());
        mMatrix.preRotate(degress,centerX(),centerY());
        mMatrix.postTranslate(centerX(), centerY());
        canvas.concat(mMatrix);*/

        canvas.rotate(mDegrees, centerX(), centerY());

        canvas.save();
        canvas.translate(x - radius * 2 - radius, y);
        canvas.scale(mScale, mScale);
        canvas.drawCircle(0, 0, radius, paint);
        canvas.restore();

        canvas.save();
        canvas.translate(x, y);
        canvas.scale(mScale, mScale);
        canvas.drawCircle(0, 0, radius, paint);
        canvas.restore();

        canvas.save();
        canvas.translate(x + radius * 2 + radius, y);
        canvas.scale(mScale, mScale);
        canvas.drawCircle(0, 0, radius, paint);
        canvas.restore();
    }

    @Override
    public ArrayList<ValueAnimator> onCreateAnimators() {
        ArrayList<ValueAnimator> animators = new ArrayList<>();
        ValueAnimator scaleAnim = ValueAnimator.ofFloat(0.5f, 1, 0.5f);
        scaleAnim.setDuration(1000);
        scaleAnim.setRepeatCount(-1);
        addUpdateListener(scaleAnim, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mScale = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        ValueAnimator rotateAnim = ValueAnimator.ofFloat(0, 180, 360);
        addUpdateListener(rotateAnim, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDegrees = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        rotateAnim.setDuration(1000);
        rotateAnim.setRepeatCount(-1);

        animators.add(scaleAnim);
        animators.add(rotateAnim);
        return animators;
    }

}
