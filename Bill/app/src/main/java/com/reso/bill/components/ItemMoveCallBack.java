package com.reso.bill.components;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import adapters.generic.GroupCustomersAdapter;

/**
 * Created by Admin on 21/01/2019.
 */

public class ItemMoveCallBack extends ItemTouchHelper.Callback {

    private final ItemTouchHelperContract mAdapter;

    public ItemMoveCallBack(ItemTouchHelperContract adapter) {
        mAdapter = adapter;
    }

    /*@Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return 0;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }*/

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        System.out.println("Swiped ..");
        if (viewHolder instanceof GroupCustomersAdapter.RecViewHolder) {
            GroupCustomersAdapter.RecViewHolder myViewHolder = (GroupCustomersAdapter.RecViewHolder) viewHolder;
            mAdapter.onRowSwiped(myViewHolder);
        }
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        System.out.println("Row moved ...");
        mAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {


        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof GroupCustomersAdapter.RecViewHolder) {
                GroupCustomersAdapter.RecViewHolder myViewHolder = (GroupCustomersAdapter.RecViewHolder) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }

        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof GroupCustomersAdapter.RecViewHolder) {
            GroupCustomersAdapter.RecViewHolder myViewHolder = (GroupCustomersAdapter.RecViewHolder) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }
    }

    public interface ItemTouchHelperContract {

        void onRowMoved(int fromPosition, int toPosition);

        void onRowSelected(GroupCustomersAdapter.RecViewHolder myViewHolder);

        void onRowClear(GroupCustomersAdapter.RecViewHolder myViewHolder);

        void onRowSwiped(GroupCustomersAdapter.RecViewHolder myViewHolder);

    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

}
