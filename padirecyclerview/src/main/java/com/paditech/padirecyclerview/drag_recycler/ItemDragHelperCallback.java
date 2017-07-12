package com.paditech.padirecyclerview.drag_recycler;

import android.graphics.Canvas;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Nha Nha on 6/20/2017.
 */

public class ItemDragHelperCallback extends ItemTouchHelper.Callback {
    private final static float ALPHA_FULL = 1.0f;

    private TouchItemHelperAdapter mAdapter;

    public ItemDragHelperCallback(TouchItemHelperAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (viewHolder.getItemViewType() != target.getItemViewType()) {
            return false;
        }

        // Notify the adapter of the move
        return true;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();

            if (viewHolder.itemView instanceof ViewGroup) {
                int childCount = ((ViewGroup) viewHolder.itemView).getChildCount();
                switch (childCount) {
                    case 1:
                        View view = ((ViewGroup) viewHolder.itemView).getChildAt(0);
                        if (view instanceof ViewGroup) {
                            switch (((ViewGroup) view).getChildCount()) {
                                case 2:
                                    View view1 = ((ViewGroup) view).getChildAt(0);
                                    View view2 = ((ViewGroup) view).getChildAt(1);
                                   // view2.setAlpha(alpha);
                                    view2.setTranslationX(dX);
                                    view1.setVisibility(View.VISIBLE);
                                    view1.setAlpha(1 - alpha);
                                    break;
                                default:
                                    viewHolder.itemView.setAlpha(alpha);
                                    viewHolder.itemView.setTranslationX(dX);
                                    break;
                            }
                        } else {
                            viewHolder.itemView.setAlpha(alpha);
                            viewHolder.itemView.setTranslationX(dX);
                        }
                        break;
                    case 2:
                        View view1 = ((ViewGroup) viewHolder.itemView).getChildAt(0);
                        View view2 = ((ViewGroup) viewHolder.itemView).getChildAt(1);
                        //view2.setAlpha(alpha);
                        view2.setTranslationX(dX);
                        view1.setVisibility(View.VISIBLE);
                        view1.setAlpha(1 - alpha);
                        break;
                    default:
                        viewHolder.itemView.setAlpha(alpha);
                        viewHolder.itemView.setTranslationX(dX);
                        break;

                }
            } else {
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            }
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            /*if (viewHolder instanceof RecyclerViewDragAdapter.DragViewHolder) {
                // Let the view holder know that this item is being moved or dragged
                RecyclerViewDragAdapter.DragViewHolder itemViewHolder = (RecyclerViewDragAdapter.DragViewHolder) viewHolder;
                itemViewHolder.onItemSelected();
            }*/
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //mAdapter.onItemRemove(viewHolder.getAdapterPosition());
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
        mAdapter.onItemMove(fromPos, toPos);
    }
}
