package com.prime.ViewRecycler;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.prime.R;

public class ViewRecycler extends RecyclerView {

    @IdRes
    private int mEmptyViewId = View.NO_ID;
    private Adapter<ViewHolder> mAdapter;
    @Nullable
    private EmptyVisibilityObserver mObserver;
    @Nullable
    private CharSequence mEmptyMessage;

    private final AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (mEmptyViewId != View.NO_ID) {
                View mEmptyView = ((ViewGroup) getParent()).findViewById(mEmptyViewId);

                if (mAdapter.getItemCount() == 0) {
                    ViewRecycler.this.setVisibility(INVISIBLE);
                    mEmptyView.setVisibility(VISIBLE);
                    if (mEmptyMessage != null) {
                        // try to find id message
                        TextView view = mEmptyView.findViewById(R.id.message);
                        if (view != null)
                            view.setText(mEmptyMessage);
                    }
                    if (mObserver != null)
                        mObserver.onShow(mEmptyView);
                } else {
                    ViewRecycler.this.setVisibility(VISIBLE);
                    mEmptyView.setVisibility(INVISIBLE);
                }
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            onChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            onChanged();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            onChanged();
        }
    };


    public ViewRecycler(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public ViewRecycler(@NonNull Context context,
                        @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ViewRecycler(@NonNull Context context,
                        @Nullable AttributeSet attrs,
                        int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);
        if (mAdapter != null)
            mAdapter.unregisterAdapterDataObserver(mDataObserver);
        mAdapter = adapter;
        if (adapter != null)
            adapter.registerAdapterDataObserver(mDataObserver);
    }

    public void setEmptyMessage(@Nullable CharSequence message) {
        mEmptyMessage = message;
    }

    public void setEmptyMessage(@StringRes int message) {
        mEmptyMessage = getResources().getString(message);
    }

    public void setEmptyView(@IdRes int viewId) {
        mEmptyViewId = viewId;
    }

    public void setEmptyViewInformer(@Nullable EmptyVisibilityObserver observer) {
        mObserver = observer;
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewRecycler
                , defStyleAttr, defStyleAttr);
        try {
            boolean enableFastScroll = a.getBoolean(R.styleable.ViewRecycler_enableFastScroll
                    , false);
            mEmptyViewId = a.getResourceId(R.styleable.ViewRecycler_emptyView, View.NO_ID);
            mEmptyMessage = a.getString(R.styleable.ViewRecycler_emptyMessage);
            if (enableFastScroll)
                new FastScroller(context, this, attrs);

        } finally {
            a.recycle();
        }
    }

    public interface EmptyVisibilityObserver {
        void onShow(@NonNull View emptyView);
    }
}
