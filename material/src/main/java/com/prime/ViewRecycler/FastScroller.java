package com.prime.ViewRecycler;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prime.R;
import com.prime.Utils.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Class responsible to animate and provide a fast scroller.
 */
class FastScroller extends RecyclerView.ItemDecoration implements RecyclerView.OnItemTouchListener {
    // Scroll thumb not showing
    private static final int STATE_HIDDEN = 0;
    // Scroll thumb visible and moving along with the scrollbar
    private static final int STATE_VISIBLE = 1;
    // Scroll thumb being dragged by user
    private static final int STATE_DRAGGING = 2;
    private static final int DRAG_NONE = 0;
    private static final int DRAG_X = 1;
    private static final int DRAG_Y = 2;
    private static final int ANIMATION_STATE_OUT = 0;
    private static final int ANIMATION_STATE_FADING_IN = 1;
    private static final int ANIMATION_STATE_IN = 2;
    private static final int ANIMATION_STATE_FADING_OUT = 3;
    private static final int SHOW_DURATION_MS = 500;
    private static final int HIDE_DELAY_AFTER_VISIBLE_MS = 1500;
    private static final int HIDE_DELAY_AFTER_DRAGGING_MS = 1200;
    private static final int HIDE_DURATION_MS = 500;
    private static final int SCROLLBAR_FULL_OPAQUE = 255;
    private static final int[] PRESSED_STATE_SET = new int[]{android.R.attr.state_pressed};
    private static final int[] EMPTY_STATE_SET = new int[]{};
    // Final values for the vertical scroll bar
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    final StateListDrawable mVerticalThumbDrawable;
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    final Drawable mVerticalTrackDrawable;
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    final ValueAnimator mShowHideAnimator = ValueAnimator.ofFloat(0, 1);
    private final int mScrollbarMinimumRange;
    private final int mMargin;

    private final int mAutoHideDelayMS;
    private final int mVerticalThumbWidth;
    private final int mVerticalTrackWidth;
    // Final values for the horizontal scroll bar
    private final StateListDrawable mHorizontalThumbDrawable;
    private final Drawable mHorizontalTrackDrawable;
    private final int mHorizontalThumbHeight;
    private final int mHorizontalTrackHeight;
    private final int[] mVerticalRange = new int[2];
    private final int[] mHorizontalRange = new int[2];
    private final Drawable mPopupDrawable;
    private final int mPopupTextColor;
    private final int mPopupTextSize;
    @PopupPosition
    private final int mPopupPosition;
    private final int mVerticalThumbHeightMin;
    private final int mHorizontalThumbWidthMin;
    // Dynamic values for the vertical scroll bar
    @VisibleForTesting
    int mVerticalThumbHeight;
    @VisibleForTesting
    int mVerticalThumbCenterY;
    @VisibleForTesting
    float mVerticalDragY;
    // Dynamic values for the horizontal scroll bar
    @VisibleForTesting
    int mHorizontalThumbWidth;
    @VisibleForTesting
    int mHorizontalThumbCenterX;
    @VisibleForTesting
    float mHorizontalDragX;
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    @AnimationState
    int mAnimationState = ANIMATION_STATE_OUT;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide(HIDE_DURATION_MS);
        }
    };
    private int mRecyclerViewWidth = 0;
    private int mRecyclerViewHeight = 0;
    private RecyclerView mRecyclerView;
    /**
     * Whether the document is long/wide enough to require scrolling. If not, we don't show the
     * relevant scroller.
     */
    private boolean mNeedVerticalScrollbar = false;
    private boolean mNeedHorizontalScrollbar = false;
    @State
    private int mState = STATE_HIDDEN;
    @DragState
    private int mDragState = DRAG_NONE;
    private boolean mFixedSize;
    private String mPopupTitle;
    private Paint mTextPaint;
    private Rect mTextBounds = new Rect();
    private int mPopupToThumbSpace = (int) Utils.toPixels(10);
    private int mPrevPosition;
    private final RecyclerView.OnScrollListener
            mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            updateScrollPosition(recyclerView.computeHorizontalScrollOffset(),
                    recyclerView.computeVerticalScrollOffset());
            int pos = findFirstVisibleItemPosition();
            if (pos != -1 && pos != mPrevPosition) {
                mPrevPosition = pos;
                if (recyclerView.getAdapter() instanceof PopupTitleProvider)
                    mPopupTitle
                            = ((PopupTitleProvider) recyclerView.getAdapter()).getPopupTitle(pos);
            }
        }
    };

    FastScroller(Context context, ViewRecycler recyclerView, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewRecycler
                , 0
                , 0);
        Resources resources = context.getResources();
        boolean autoHideScrollerBars;
        try {
            StateListDrawable stateListPlaceHolder;
            stateListPlaceHolder
                    =
                    (StateListDrawable) a.getDrawable(R.styleable.ViewRecycler_verticalThumbDrawable);
            if (stateListPlaceHolder == null) {
                stateListPlaceHolder
                        =
                        (StateListDrawable) resources.getDrawable(R.drawable.invisible_state_list_placeholder);
            }
            mVerticalThumbDrawable = stateListPlaceHolder;
            stateListPlaceHolder
                    =
                    (StateListDrawable) a.getDrawable(R.styleable.ViewRecycler_horizontalThumbDrawable);
            if (stateListPlaceHolder == null)
                stateListPlaceHolder
                        =
                        (StateListDrawable) resources.getDrawable(R.drawable.invisible_state_list_placeholder);
            mHorizontalThumbDrawable = stateListPlaceHolder;
            //Track PlaceHolder
            Drawable placeHolder;
            placeHolder = a.getDrawable(R.styleable.ViewRecycler_verticalTrackDrawable);
            if (placeHolder == null)
                placeHolder = resources.getDrawable(R.drawable.invisible_place_holder);
            mVerticalTrackDrawable = placeHolder;
            placeHolder = a.getDrawable(R.styleable.ViewRecycler_horizontalTrackDrawable);
            if (placeHolder == null)
                placeHolder = resources.getDrawable(R.drawable.invisible_place_holder);
            mHorizontalTrackDrawable = placeHolder;
            //Popup
            mPopupDrawable = a.getDrawable(R.styleable.ViewRecycler_popupDrawable);
            mPopupPosition =
                    a.getInt(R.styleable.ViewRecycler_popupPosition, PopupPosition.ADJACENT_CENTER);
            mPopupTextColor = a.getColor(R.styleable.ViewRecycler_popupTextColor, 0xffffffff);
            mPopupTextSize = a.getDimensionPixelSize(R.styleable.ViewRecycler_popupTextSize,
                    Utils.toSp(22));
            mFixedSize = a.getBoolean(R.styleable.ViewRecycler_fixedThumbSize, false);
            autoHideScrollerBars = a.getBoolean(R.styleable.ViewRecycler_autoHideScrollbars,
                    true);
            mAutoHideDelayMS = a.getInteger(R.styleable.ViewRecycler_autoHideDelayMS,
                    HIDE_DELAY_AFTER_VISIBLE_MS);
        } finally {
            a.recycle();
        }
        int defaultWidth = resources.getDimensionPixelSize(R.dimen.fastscroller_default_thickness);
        mScrollbarMinimumRange
                = resources.getDimensionPixelSize(R.dimen.fastscroller_minimum_range);
        int defaultThumbSize = resources.getDimensionPixelSize(R.dimen.fastScroller_min_size);
        mVerticalThumbHeightMin = mVerticalThumbDrawable.getIntrinsicHeight() == -1
                ? defaultThumbSize : mVerticalThumbDrawable.getIntrinsicHeight();
        mHorizontalThumbWidthMin = mHorizontalThumbDrawable.getIntrinsicWidth() == -1
                ? (int) Utils.toPixels(5)
                : mHorizontalThumbDrawable.getIntrinsicWidth();
        mVerticalThumbWidth = mVerticalThumbDrawable.getIntrinsicWidth() == -1
                ? defaultWidth : mVerticalThumbDrawable.getIntrinsicWidth();
        mMargin = resources.getDimensionPixelOffset(R.dimen.fastscroller_margin);
        mVerticalTrackWidth = mVerticalTrackDrawable.getIntrinsicWidth() == -1
                ? defaultWidth : mVerticalTrackDrawable.getIntrinsicWidth();
        mHorizontalThumbHeight = mHorizontalThumbDrawable.getIntrinsicHeight() == -1
                ? defaultWidth : mHorizontalThumbDrawable.getIntrinsicHeight();
        mHorizontalTrackHeight = mHorizontalTrackDrawable.getIntrinsicWidth() == -1
                ? (int) Utils.toPixels(5) : mHorizontalTrackDrawable.getIntrinsicWidth();
        mVerticalThumbDrawable.setAlpha(SCROLLBAR_FULL_OPAQUE);
        mVerticalTrackDrawable.setAlpha(SCROLLBAR_FULL_OPAQUE);
        if (autoHideScrollerBars) {
            mShowHideAnimator.addListener(new AnimatorListener());
            mShowHideAnimator.addUpdateListener(new AnimatorUpdater());
        }
        if (mPopupDrawable != null)
            initPopup();
        attachToRecyclerView(recyclerView);
    }

    /**
     * Returns the position currently visible in {@link RecyclerView}
     *
     * @return position
     */
    private int findFirstVisibleItemPosition() {
        RecyclerView.LayoutManager manager = mRecyclerView.getLayoutManager();
        if (manager instanceof LinearLayoutManager)
            return ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
        else if (manager instanceof GridLayoutManager)
            return ((GridLayoutManager) manager).findFirstVisibleItemPosition();
        else
            return -1;
    }

    private void initPopup() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mPopupTextColor);
        mTextPaint.setAlpha(255);
        mTextPaint.setTextSize(mPopupTextSize);
    }

    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        if (mRecyclerView == recyclerView) {
            return; // nothing to do
        }
        if (mRecyclerView != null) {
            destroyCallbacks();
        }
        mRecyclerView = recyclerView;
        if (mRecyclerView != null) {
            setupCallbacks();
        }
    }

    private void setupCallbacks() {
        mRecyclerView.addItemDecoration(this);
        mRecyclerView.addOnItemTouchListener(this);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    private void destroyCallbacks() {
        mRecyclerView.removeItemDecoration(this);
        mRecyclerView.removeOnItemTouchListener(this);
        mRecyclerView.removeOnScrollListener(mOnScrollListener);
        cancelHide();
    }

    @SuppressWarnings("WeakerAccess") /* synthetic access */
    void requestRedraw() {
        mRecyclerView.invalidate();
    }

    void setState(@State int state) {
        if (state == STATE_DRAGGING && mState != STATE_DRAGGING) {
            if (mNeedVerticalScrollbar)
                mVerticalThumbDrawable.setState(PRESSED_STATE_SET);
            if (mNeedHorizontalScrollbar)
                mHorizontalThumbDrawable.setState(PRESSED_STATE_SET);
            cancelHide();
        }

        if (state == STATE_HIDDEN) {
            requestRedraw();
        } else {
            show();
        }

        if (mState == STATE_DRAGGING && state != STATE_DRAGGING) {
            if (mNeedVerticalScrollbar)
                mVerticalThumbDrawable.setState(EMPTY_STATE_SET);
            if (mNeedHorizontalScrollbar)
                mHorizontalThumbDrawable.setState(EMPTY_STATE_SET);
            //mVerticalThumbDrawable.setState(EMPTY_STATE_SET);
            resetHideDelay(HIDE_DELAY_AFTER_DRAGGING_MS);
        } else if (state == STATE_VISIBLE) {
            resetHideDelay(mAutoHideDelayMS);
        }
        mState = state;
    }

    private boolean isLayoutRTL() {
        return ViewCompat.getLayoutDirection(mRecyclerView) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    public boolean isDragging() {
        return mState == STATE_DRAGGING;
    }

    @VisibleForTesting
    boolean isVisible() {
        return mState == STATE_VISIBLE;
    }

    public void show() {
        switch (mAnimationState) {
            case ANIMATION_STATE_FADING_OUT:
                mShowHideAnimator.cancel();
                // fall through
            case ANIMATION_STATE_OUT:
                mAnimationState = ANIMATION_STATE_FADING_IN;
                mShowHideAnimator.setFloatValues((float) mShowHideAnimator.getAnimatedValue(), 1);
                mShowHideAnimator.setDuration(SHOW_DURATION_MS);
                mShowHideAnimator.setStartDelay(0);
                mShowHideAnimator.start();
                break;
        }
    }

    @VisibleForTesting
    void hide(int duration) {
        switch (mAnimationState) {
            case ANIMATION_STATE_FADING_IN:
                mShowHideAnimator.cancel();
                // fall through
            case ANIMATION_STATE_IN:
                mAnimationState = ANIMATION_STATE_FADING_OUT;
                mShowHideAnimator.setFloatValues((float) mShowHideAnimator.getAnimatedValue(), 0);
                mShowHideAnimator.setDuration(duration);
                mShowHideAnimator.start();
                break;
        }
    }

    private void cancelHide() {
        mRecyclerView.removeCallbacks(mHideRunnable);
    }

    private void resetHideDelay(int delay) {
        cancelHide();
        mRecyclerView.postDelayed(mHideRunnable, delay);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (mRecyclerViewWidth != mRecyclerView.getWidth()
                || mRecyclerViewHeight != mRecyclerView.getHeight()) {
            mRecyclerViewWidth = mRecyclerView.getWidth();
            mRecyclerViewHeight = mRecyclerView.getHeight();
            // This is due to the different events ordering when keyboard is opened or
            // retracted vs rotate. Hence to avoid corner cases we just disable the
            // scroller when size changed, and wait until the scroll position is recomputed
            // before showing it back.
            setState(STATE_HIDDEN);
            return;
        }

        if (mAnimationState != ANIMATION_STATE_OUT) {
            if (mNeedVerticalScrollbar) {
                drawVerticalScrollbar(canvas);
                if (mPopupTitle != null)
                    if (mPopupPosition == PopupPosition.CENTER)
                        drawPopupCentre(canvas);
                    else
                        drawPopupVertical(canvas);
            }
            if (mNeedHorizontalScrollbar) {
                drawHorizontalScrollbar(canvas);
                if (mPopupTitle != null)
                    if (mPopupPosition == PopupPosition.CENTER)
                        drawPopupCentre(canvas);
                    else
                        drawPopupHorizontal(canvas);
            }
        }
    }

    private void drawVerticalScrollbar(Canvas canvas) {
        int viewWidth = mRecyclerViewWidth;

        int left = viewWidth - mVerticalThumbWidth;
        int top = mVerticalThumbCenterY - mVerticalThumbHeight / 2;
        mVerticalThumbDrawable.setBounds(0, 0, mVerticalThumbWidth, mVerticalThumbHeight);
        mVerticalTrackDrawable
                .setBounds(0, 0, mVerticalTrackWidth, mRecyclerViewHeight);

        if (isLayoutRTL()) {
            mVerticalTrackDrawable.draw(canvas);
            canvas.translate(mVerticalThumbWidth, top);
            canvas.scale(-1, 1);
            mVerticalThumbDrawable.draw(canvas);
            canvas.scale(1, 1);
            canvas.translate(-mVerticalThumbWidth, -top);
        } else {
            canvas.translate(left, 0);
            mVerticalTrackDrawable.draw(canvas);
            canvas.translate(0, top);
            mVerticalThumbDrawable.draw(canvas);
            canvas.translate(-left, -top);
        }
    }

    private void drawHorizontalScrollbar(Canvas canvas) {
        int viewHeight = mRecyclerViewHeight;

        int top = viewHeight - mHorizontalThumbHeight;
        int left = mHorizontalThumbCenterX - mHorizontalThumbWidth / 2;
        mHorizontalThumbDrawable.setBounds(0, 0, mHorizontalThumbWidth, mHorizontalThumbHeight);
        mHorizontalTrackDrawable
                .setBounds(0, 0, mRecyclerViewWidth, mHorizontalTrackHeight);
        canvas.translate(0, top);
        mHorizontalTrackDrawable.draw(canvas);
        canvas.translate(left, 0);
        mHorizontalThumbDrawable.draw(canvas);
        canvas.translate(-left, -top);
    }

    /**
     * Draws Popup At the centre of Screen
     *
     * @param canvas
     */
    private void drawPopupCentre(Canvas canvas) {
        mTextPaint.getTextBounds(mPopupTitle, 0, mPopupTitle.length(), mTextBounds);
        int bgPadding = Math.round((mTextBounds.height()) / 10f) * 10;
        int width = mTextBounds.width() + 2 * bgPadding;
        int height = mTextBounds.height() + 2 * bgPadding;
        int dx;

        int dy;

        if (isLayoutRTL()) {
            dx = -(mRecyclerView.getWidth() + width) / 2;
            dy = (mRecyclerView.getHeight() - height) / 2;
        } else {
            dx = (mRecyclerView.getWidth() - width) / 2;
            dy = (mRecyclerView.getHeight() - height) / 2;
        }

        mPopupDrawable.setBounds(0, 0, width, height);
        canvas.translate(dx, dy);
        //canvas.scale(1, 1);
        mPopupDrawable.draw(canvas);
        Rect temp = mPopupDrawable.getBounds();
        canvas.drawText(
                mPopupTitle,
                temp.left + bgPadding,
                temp.bottom - bgPadding,
                mTextPaint
        );
    }

    private void drawPopupHorizontal(Canvas canvas) {
        int bgPadding = Math.round((mTextBounds.height()) / 10f) * 10;
        mTextPaint.getTextBounds(mPopupTitle, 0, mPopupTitle.length(), mTextBounds);
        int width = mTextBounds.width() + 2 * bgPadding + 5;
        int height = mTextBounds.height() + 2 * bgPadding;
        /*Adjacent bottom*/
        int left = mHorizontalThumbCenterX /*- width / 2*/;
        int top = mRecyclerViewHeight - mVerticalThumbHeight - mPopupToThumbSpace - height;

        if (mPopupPosition == PopupPosition.ADJACENT_CENTER)
            left -= width/2;
        else if (mPopupPosition == PopupPosition.ADJACENT_TOP)
            left -= mHorizontalThumbWidth/2 + width;
        else
            left += mHorizontalThumbWidth/2;
        if (left < 0)
            left = 0;
        else if (left + width > mRecyclerViewWidth)
            left = mRecyclerViewWidth - width;
        mPopupDrawable.setBounds(0, 0, width, height);
        canvas.translate(left, top);
        mPopupDrawable.draw(canvas);
        Rect temp = mPopupDrawable.getBounds();
        canvas.drawText(
                mPopupTitle,
                temp.left + bgPadding + 5,
                temp.bottom - bgPadding,
                mTextPaint
        );
    }

    private void drawPopupVertical(Canvas canvas) {
        int bgPadding = Math.round((mTextBounds.height()) / 10f) * 10;
        mTextPaint.getTextBounds(mPopupTitle, 0, mPopupTitle.length(), mTextBounds);
        int width = mTextBounds.width() + 2 * bgPadding + 5;
        int height = mTextBounds.height() + 2 * bgPadding;
        int left = mRecyclerViewWidth - mVerticalThumbWidth - mPopupToThumbSpace - width;
        /*Align bottom with centre*/
        int top = mVerticalThumbCenterY/* - height*/;

        if (mPopupPosition == PopupPosition.ADJACENT_CENTER)
            top -= height/2;
        if (mPopupPosition == PopupPosition.ADJACENT_BOTTOM)
            top += mVerticalThumbHeight/2;
        else if (mPopupPosition == PopupPosition.ADJACENT_TOP)
            top -= mVerticalThumbHeight/2 + height;
        if (top < 0)
            top = 0;
        else if (top + height > mRecyclerViewHeight)
            top = mRecyclerViewHeight - height;
        mPopupDrawable.setBounds(0, 0, width, height);
        if (isLayoutRTL()) {
            int pos = mVerticalThumbWidth + mPopupToThumbSpace + width;
            canvas.translate(-pos, top);
            mPopupDrawable.draw(canvas);
            //canvas.translate(width, );
            Rect temp = mPopupDrawable.getBounds();
            canvas.drawText(
                    mPopupTitle,
                    temp.left + bgPadding + 5,
                    temp.bottom - bgPadding,
                    mTextPaint
            );
        } else {
            canvas.translate(left, top);
            mPopupDrawable.draw(canvas);
            Rect temp = mPopupDrawable.getBounds();
            canvas.drawText(
                    mPopupTitle,
                    temp.left + bgPadding + 5,
                    temp.bottom - bgPadding,
                    mTextPaint
            );
        }
    }

    /**
     * Notify the scroller of external change of the scroll, e.g. through dragging or flinging on
     * the view itself.
     *
     * @param offsetX
     *         The new scroll X offset.
     * @param offsetY
     *         The new scroll Y offset.
     */
    void updateScrollPosition(int offsetX, int offsetY) {
        int verticalContentLength = mRecyclerView.computeVerticalScrollRange();
        int verticalVisibleLength = mRecyclerViewHeight;
        mNeedVerticalScrollbar = verticalContentLength - verticalVisibleLength > 0
                && mRecyclerViewHeight >= mScrollbarMinimumRange;

        int horizontalContentLength = mRecyclerView.computeHorizontalScrollRange();
        int horizontalVisibleLength = mRecyclerViewWidth;
        mNeedHorizontalScrollbar = horizontalContentLength - horizontalVisibleLength > 0
                && mRecyclerViewWidth >= mScrollbarMinimumRange;

        if (!mNeedVerticalScrollbar && !mNeedHorizontalScrollbar) {
            if (mState != STATE_HIDDEN) {
                setState(STATE_HIDDEN);
            }
            return;
        }

        if (mNeedVerticalScrollbar) {
            float middleScreenPos = offsetY + verticalVisibleLength / 2.0f;
            mVerticalThumbCenterY =
                    (int) ((verticalVisibleLength * middleScreenPos) / verticalContentLength);
            mVerticalThumbHeight = Math.min(verticalVisibleLength,
                    (verticalVisibleLength * verticalVisibleLength) / verticalContentLength);
            //check may be height is less than minimum
            int newHeight = Math.max(mVerticalThumbHeightMin, mVerticalThumbHeight);
            if (mFixedSize)
                // if user requested fixed Height new height will always be equal to minimum height
                // as it represents the height of drawable
                newHeight = mVerticalThumbHeightMin;
            if (newHeight > mVerticalThumbHeight) {
                // the position of the from 0 to view height on screen
                double pos = (((double) offsetY / (verticalContentLength - verticalVisibleLength))
                        * verticalVisibleLength);
                // only then it needs adjustment
                // distribute that adjustment equally among the pixels
                int adj =
                        (int) (((double) (newHeight - mVerticalThumbHeight) / verticalVisibleLength)
                                * pos);
                // remove the computed height of scroller from centreY
                mVerticalThumbCenterY -= mVerticalThumbHeight / 2;
                //update new thumb height
                mVerticalThumbHeight = newHeight;
                //add new height to centreY
                mVerticalThumbCenterY += newHeight / 2;
                // dis
                mVerticalThumbCenterY -= adj;
            } else if (newHeight < mVerticalThumbHeight) {
                // the position of the from 0 to view height on screen
                double pos = (((double) (offsetY) / (verticalContentLength - verticalVisibleLength)
                        * verticalVisibleLength));
                // only then it needs adjustment
                // distribute that adjustment equally among the pixels
                int adj = (int) (((double) (mVerticalThumbHeight - newHeight)
                        / verticalVisibleLength)
                        * pos);
                // remove the computed height of scroller from centreY
                mVerticalThumbCenterY -= mVerticalThumbHeight / 2;
                //update new thumb height
                mVerticalThumbHeight = newHeight;
                //add new height to centreY
                mVerticalThumbCenterY += newHeight / 2;
                // dis
                mVerticalThumbCenterY += adj;
            }
        }

        if (mNeedHorizontalScrollbar) {
            float middleScreenPos = offsetX + horizontalVisibleLength / 2.0f;
            mHorizontalThumbCenterX =
                    (int) ((horizontalVisibleLength * middleScreenPos) / horizontalContentLength);
            mHorizontalThumbWidth = Math.min(horizontalVisibleLength,
                    (horizontalVisibleLength * horizontalVisibleLength) / horizontalContentLength);
            //check may be height is less than minimum
            int newWidth = Math.max(mHorizontalThumbWidth, mHorizontalThumbWidthMin);
            if (mFixedSize)
                newWidth = mHorizontalThumbWidthMin;
            if (newWidth < mHorizontalThumbWidth) {
                double pos = (((double) offsetX / (horizontalContentLength
                        - horizontalVisibleLength))
                        * horizontalVisibleLength);
                int adj =
                        (int) (((double) (newWidth - mHorizontalThumbWidth)
                                / horizontalVisibleLength)
                                * pos);
                mHorizontalThumbCenterX -= mHorizontalThumbWidth / 2;
                mHorizontalThumbWidth = newWidth;
                mHorizontalThumbCenterX += newWidth / 2;
                mHorizontalThumbCenterX += adj;
            } else if (newWidth > mHorizontalThumbWidth) {
                double pos = (((double) (offsetX) / (horizontalContentLength - horizontalVisibleLength)
                        * horizontalVisibleLength));
                int adj = (int) (((double) (newWidth - mHorizontalThumbWidth)
                        / horizontalVisibleLength)
                        * pos);
                mHorizontalThumbCenterX -= mHorizontalThumbWidth / 2;
                mHorizontalThumbWidth = newWidth;
                mHorizontalThumbCenterX += newWidth / 2;
                mHorizontalThumbCenterX -= adj;
            }
        }

        if (mState == STATE_HIDDEN || mState == STATE_VISIBLE) {
            setState(STATE_VISIBLE);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView,
                                         @NonNull MotionEvent ev) {
        final boolean handled;
        if (mState == STATE_VISIBLE) {
            boolean insideVerticalThumb = isPointInsideVerticalThumb(ev.getX(), ev.getY());
            boolean insideHorizontalThumb = isPointInsideHorizontalThumb(ev.getX(), ev.getY());
            if (ev.getAction() == MotionEvent.ACTION_DOWN
                    && (insideVerticalThumb || insideHorizontalThumb)) {
                if (insideHorizontalThumb) {
                    mDragState = DRAG_X;
                    mHorizontalDragX = (int) ev.getX();
                } else if (insideVerticalThumb) {
                    mDragState = DRAG_Y;
                    mVerticalDragY = (int) ev.getY();
                }

                setState(STATE_DRAGGING);
                handled = true;
            } else {
                handled = false;
            }
        } else handled = mState == STATE_DRAGGING;
        return handled;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent me) {
        if (mState == STATE_HIDDEN) {
            return;
        }

        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            boolean insideVerticalThumb = isPointInsideVerticalThumb(me.getX(), me.getY());
            boolean insideHorizontalThumb = isPointInsideHorizontalThumb(me.getX(), me.getY());
            if (insideVerticalThumb || insideHorizontalThumb) {
                if (insideHorizontalThumb) {
                    mDragState = DRAG_X;
                    mHorizontalDragX = (int) me.getX();
                } else if (insideVerticalThumb) {
                    mDragState = DRAG_Y;
                    mVerticalDragY = (int) me.getY();
                }
                setState(STATE_DRAGGING);
            }
        } else if (me.getAction() == MotionEvent.ACTION_UP && mState == STATE_DRAGGING) {
            mVerticalDragY = 0;
            mHorizontalDragX = 0;
            setState(STATE_VISIBLE);
            mDragState = DRAG_NONE;
        } else if (me.getAction() == MotionEvent.ACTION_MOVE && mState == STATE_DRAGGING) {
            show();
            if (mDragState == DRAG_X) {
                horizontalScrollTo(me.getX());
            }
            if (mDragState == DRAG_Y) {
                verticalScrollTo(me.getY());
            }
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private void verticalScrollTo(float y) {
        final int[] scrollbarRange = getVerticalRange();
        y = Math.max(scrollbarRange[0], Math.min(scrollbarRange[1], y));
        if (Math.abs(mVerticalThumbCenterY - y) < 2) {
            return;
        }
        int scrollingBy = scrollTo(mVerticalDragY, y, scrollbarRange,
                mRecyclerView.computeVerticalScrollRange(),
                mRecyclerView.computeVerticalScrollOffset(), mRecyclerViewHeight);
        if (scrollingBy != 0) {
            mRecyclerView.scrollBy(0, scrollingBy);
        }
        mVerticalDragY = y;
    }

    private void horizontalScrollTo(float x) {
        final int[] scrollbarRange = getHorizontalRange();
        x = Math.max(scrollbarRange[0], Math.min(scrollbarRange[1], x));
        if (Math.abs(mHorizontalThumbCenterX - x) < 2) {
            return;
        }

        int scrollingBy = scrollTo(mHorizontalDragX, x, scrollbarRange,
                mRecyclerView.computeHorizontalScrollRange(),
                mRecyclerView.computeHorizontalScrollOffset(), mRecyclerViewWidth);
        if (scrollingBy != 0) {
            mRecyclerView.scrollBy(scrollingBy, 0);
        }

        mHorizontalDragX = x;
    }

    private int scrollTo(float oldDragPos, float newDragPos, int[] scrollbarRange, int scrollRange,
                         int scrollOffset, int viewLength) {
        int scrollbarLength = scrollbarRange[1] - scrollbarRange[0];
        if (scrollbarLength == 0) {
            return 0;
        }
        float percentage = ((newDragPos - oldDragPos) / (float) scrollbarLength);
        int totalPossibleOffset = scrollRange - viewLength;
        int scrollingBy = (int) (percentage * totalPossibleOffset);
        int absoluteOffset = scrollOffset + scrollingBy;
        if (absoluteOffset < totalPossibleOffset && absoluteOffset >= 0) {
            return scrollingBy;
        } else {
            return 0;
        }
    }

    @VisibleForTesting
    boolean isPointInsideVerticalThumb(float x, float y) {
        return (isLayoutRTL() ? x <= mVerticalThumbWidth
                : x >= mRecyclerViewWidth - mVerticalThumbWidth)
                && y >= mVerticalThumbCenterY - mVerticalThumbHeight / 2
                && y <= mVerticalThumbCenterY + mVerticalThumbHeight / 2;
    }

    @VisibleForTesting
    boolean isPointInsideHorizontalThumb(float x, float y) {
        return (y >= mRecyclerViewHeight - mHorizontalThumbHeight)
                && x >= mHorizontalThumbCenterX - mHorizontalThumbWidth / 2
                && x <= mHorizontalThumbCenterX + mHorizontalThumbWidth / 2;
    }

    @VisibleForTesting
    Drawable getHorizontalTrackDrawable() {
        return mHorizontalTrackDrawable;
    }

    @VisibleForTesting
    Drawable getHorizontalThumbDrawable() {
        return mHorizontalThumbDrawable;
    }

    @VisibleForTesting
    Drawable getVerticalTrackDrawable() {
        return mVerticalTrackDrawable;
    }

    @VisibleForTesting
    Drawable getVerticalThumbDrawable() {
        return mVerticalThumbDrawable;
    }

    /**
     * Gets the (min, max) vertical positions of the vertical scroll bar.
     */
    private int[] getVerticalRange() {
        mVerticalRange[0] = mMargin;
        mVerticalRange[1] = mRecyclerViewHeight - mMargin;
        return mVerticalRange;
    }

    /**
     * Gets the (min, max) horizontal positions of the horizontal scroll bar.
     */
    private int[] getHorizontalRange() {
        mHorizontalRange[0] = mMargin;
        mHorizontalRange[1] = mRecyclerViewWidth - mMargin;
        return mHorizontalRange;
    }

    @IntDef({STATE_HIDDEN, STATE_VISIBLE, STATE_DRAGGING})
    @Retention(RetentionPolicy.SOURCE)
    private @interface State {
    }

    @IntDef({DRAG_X, DRAG_Y, DRAG_NONE})
    @Retention(RetentionPolicy.SOURCE)
    private @interface DragState {
    }

    @IntDef({ANIMATION_STATE_OUT, ANIMATION_STATE_FADING_IN, ANIMATION_STATE_IN,
             ANIMATION_STATE_FADING_OUT})
    @Retention(RetentionPolicy.SOURCE)
    private @interface AnimationState {
    }

    @IntDef({PopupPosition.ADJACENT_CENTER, PopupPosition.ADJACENT_BOTTOM, PopupPosition.ADJACENT_TOP,  PopupPosition.CENTER})
    public @interface PopupPosition {
        int ADJACENT_CENTER = 0;
        int ADJACENT_BOTTOM = 1;
        int ADJACENT_TOP = 2;
        int CENTER = 3;
    }

    private class AnimatorListener extends AnimatorListenerAdapter {

        private boolean mCanceled = false;

        AnimatorListener() {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            // Cancel is always followed by a new directive, so don't update state.
            if (mCanceled) {
                mCanceled = false;
                return;
            }
            if ((float) mShowHideAnimator.getAnimatedValue() == 0) {
                mAnimationState = ANIMATION_STATE_OUT;
                setState(STATE_HIDDEN);
            } else {
                mAnimationState = ANIMATION_STATE_IN;
                requestRedraw();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mCanceled = true;
        }
    }

    private class AnimatorUpdater implements AnimatorUpdateListener {
        AnimatorUpdater() {
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            int alpha = (int) (SCROLLBAR_FULL_OPAQUE * ((float) valueAnimator.getAnimatedValue()));
            mVerticalThumbDrawable.setAlpha(alpha);
            mVerticalTrackDrawable.setAlpha(alpha);
            requestRedraw();
        }
    }
}