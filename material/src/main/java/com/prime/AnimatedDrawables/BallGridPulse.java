package com.prime.AnimatedDrawables;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

public class BallGridPulse extends AbstractAnimatedDrawable {

    private static final int ALPHA = 255;
    private static final float SCALE = 1.0f;

    private int[] mAlphas = new int[]{ALPHA,
                                      ALPHA,
                                      ALPHA,
                                      ALPHA,
                                      ALPHA,
                                      ALPHA,
                                      ALPHA,
                                      ALPHA,
                                      ALPHA};

    private float[] mScales = new float[]{SCALE,
                                          SCALE,
                                          SCALE,
                                          SCALE,
                                          SCALE,
                                          SCALE,
                                          SCALE,
                                          SCALE,
                                          SCALE};

    @Override
    public void draw(Canvas canvas, Paint paint) {
        float spacing = 4;
        float radius = (getWidth() - spacing * 4) / 6;
        float x = getWidth() / 2 - (radius * 2 + spacing);
        float y = getWidth() / 2 - (radius * 2 + spacing);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                canvas.save();
                float translateX = x + (radius * 2) * j + spacing * j;
                float translateY = y + (radius * 2) * i + spacing * i;
                canvas.translate(translateX, translateY);
                canvas.scale(mScales[3 * i + j], mScales[3 * i + j]);
                paint.setAlpha(mAlphas[3 * i + j]);
                canvas.drawCircle(0, 0, radius, paint);
                canvas.restore();
            }
        }
    }

    @Override
    public ArrayList<ValueAnimator> onCreateAnimators() {
        ArrayList<ValueAnimator> animators = new ArrayList<>();
        int[] durations = {720, 1020, 1280, 1420, 1450, 1180, 870, 1450, 1060};
        int[] delays = {-60, 250, -170, 480, 310, 30, 460, 780, 450};

        for (int i = 0; i < 9; i++) {
            final int index = i;
            ValueAnimator scaleAnim = ValueAnimator.ofFloat(1, 0.5f, 1);
            scaleAnim.setDuration(durations[i]);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(delays[i]);
            addUpdateListener(scaleAnim, new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mScales[index] = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });

            ValueAnimator alphaAnim = ValueAnimator.ofInt(255, 210, 122, 255);
            alphaAnim.setDuration(durations[i]);
            alphaAnim.setRepeatCount(-1);
            alphaAnim.setStartDelay(delays[i]);
            addUpdateListener(alphaAnim, new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mAlphas[index] = (int) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            animators.add(scaleAnim);
            animators.add(alphaAnim);
        }
        return animators;
    }
}
