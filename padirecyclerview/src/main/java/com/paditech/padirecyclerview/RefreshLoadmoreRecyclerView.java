package com.paditech.padirecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import static com.paditech.padirecyclerview.LoadMoreRecyclerView.ITEM_ANIMATE_DURATION;

/**
 * Created by Nha Nha on 7/11/2017.
 */

public class RefreshLoadmoreRecyclerView extends SwipeRefreshLayout {
    private int mRefreshColor = Color.BLUE;

    private LoadMoreRecyclerView mLoadMoreRecyclerView;

    public RefreshLoadmoreRecyclerView(Context context) {
        this(context, null);
    }

    public RefreshLoadmoreRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        if (attributeSet == null) return;

        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.Padi_RecycelerView);
        mRefreshColor = typedArray.getColor(R.styleable.Padi_RecycelerView_refresh_color, Color.BLUE);
        mLoadMoreRecyclerView = new LoadMoreRecyclerView(getContext(), attributeSet);
        typedArray.recycle();

        addView(mLoadMoreRecyclerView, new SwipeRefreshLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setColorSchemeColors(mRefreshColor);
    }

    public void setLoadMoreListener(LoadMoreRecyclerView.LoadMoreListener listener) {
        if (mLoadMoreRecyclerView != null) mLoadMoreRecyclerView.setLoadMoreListener(listener);
    }

    public void onLoadMoreComplete() {
        if (mLoadMoreRecyclerView != null) {
            mLoadMoreRecyclerView.onLoadMoreComplete();
        }
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (mLoadMoreRecyclerView != null)
            mLoadMoreRecyclerView.setLayoutManager(layoutManager);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (mLoadMoreRecyclerView != null) {
            mLoadMoreRecyclerView.setAdapter(adapter);
        }
    }
}
