package com.prime.AnimatedDrawables;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractAnimatedDrawable extends Drawable implements Animatable {
    //
    private static final String TAG = "Loop";

    protected Rect mBounds = new Rect();
    private HashMap<ValueAnimator, ValueAnimator.AnimatorUpdateListener>
            mUpdateListeners =
            new HashMap<>();
    private ArrayList<ValueAnimator> mAnimators;
    private int mAlpha = 255;
    private boolean mHasAnimators;
    private Paint mPaint = new Paint();

    public AbstractAnimatedDrawable() {
        //Default color is white
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    public int getColor() {
        return mPaint.getColor();
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public void setAlpha(int alpha) {
        this.mAlpha = alpha;
    }

    @Override
    public void start() {
        ensureAnimators();
        // If the animators has not ended, do nothing.
        if (mAnimators == null || isStarted()) {
            return;
        }
        startAnimators();
        invalidateSelf();
    }

    private void startAnimators() {
        for (int i = 0; i < mAnimators.size(); i++) {
            ValueAnimator animator = mAnimators.get(i);
            //when the animator restart , add the updateListener again because it
            // was removed by animator stop .
            ValueAnimator.AnimatorUpdateListener updateListener = mUpdateListeners.get(animator);
            if (updateListener != null) {
                animator.addUpdateListener(updateListener);
            }
            animator.start();
        }
    }

    private void stopAnimators() {
        if (mAnimators != null) {
            for (ValueAnimator animator : mAnimators) {
                if (animator != null && animator.isStarted()) {
                    animator.removeAllUpdateListeners();
                    animator.end();
                }
            }
        }
    }

    @Override
    public void stop() {
        stopAnimators();
    }

    @Override
    public boolean isRunning() {
        for (ValueAnimator animator : mAnimators) {
            return animator.isRunning();
        }
        return false;
    }

    @Override
    public final void draw(@NonNull Canvas canvas) {
        draw(canvas, mPaint);
    }

    public abstract void draw(Canvas canvas, Paint paint);

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        throw new UnsupportedOperationException(TAG + ": This method is currently not supported!!");
    }

    public abstract ArrayList<ValueAnimator> onCreateAnimators();

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    private void ensureAnimators() {
        if (!mHasAnimators) {
            mAnimators = onCreateAnimators();
            mHasAnimators = true;
        }
    }

    private boolean isStarted() {
        for (ValueAnimator animator : mAnimators) {
            return animator.isStarted();
        }
        return false;
    }

    /**
     * Your should use this to add AnimatorUpdateListener when
     * create animator , otherwise , animator doesn't work when
     * the animation restart .
     *
     * @param updateListener
     */
    public void addUpdateListener(ValueAnimator animator,
                                  ValueAnimator.AnimatorUpdateListener updateListener) {
        mUpdateListeners.put(animator, updateListener);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mBounds = new Rect(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    public void postInvalidate() {
        invalidateSelf();
    }

    public Rect getDrawBounds() {
        return mBounds;
    }

    public int getWidth() {
        return mBounds.width();
    }

    public int getHeight() {
        return mBounds.height();
    }

    public int centerX() {
        return mBounds.centerX();
    }

    public int centerY() {
        return mBounds.centerY();
    }

    public float exactCenterX() {
        return mBounds.exactCenterX();
    }

    public float exactCenterY() {
        return mBounds.exactCenterY();
    }
}
