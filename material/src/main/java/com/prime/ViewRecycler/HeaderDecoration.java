package com.prime.ViewRecycler;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.prime.Utils.Utils;

public class HeaderDecoration extends RecyclerView.ItemDecoration {


    private final Drawable mHeaderBg;
    private final TextPaint mTextPaint;
    private final DecorationProvider mProvider;
    private final Rect mBounds = new Rect();
    private int mTextColor = Color.WHITE;
    private int mTextSize = 16;
    private Typeface mTypeface = Typeface.SANS_SERIF;
    private final int mHeaderMargin = (int) Utils.toPixels(16);
    private final int mHeaderHeight = (int) Utils.toPixels(45);

    public HeaderDecoration(@NonNull Drawable headerBg, @NonNull DecorationProvider provider) {
        mHeaderBg = headerBg;
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mProvider = provider;
        initPaint();
    }

    private void initPaint() {
        mTextPaint.setTextSize(Utils.toSp(mTextSize));
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTypeface(mTypeface);
    }


    public void setHeaderProp(@ColorInt int textColor, int textSize, @Nullable Typeface typeface) {
        mTextColor = textColor;
        mTextSize = textSize;
        if (typeface != null)
            mTypeface = typeface;
        initPaint();
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                               @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view);
        if (mProvider.requiresDecor(position))
            outRect.set(0, mHeaderHeight, 0, 0);
        else
            outRect.set(0, 0, 0, 0);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas,
                       @NonNull RecyclerView parent,
                       @NonNull RecyclerView.State state) {
        canvas.save();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);
            if (position != RecyclerView.NO_POSITION && mProvider.requiresDecor(position)) {
                final int left;
                final int right;
                //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
                if (parent.getClipToPadding()) {
                    left = parent.getPaddingLeft();
                    right = parent.getWidth() - parent.getPaddingRight();
                    canvas.clipRect(left, parent.getPaddingTop(), right,
                            parent.getHeight() - parent.getPaddingBottom());
                } else {
                    left = 0;
                    right = parent.getWidth();
                }
                parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
                final int
                        bottom =
                        mBounds.bottom - child.getHeight() + Math.round(child.getTranslationY());
                final int top = mBounds.top;
                mHeaderBg.setBounds(left, top, right, bottom);
                mHeaderBg.draw(canvas);
                canvas.drawText(mProvider.getDecorText(position),
                        left +
                                mHeaderMargin,
                        top + mHeaderHeight / 2 + mTextSize / 2,
                        mTextPaint);
            }
        }
        canvas.restore();
    }

    public interface DecorationProvider {
        boolean requiresDecor(int position);

        @NonNull
        String getDecorText(int position);
    }
}
