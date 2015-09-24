package augsburg.se.alltagsguide.utilities;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder, Item extends Comparable> extends RecyclerView.Adapter<VH> {

    private List<Item> mItems;

    protected BaseAdapter(List<Item> items) {
        super();
        mItems = items;
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
        mItems = items;
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
