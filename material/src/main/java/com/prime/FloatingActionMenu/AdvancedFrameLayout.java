package com.prime.FloatingActionMenu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdvancedFrameLayout extends FrameLayout {


    private Drawable background;
    private Paint mEraser;
    private int mLeft;
    private int mTop;
    private int mRadius;


    public AdvancedFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public AdvancedFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdvancedFrameLayout(@NonNull Context context,
                               @Nullable AttributeSet attrs,
                               int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AdvancedFrameLayout(@NonNull Context context,
                               @Nullable AttributeSet attrs,
                               int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        background = new ColorDrawable(Color.BLUE);
        mEraser = new Paint();
        mEraser.setColor(Color.TRANSPARENT);
        mEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mLeft, mTop, mRadius, mEraser);

    }



    public void setReact(float x, float y, int i) {
        mLeft = (int) x;
        mTop = (int) y;
        mRadius = i;
    }
}
