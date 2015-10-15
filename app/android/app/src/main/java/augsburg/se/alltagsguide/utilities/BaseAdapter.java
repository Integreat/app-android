package augsburg.se.alltagsguide.utilities;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder, Item extends Comparable> extends RecyclerView.Adapter<VH> {

    private List<Item> mItems;

    protected BaseAdapter(List<Item> items) {
        super();
        mItems = new ArrayList<>();
        setItems(items);
    }


    private void applyAndAnimateRemovals(List<Item> newModels) {
        for (int i = mItems.size() - 1; i >= 0; i--) {
            final Item model = mItems.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Item> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Item model = newModels.get(i);
            if (!mItems.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Item> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Item model = newModels.get(toPosition);
            final int fromPosition = mItems.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Item removeItem(int position) {
        final Item model = mItems.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, Item model) {
        mItems.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Item model = mItems.remove(fromPosition);
        mItems.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public Item get(int index) {
        return mItems.get(index);
    }

    private int indexOf(Item item) {
        return mItems.indexOf(item);
    }

    public List<Item> getItems() {
        return mItems;
    }

    public void setItems(List<Item> items) {
        applyAndAnimateRemovals(items);
        applyAndAnimateAdditions(items);
        applyAndAnimateMovedItems(items);
    }

    public void remove(Item removingItem) {
        int index = indexOf(removingItem);
        if (index >= 0) {
            mItems.remove(removingItem);
            notifyItemRemoved(index);
        }
    }

    public void removeAll() {
        int size = mItems.size();
        mItems.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void add(List<Item> newItems) {
        List<Item> addedItems = new ArrayList<>();
        for (Item item : newItems) {
            if (!mItems.contains(item)) {
                addedItems.add(item);
                mItems.add(item);
            }
        }
        Collections.sort(mItems);
        for (Item addedItem : addedItems) {
            int index = indexOf(addedItem);
            notifyItemInserted(index);
        }
    }

    public void add(Item newItem) {
        if (!mItems.contains(newItem)) {
            mItems.add(newItem);
            Collections.sort(mItems);
            int index = indexOf(newItem);
            notifyItemInserted(index);
        } else {
            int index = indexOf(newItem);
            notifyItemChanged(index);
        }
    }
}
