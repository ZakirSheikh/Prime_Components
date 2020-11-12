package com.prime.ViewRecycler;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prime.Utils.Utils;

public abstract class ListAdapter<T, VH extends RecyclerView.ViewHolder>
        extends androidx.recyclerview.widget.ListAdapter<T, VH> {

    private static final String TAG = "BaseAdapter";
    private boolean mAllowSwipeRight = false;
    private boolean mAllowSwipeLeft = false;
    private boolean mAllowDrag = false;
    private final ItemTouchHelper.Callback mItemTouchCallback = new ItemTouchHelper.Callback() {
        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setSelected(false);
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder,
                                      int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null)
                viewHolder.itemView.setSelected(true);
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder) {
            int
                    sFlaps =
                    mAllowSwipeLeft && acknowledgeSwipeLeft(viewHolder) ? ItemTouchHelper.START : 0;
            sFlaps |=
                    mAllowSwipeRight && acknowledgeSwipeRight(viewHolder) ? ItemTouchHelper.END : 0;
            final int dFrags = mAllowDrag ? ItemTouchHelper.UP | ItemTouchHelper.DOWN : 0;
            return makeMovementFlags(dFrags, sFlaps);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            return ListAdapter.this.onMove(viewHolder.getAdapterPosition(),
                    target.getAdapterPosition());
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            ListAdapter.this.onSwipe(viewHolder.getAdapterPosition(), direction);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c,
                                @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX,
                                float dY,
                                int actionState,
                                boolean isCurrentlyActive) {
            if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) return;
            decorate(c, viewHolder, dX);
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
    private final ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(mItemTouchCallback);
    @Nullable
    private Drawable mSwipeRightActionIcon;
    @Nullable
    private Drawable mSwipeLeftActionIcon;
    @ColorInt
    private int mSwipeLeftBackgroundColor = Color.RED;
    @ColorInt
    private int mColorOnSwipeLeftBg = Color.WHITE;
    @ColorInt
    private int mSwipeRightBackgroundColor = Color.MAGENTA;
    @ColorInt
    private int mColorOnSwipeRightBg = Color.WHITE;
    @Nullable
    private String mSwipeLeftText;
    private float mTextSize = 14;
    private Typeface mTypeface = Typeface.SANS_SERIF;
    @Nullable
    private String mSwipeRightText;
    private int mHorizontalMargin = (int) Utils.toPixels(16);

    protected ListAdapter(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
        setHasStableIds(true);
    }

    @Override
    public abstract long getItemId(int position);


    /**
     * Decorate the RecyclerView item with the chosen backgrounds and icons
     */
    private void decorate(Canvas canvas,
                          RecyclerView.ViewHolder viewHolder,
                          float dX) {
        if (dX > 0) {
            // Swiping Right
            canvas.clipRect(viewHolder.itemView.getLeft(),
                    viewHolder.itemView.getTop(),
                    viewHolder.itemView.getLeft() + (int) dX,
                    viewHolder.itemView.getBottom());
            if (mSwipeRightBackgroundColor != 0) {
                final ColorDrawable background = new ColorDrawable(mSwipeRightBackgroundColor);
                background.setBounds(viewHolder.itemView.getLeft(),
                        viewHolder.itemView.getTop(),
                        viewHolder.itemView.getLeft() + (int) dX,
                        viewHolder.itemView.getBottom());
                background.draw(canvas);
            }

            int iconSize = 0;
            if (mSwipeRightActionIcon != null && dX > mHorizontalMargin) {
                iconSize = mSwipeRightActionIcon.getIntrinsicHeight();
                int halfIcon = iconSize / 2;
                int
                        top =
                        viewHolder.itemView.getTop() +
                                ((viewHolder.itemView.getBottom() - viewHolder.itemView.getTop()) /
                                        2 - halfIcon);
                mSwipeRightActionIcon.setBounds(viewHolder.itemView.getLeft() +
                                mHorizontalMargin,
                        top,
                        viewHolder.itemView.getLeft() +
                                mHorizontalMargin +
                                mSwipeRightActionIcon.getIntrinsicWidth(),
                        top + mSwipeRightActionIcon.getIntrinsicHeight());
                if (mColorOnSwipeRightBg != 0)
                    mSwipeRightActionIcon.setColorFilter(mColorOnSwipeRightBg,
                            PorterDuff.Mode.SRC_IN);
                mSwipeRightActionIcon.draw(canvas);
            }

            if (mSwipeRightText != null &&
                    mSwipeRightText.length() > 0 &&
                    dX > mHorizontalMargin + iconSize) {
                TextPaint textPaint = new TextPaint();
                textPaint.setAntiAlias(true);
                textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                        mTextSize,
                        viewHolder.itemView.getContext().getResources().getDisplayMetrics()));
                textPaint.setColor(mColorOnSwipeRightBg);
                textPaint.setTypeface(mTypeface);

                int
                        textTop =
                        (int) (viewHolder.itemView.getTop() +
                                ((viewHolder.itemView.getBottom() - viewHolder.itemView.getTop()) /
                                        2.0) +
                                textPaint.getTextSize() / 2);
                canvas.drawText(mSwipeRightText,
                        viewHolder.itemView.getLeft() +
                                mHorizontalMargin +
                                iconSize +
                                (iconSize > 0 ? mHorizontalMargin / 2 : 0),
                        textTop,
                        textPaint);
            }

        } else if (dX < 0) {
            // Swiping Left
            canvas.clipRect(viewHolder.itemView.getRight() + (int) dX,
                    viewHolder.itemView.getTop(),
                    viewHolder.itemView.getRight(),
                    viewHolder.itemView.getBottom());
            if (mSwipeLeftBackgroundColor != 0) {
                final ColorDrawable background = new ColorDrawable(mSwipeLeftBackgroundColor);
                background.setBounds(viewHolder.itemView.getRight() + (int) dX,
                        viewHolder.itemView.getTop(),
                        viewHolder.itemView.getRight(),
                        viewHolder.itemView.getBottom());
                background.draw(canvas);
            }

            int iconSize = 0;
            int imgLeft = viewHolder.itemView.getRight();
            if (mSwipeLeftActionIcon != null && dX < -mHorizontalMargin) {
                iconSize = mSwipeLeftActionIcon.getIntrinsicHeight();
                int halfIcon = iconSize / 2;
                int
                        top =
                        viewHolder.itemView.getTop() +
                                ((viewHolder.itemView.getBottom() -
                                        viewHolder.itemView.getTop()) / 2 - halfIcon);
                imgLeft = viewHolder.itemView.getRight() - mHorizontalMargin - halfIcon * 2;
                mSwipeLeftActionIcon.setBounds(imgLeft,
                        top,
                        viewHolder.itemView.getRight() - mHorizontalMargin,
                        top + mSwipeLeftActionIcon.getIntrinsicHeight());
                if (mColorOnSwipeLeftBg != 0)
                    mSwipeLeftActionIcon.setColorFilter(mColorOnSwipeLeftBg,
                            PorterDuff.Mode.SRC_IN);
                mSwipeLeftActionIcon.draw(canvas);
            }

            if (mSwipeLeftText != null &&
                    mSwipeLeftText.length() > 0 &&
                    dX < -mHorizontalMargin - iconSize) {
                TextPaint textPaint = new TextPaint();
                textPaint.setAntiAlias(true);
                textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                        mTextSize,
                        viewHolder.itemView.getContext().getResources().getDisplayMetrics()));
                textPaint.setColor(mColorOnSwipeLeftBg);
                textPaint.setTypeface(mTypeface);

                float width = textPaint.measureText(mSwipeLeftText);
                int
                        textTop =
                        (int) (viewHolder.itemView.getTop() +
                                ((viewHolder.itemView.getBottom() - viewHolder.itemView.getTop()) /
                                        2.0) +
                                textPaint.getTextSize() / 2);
                canvas.drawText(mSwipeLeftText,
                        imgLeft -
                                width -
                                (imgLeft == viewHolder.itemView.getRight() ?
                                        mHorizontalMargin :
                                        mHorizontalMargin / 2),
                        textTop,
                        textPaint);
            }
        }
    }

    public void setSwipeBgColor(@ColorInt int left, @ColorInt int right) {
        mSwipeLeftBackgroundColor = left;
        mSwipeRightBackgroundColor = right;
    }

    public void setSwipeBgOnColor(@ColorInt int colorOnLeft, @ColorInt int colorOnRight) {
        mColorOnSwipeLeftBg = colorOnLeft;
        mColorOnSwipeRightBg = colorOnRight;
    }

    public void setSwipeActionIcon(@Nullable Drawable left, @Nullable Drawable right) {
        mSwipeLeftActionIcon = left;
        mSwipeRightActionIcon = right;
        if (mSwipeLeftActionIcon != null)
            mSwipeLeftActionIcon.mutate();
        if (mSwipeRightActionIcon != null)
            mSwipeRightActionIcon.mutate();
    }


    public void setSwipeActionText(@Nullable String left, @Nullable String right) {
        mSwipeLeftText = left;
        mSwipeRightText = right;
    }

    public void setSwipeTextProp(@NonNull Typeface typeface, float textSize) {
        mTypeface = typeface;
        mTextSize = textSize;
    }


    /**
     * Set the horizontal margin of the icon in the given unit (default is 16dp)
     *
     * @param iconHorizontalMargin the margin in the given unit
     */
    public void setIconHorizontalMargin(int iconHorizontalMargin) {
        mHorizontalMargin = (int) Utils.toPixels(iconHorizontalMargin);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (!(manager instanceof LinearLayoutManager)) {
            mAllowSwipeLeft = false;
            mAllowSwipeRight = false;
        }
    }


    protected final void startDrag(VH viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    protected boolean acknowledgeSwipeLeft(RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    protected boolean acknowledgeSwipeRight(RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    /**
     * Called when a ViewHolder is swiped by the user.
     * <p>
     * If you are returning relative directions ({@link #ItemTouchHelper.Callback.START} ,
     * {@link #END}) from the
     * {@link #ItemTouchHelper.Callback.getMovementFlags(RecyclerView, RecyclerView.ViewHolder)}
     * method, this method
     * will also use relative directions. Otherwise, it will use absolute directions.
     * <p>
     * If you don't support swiping, this method will never be called.
     * <p>
     * ItemTouchHelper will keep a reference to the View until it is detached from
     * RecyclerView.
     * As soon as it is detached, ItemTouchHelper will call
     * {@link #ItemTouchHelper.Callback.clearView(RecyclerView, RecyclerView.ViewHolder)}.
     *
     * @param position  The position swiped by the user.
     * @param direction The direction to which the ViewHolder is swiped. It is one of
     *                  {@link #UP}, {@link #DOWN},
     *                  {@link #LEFT} or {@link #RIGHT}. If your
     *                  {@link #getMovementFlags(RecyclerView, RecyclerView.ViewHolder)}
     *                  method
     *                  returned relative flags instead of {@link #LEFT} / {@link #RIGHT};
     *                  `direction` will be relative as well. ({@link #START} or {@link
     *                  #END}).
     */
    protected abstract void onSwipe(int position, int direction);

    /**
     * Called when ItemTouchHelper wants to move the dragged item from its old position to
     * the new position.
     * <p>
     * If this method returns true, ItemTouchHelper assumes {@code viewHolder} has been moved
     * to the adapter position of {@code target} ViewHolder
     * ({@link RecyclerView.ViewHolder#getAdapterPosition()
     * ViewHolder#getAdapterPosition()}).
     * <p>
     * If you don't support drag & drop, this method will never be called.
     *
     * @param fromPosition The ViewHolder which is being dragged by the user.
     * @param toPosition   The ViewHolder over which the currently active item is being dragged.
     * @return True if the {@code viewHolder} has been moved to the adapter position of
     * {@code target}.
     */
    protected abstract boolean onMove(int fromPosition, int toPosition);


    public final boolean isDragAllowed() {
        return mAllowDrag;
    }

    public final void enableDrag() {
        mAllowDrag = true;
    }

    public final void enableSwipeLeft() {
        mAllowSwipeLeft = true;
    }

    public final void enableSwipeRight() {
        mAllowSwipeRight = true;
    }
}
