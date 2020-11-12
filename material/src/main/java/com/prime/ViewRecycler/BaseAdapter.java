package com.prime.ViewRecycler;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.prime.Glide.GlideApp;
import com.prime.Glide.GlideRequest;
import com.prime.Glide.GlideRequests;
import com.prime.Glide.Overlay;
import com.prime.R;

import java.util.Collections;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public abstract class BaseAdapter<T> extends ListAdapter<T, BaseAdapter.ViewHolder>
        implements ListPreloader.PreloadSizeProvider<T>, ListPreloader.PreloadModelProvider<T> {
    private final boolean mAllowGlide = true;
    @Nullable
    protected GestureListener mGestureListener;
    protected GlideRequest<Drawable> mRequestBuilder;
    private RecyclerView mRecyclerView;
    @LayoutRes
    private int mItemLayout = R.layout.list_item;
    private GlideRequests mGlideRequests;
    private RecyclerViewPreloader<T> mPreloader;
    @Nullable
    private Drawable mOverlay;

    protected BaseAdapter(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
    }

    public final void setGestureListener(@Nullable GestureListener listener) {
        mGestureListener = listener;
    }

    public final void setItemLayout(@LayoutRes int itemLayout) {
        mItemLayout = itemLayout;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        if (mAllowGlide) {
            initGlide();
        }
    }

    protected void initGlide() {
        mGlideRequests = GlideApp.with(mRecyclerView);
        mRequestBuilder =
                mGlideRequests.asDrawable()
                        .transition(withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.NONE);
        mPreloader = new RecyclerViewPreloader<>(mGlideRequests, this, this, 5);
        mRecyclerView.addOnScrollListener(mPreloader);
    }


    @NonNull
    @Override
    public final ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(mItemLayout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        if (mGestureListener != null) {
            holder.itemView.setOnClickListener(v -> mGestureListener.onClick(holder.getAdapterPosition()));
            holder.mOverflowBtn.setOnClickListener(v -> mGestureListener.onOverFlowClick(v,
                    holder.getAdapterPosition()));
            holder.itemView.setOnLongClickListener(v -> mGestureListener.onLongClick(holder.getAdapterPosition()));
            // this will act as handle for drag
            // provide animatetable vector drawable for this to change
        }
        if (isDragAllowed())
            holder.mOverflowBtn.setOnLongClickListener(v -> {
                startDrag(holder);
                return true;
            });
        return holder;
    }


    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull T item) {
        return null;
    }

    @Nullable
    @Override
    public int[] getPreloadSize(@NonNull T item, int adapterPosition, int perItemPosition) {
        return new int[]{60, 60};
    }


    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        if (mAllowGlide) {
            recyclerView.removeOnScrollListener(mPreloader);
            mGlideRequests.pauseAllRequestsRecursive();
        }
        super.onDetachedFromRecyclerView(recyclerView);
    }


    public void clearGlideRequest(ImageView forView) {
        mGlideRequests.clear(forView);
    }

    public RequestBuilder<Drawable> getRequestBuilder() {
        return mRequestBuilder;
    }

    @SuppressLint("CheckResult")
    protected void setOverlay(Drawable overlay) {
        mOverlay = overlay;
        mRequestBuilder.transform(new Overlay(mOverlay));
    }

    @NonNull
    @Override
    public final List<T> getPreloadItems(int position) {
        return (getItemCount() == 0) ?
                Collections.emptyList() :
                Collections.singletonList(getItem(position));
    }

    @Override
    protected final boolean onMove(int fromPosition, int toPosition) {
        if (mGestureListener != null) return mGestureListener.onMove(fromPosition, toPosition);
        return false;
    }


    @Override
    protected void onSwipe(int position, int direction) {
        if (mGestureListener != null)
            mGestureListener.onSwipe(position, direction);
    }

    public interface GestureListener {
        void onClick(int position);

        boolean onLongClick(int position);

        boolean onMove(int fromPosition, int toPosition);

        void onSwipe(int position, int direction);

        boolean onDoubleTap(int position);

        boolean onOverFlowClick(View anchor, int position);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitleView;
        public TextView mSubView;
        public TextView mDescView;
        public ImageView mArtworkView;
        public ImageButton mOverflowBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.title);
            mSubView = itemView.findViewById(R.id.subtitle);
            mDescView = itemView.findViewById(R.id.desc);
            mArtworkView = itemView.findViewById(R.id.artwork);
            mOverflowBtn = itemView.findViewById(R.id.overflow);
        }
    }
}
