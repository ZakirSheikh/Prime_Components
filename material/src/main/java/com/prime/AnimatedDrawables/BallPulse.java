package com.prime.AnimatedDrawables;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

public class BallPulse extends AbstractAnimatedDrawable {
    //
    public static final float SCALE = 1.0f;
    //scale x ,y
    private float[] scaleFloats = new float[]{SCALE, SCALE, SCALE};


    @Override
    public void draw(Canvas canvas, Paint paint) {
        float spacing = 4;
        float radius = (Math.min(getWidth(), getHeight()) - spacing * 2) / 6;
        float x = getWidth() / 2 - (radius * 2 + spacing);
        float y = getHeight() / 2;
        for (int i = 0; i < 3; i++) {
            canvas.save();
            float translateX = x + (radius * 2) * i + spacing * i;
            canvas.translate(translateX, y);
            canvas.scale(scaleFloats[i], scaleFloats[i]);
            canvas.drawCircle(0, 0, radius, paint);
            canvas.restore();
        }
    }

    @Override
    public ArrayList<ValueAnimator> onCreateAnimators() {
        ArrayList<ValueAnimator> animators = new ArrayList<>();
        int[] delays = new int[]{120, 240, 360};
        for (int i = 0; i < 3; i++) {
            final int index = i;
            ValueAnimator scaleAnim = ValueAnimator.ofFloat(1, 0.3f, 1);
            scaleAnim.setDuration(750);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(delays[i]);
            addUpdateListener(scaleAnim, new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    scaleFloats[index] = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            animators.add(scaleAnim);
        }
        return animators;
    }
}
