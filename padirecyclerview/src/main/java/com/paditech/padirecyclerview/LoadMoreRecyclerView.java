package com.paditech.padirecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.paditech.padirecyclerview.drag_recycler.ItemDragHelperCallback;
import com.paditech.padirecyclerview.drag_recycler.TouchItemHelperAdapter;
import com.paditech.padirecyclerview.util.AnimationUtil;

import java.util.List;

/**
 * Created by NhaPCS on 28/03/2017.
 */

public class LoadMoreRecyclerView extends RecyclerView {
    public static final int ITEM_ANIMATE_DURATION = 200;
    static final int MIN_DISTANCE = 150;
    private static final int TYPE_FOOTER = 10001;
    private View mLoadMoreView;
    private View mSpinner;
    private boolean mIsLoading = false;
    private WrapAdapter mWrapAdapter;
    private DataObserver mDataObserver = new DataObserver();
    private LoadMoreListener mLoadMoreListener;
    private boolean mScrollable;
    private int mLoadMoreColor = Color.BLUE;
    private int mItemAnimateDuration = ITEM_ANIMATE_DURATION;
    private int mLastPositionVisible = 0;
    private boolean mDraggingEnable, mSwipeEnable;
    private ItemTouchHelper mItemTouchHelper;
    private boolean mAnimateEnable = true;

    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScrollable = false;
        init(attrs);
    }

    public void setLoadMoreListener(LoadMoreListener listener) {
        this.mLoadMoreListener = listener;
    }

    private void init(AttributeSet attributeSet) {
        if (attributeSet == null) return;
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.Padi_RecycelerView);
        mLoadMoreColor = typedArray.getColor(R.styleable.Padi_RecycelerView_load_more_color, Color.BLUE);
        mItemAnimateDuration = typedArray.getInt(R.styleable.Padi_RecycelerView_item_animate_duration, ITEM_ANIMATE_DURATION);
        mDraggingEnable = typedArray.getBoolean(R.styleable.Padi_RecycelerView_dragging_enable, false);
        mSwipeEnable = typedArray.getBoolean(R.styleable.Padi_RecycelerView_swipe_enable, false);
        typedArray.recycle();
    }

    public void setmLoadMoreColor(int mLoadMoreColor) {
        this.mLoadMoreColor = mLoadMoreColor;
    }

    public void setmItemAnimateDuration(int mItemAnimateDuration) {
        this.mItemAnimateDuration = mItemAnimateDuration;
    }

    public void setLoadMoreView(View loadMore) {
        this.mLoadMoreView = loadMore;
    }

    public void onLoadMoreComplete() {
        if (mWrapAdapter != null) {
            AnimationUtil.goneViewScaleFade(getContext(), mLoadMoreView);
            mLoadMoreView.setVisibility(GONE);
            mIsLoading = false;
            //mWrapAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mWrapAdapter = new WrapAdapter(adapter);
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
        if (mDraggingEnable || mSwipeEnable) {
            ItemTouchHelper.Callback callback = new ItemDragHelperCallback(mWrapAdapter) {
                @Override
                public boolean isLongPressDragEnabled() {
                    return false;
                }

                @Override
                public int interpolateOutOfBoundsScroll(RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {
                    final int direction = (int) Math.signum(viewSizeOutOfBounds);
                    return 20 * direction;
                }
            };
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(this);
        }
    }

    @Override
    public Adapter getAdapter() {
        if (mWrapAdapter != null)
            return mWrapAdapter.getOriginalAdapter();
        return null;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (mWrapAdapter != null) {
            if (layout instanceof GridLayoutManager) {
                final GridLayoutManager gridLayoutManager = (GridLayoutManager) layout;
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return mWrapAdapter.isFooter(position) ? gridLayoutManager.getSpanCount() : 1;
                    }
                });
            }
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == SCROLL_STATE_IDLE && mLoadMoreListener != null && !mIsLoading) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition = 0;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof LinearLayoutManager) {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            }

            if (layoutManager.getChildCount() > 0 &&
                    lastVisibleItemPosition >= layoutManager.getItemCount() - 1 &&
                    layoutManager.getItemCount() > layoutManager.getChildCount()) {
                mIsLoading = true;
                /*LoadMoreRecyclerView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        mWrapAdapter.notifyDataSetChanged();
                    }
                });*/
                //AnimationUtil.visibleViewScaleFade(getContext(), mSpinner);
                AnimationUtil.visibleViewScaleFade(getContext(), mLoadMoreView);
                smoothScrollToPosition(mWrapAdapter.getItemCount() + 1);
                mLoadMoreListener.onLoadMore();
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        runItemsAnimate();
    }

    private void runItemsAnimate() {
        if (!mAnimateEnable) return;
        int firstPositionVisible = 0;
        int spanCount = 0;
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            firstPositionVisible = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof LinearLayoutManager) {
            firstPositionVisible = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            spanCount = 1;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int fist[] = null;
            fist = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(fist);
            if (fist != null && fist.length > 0) firstPositionVisible = fist[0];
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }

        if (firstPositionVisible == 0) {
            for (int i = 0; i < getChildCount(); i++) {
                animate(getChildAt(i), i / spanCount);

                if (i == getChildCount() - 1) {
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mScrollable = true;
                        }
                    }, i * 100);
                }
            }
            mAnimateEnable = false;
        }
    }

    private void animate(final View view, final int pos) {
        view.post(new Runnable() {
            @Override
            public void run() {
                view.animate().cancel();
                view.setTranslationY(100);
                view.setAlpha(0);
                view.setScaleX(0.95f);
                view.setScaleY(0.95f);
                view.animate().alpha(1.0f).scaleX(1f).scaleY(1f).
                        setInterpolator(new AccelerateDecelerateInterpolator()).translationY(0).
                        setDuration(mItemAnimateDuration).setStartDelay(pos * 100);
            }
        });
    }

    private int findMax(int[] lastPositions) {

        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }


    private class WrapAdapter extends Adapter<ViewHolder> implements TouchItemHelperAdapter {

        private float x1, x2;
        private Adapter<ViewHolder> adapter;

        public WrapAdapter(Adapter<ViewHolder> adapter) {
            this.adapter = adapter;
        }

        public Adapter<ViewHolder> getOriginalAdapter() {
            return this.adapter;
        }


        public boolean isFooter(int position) {
            return position == getItemCount() - 1;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_FOOTER) {
                mLoadMoreView = LayoutInflater.from(getContext()).inflate(R.layout.view_loadmore, parent, false);
                mSpinner = mLoadMoreView.findViewById(R.id.spinner);
                AnimationDrawable mAnimationDrawable = (AnimationDrawable) mSpinner.getBackground();
                mAnimationDrawable.setColorFilter(mLoadMoreColor, PorterDuff.Mode.SRC_ATOP);
                mAnimationDrawable.start();
                return new SimpleViewHolder(mLoadMoreView);
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (adapter != null) {
                if (position < adapter.getItemCount()) {
                    adapter.onBindViewHolder(holder, position);
                }
            }
        }

        // some times we need to override this
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position, List<Object> payloads) {
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (position < adapterCount) {
                    if (payloads.isEmpty()) {
                        adapter.onBindViewHolder(holder, position);
                    } else {
                        adapter.onBindViewHolder(holder, position, payloads);
                    }

                    holder.itemView.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            switch (motionEvent.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    x1 = motionEvent.getX();
                                    if (mDraggingEnable && mItemTouchHelper != null)
                                        mItemTouchHelper.startDrag(holder);
                                    if (mSwipeEnable && mItemTouchHelper != null) {
                                        mItemTouchHelper.startSwipe(holder);
                                    }
                                    break;
                                case MotionEvent.ACTION_UP:
                                    break;
                                case MotionEvent.ACTION_CANCEL:
                                    break;
                            }
                            return false;
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            if (adapter != null) {
                int count = adapter.getItemCount();
                if (count > 0) return adapter.getItemCount() + 1;
            }
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (isFooter(position)) {
                return TYPE_FOOTER;
            }
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (position < adapterCount) {
                    return adapter.getItemViewType(position);
                }
            }
            return super.getItemViewType(position);
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position >= 1) {
                if (position < adapter.getItemCount()) {
                    return adapter.getItemId(position);
                }
            }
            return super.getItemId(position);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return isFooter(position) ? gridManager.getSpanCount() : 1;
                    }
                });
            }
            adapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && isFooter(holder.getLayoutPosition())) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
            adapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            adapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            adapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(ViewHolder holder) {
            return adapter.onFailedToRecycleView(holder);
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            adapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            adapter.registerAdapterDataObserver(observer);
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            adapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public boolean onItemRemove(int position) {
            adapter.notifyItemRemoved(position);
            return true;
        }

        private class SimpleViewHolder extends ViewHolder {
            SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    private class DataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
            if (mWrapAdapter != null) {
                int emptyCount = 0;
                emptyCount++;
                if (mWrapAdapter.getItemCount() == emptyCount) {
                    LoadMoreRecyclerView.this.setVisibility(View.GONE);
                } else {
                    LoadMoreRecyclerView.this.setVisibility(View.VISIBLE);
                }
            }
            mAnimateEnable = true;
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

    public interface LoadMoreListener {
        void onLoadMore();
    }
}
