package com.prime.Dialog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.prime.R;
import com.prime.Utils.Utils;
import com.prime.ViewRecycler.BaseAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListDialog extends BaseDialog<ListDialog> {

    @LayoutRes
    private int mItemLayout = R.layout.list_item;
    private RecyclerView mListRecycler;
    @Nullable
    private BaseAdapter.GestureListener mListener;
    private Adapter mAdapter;
    private List<ListItem> mList;
    private boolean mAllowSwipeRight = false;
    private boolean mAllowSwipeLeft = false;
    private boolean mAllowDrag = false;
    private final List<RecyclerView.ItemDecoration> mDecorationList = new ArrayList<>();

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

    public ListDialog setSwipeBgColor(@ColorRes int left, @ColorRes int right) {
        mSwipeLeftBackgroundColor = ContextCompat.getColor(getContext(), left);
        mSwipeRightBackgroundColor = ContextCompat.getColor(getContext(), right);
        return this;
    }

    public ListDialog setSwipeBgOnColor(@ColorRes int colorOnLeft, @ColorRes int colorOnRight) {
        mColorOnSwipeLeftBg = ContextCompat.getColor(getContext(), colorOnLeft);
        mColorOnSwipeRightBg = ContextCompat.getColor(getContext(), colorOnRight);
        return this;
    }

    public ListDialog setSwipeActionIcon(@Nullable Drawable left, @Nullable Drawable right) {
        mSwipeLeftActionIcon = left;
        mSwipeRightActionIcon = right;
        if (mSwipeLeftActionIcon != null)
            mSwipeLeftActionIcon.mutate();
        if (mSwipeRightActionIcon != null)
            mSwipeRightActionIcon.mutate();
        return this;
    }

    public ListDialog setSwipeActionIcon(@DrawableRes int left, @DrawableRes int right) {
        Drawable l = ContextCompat.getDrawable(getContext(), left);
        Drawable r = ContextCompat.getDrawable(getContext(), right);
        return setSwipeActionIcon(l, r);
    }


    public Adapter getAdapter() {
        return mAdapter;
    }

    public ListDialog setAdapter(@NonNull Adapter adapter) {
        this.mAdapter = adapter;
        return this;
    }

    public ListDialog setSwipeActionText(@Nullable String left, @Nullable String right) {
        mSwipeLeftText = left;
        mSwipeRightText = right;
        return this;
    }

    public ListDialog setSwipeActionText(@StringRes int leftRes, @StringRes int rightRes) {
        String left = getString(leftRes);
        String right = getString(rightRes);
        return setSwipeActionText(left, right);
    }

    public ListDialog setSwipeTextProp(@NonNull Typeface typeface, int textSize) {
        mTypeface = typeface;
        mTextSize = textSize;
        return this;
    }

    public final ListDialog enableDrag() {
        mAllowDrag = true;
        return this;
    }

    public final ListDialog enableSwipeLeft() {
        mAllowSwipeLeft = true;
        return this;
    }

    public final ListDialog enableSwipeRight() {
        mAllowSwipeRight = true;
        return this;
    }

    public ListDialog setItemLayout(@LayoutRes int itemLayout) {
        mItemLayout = itemLayout;
        return this;
    }


    public ListDialog submitList(List<ListItem> newList) {
        mList = newList;
        if (mAdapter != null)
            mAdapter.submitList(mList);
        return this;
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
    protected ListDialog getThis() {
        return this;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.dialog_layout_list;
    }

    /**
     * Add an {@link RecyclerView.ItemDecoration} to this RecyclerView. Item decorations can
     * affect both measurement and drawing of individual item views.
     *
     * <p>Item decorations are ordered. Decorations placed earlier in the list will
     * be run/queried/drawn first for their effects on item views. Padding added to views
     * will be nested; a padding added by an earlier decoration will mean further
     * item decorations in the list will be asked to draw/pad within the previous decoration's
     * given area.</p>
     *
     * @param decor Decoration to add
     */
    public ListDialog addItemDecoration(@NonNull RecyclerView.ItemDecoration decor) {
       mDecorationList.add(decor);
       return this;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListRecycler = view.findViewById(R.id.dialog_list_recycler);
        if (mAdapter == null)
            mAdapter = new Adapter();
        mAdapter.setItemLayout(mItemLayout);
        mAdapter.setGestureListener(mListener);
        mAdapter.setIconHorizontalMargin(mHorizontalMargin);
        if (mAllowDrag)
            mAdapter.enableDrag();
        if (mAllowSwipeLeft)
            mAdapter.enableSwipeLeft();
        if (mAllowSwipeRight)
            mAdapter.enableSwipeRight();
        mAdapter.setSwipeActionIcon(mSwipeLeftActionIcon, mSwipeRightActionIcon);
        mAdapter.setSwipeActionText(mSwipeLeftText, mSwipeRightText);
        mAdapter.setSwipeBgColor(mSwipeLeftBackgroundColor, mSwipeRightBackgroundColor);
        mAdapter.setSwipeTextProp(mTypeface, mTextSize);
        mAdapter.setSwipeBgOnColor(mColorOnSwipeLeftBg, mColorOnSwipeRightBg);
        mListRecycler.setAdapter(mAdapter);
        mAdapter.submitList(mList);
        for (RecyclerView.ItemDecoration decoration : mDecorationList)
            mListRecycler.addItemDecoration(decoration);
    }

    public ListDialog setMenuItemGestureListener(@Nullable BaseAdapter.GestureListener listener) {
        mListener = listener;
        return this;
    }

    public ListDialog fromMenu(Menu menu) {
        mList = new ArrayList<>();
        if (menu == null)
            return this;
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            ListItem listItem = new ListItem(item.getItemId());
            listItem.setIcon(item.getIcon());
            listItem.setTitle(item.getTitle());
            listItem.setSubtitle(item.getTitleCondensed());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                listItem.setDesc(item.getContentDescription());
            }
            mList.add(listItem);
        }
        if (mAdapter != null)
            mAdapter.submitList(mList);
        return this;
    }

    public ListDialog fromMenu(@MenuRes int mRes) {
        @SuppressLint("RestrictedApi") MenuBuilder builder = new MenuBuilder(getContext());
        new MenuInflater(getContext()).inflate(mRes, builder);
        return fromMenu(builder);
    }

    public static class ListItem {
        private final long mID;
        private CharSequence mTitle;
        private CharSequence mSubtitle;
        private CharSequence mDesc;
        private Drawable mIcon;

        public ListItem(long mID) {this.mID = mID;}


        public long getID() {
            return mID;
        }

        public CharSequence getTitle() {
            return mTitle;
        }

        public void setTitle(CharSequence mTitle) {
            this.mTitle = mTitle;
        }

        public CharSequence getSubtitle() {
            return mSubtitle;
        }

        public void setSubtitle(CharSequence mSubtitle) {
            this.mSubtitle = mSubtitle;
        }

        public CharSequence getDesc() {
            return mDesc;
        }

        public void setDesc(CharSequence mDesc) {
            this.mDesc = mDesc;
        }

        public Drawable getIcon() {
            return mIcon;
        }

        public void setIcon(@Nullable Drawable icon) {
            mIcon = icon;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ListItem)) return false;
            ListItem item = (ListItem) o;
            return mID == item.mID &&
                    Objects.equals(mTitle, item.mTitle) &&
                    Objects.equals(mSubtitle, item.mSubtitle) &&
                    Objects.equals(mDesc, item.mDesc) &&
                    Objects.equals(mIcon, item.mIcon);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mID);
        }
    }

    public static class Adapter extends BaseAdapter<ListItem> {
        private static final DiffUtil.ItemCallback<ListItem>
                mCallback =
                new DiffUtil.ItemCallback<ListItem>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull ListItem oldItem,
                                                   @NonNull ListItem newItem) {
                        return oldItem.getID() == newItem.getID();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull ListItem oldItem,
                                                      @NonNull ListItem newItem) {
                        return oldItem.equals(newItem);
                    }
                };

        public Adapter() {
            super(mCallback);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ListItem item = getItem(position);
            if (item.getIcon() != null)
                holder.mArtworkView.setImageDrawable(item.getIcon());
            holder.mTitleView.setText(item.getTitle());
            holder.mSubView.setText(item.getSubtitle());
            holder.mDescView.setText(item.getDesc());
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getID();
        }
    }
}
