package augsburg.se.alltagsguide.utilities;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder, Item extends Newer> extends RecyclerView.Adapter<VH> {

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
            int indexOf = mItems.indexOf(model);
            if (indexOf != -1) {
                Item other = mItems.get(indexOf);
                if (model.getTimestamp() > other.getTimestamp()) {
                    /* Update might have changed the model, so put new object and notify adapter about the new item*/
                    mItems.set(indexOf, model);
                    notifyItemChanged(indexOf);
                }
            } else {
                // item not in list -> add here
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

    public void setItems(List<Item> items) {
        applyAndAnimateRemovals(items);
        applyAndAnimateAdditions(items);
        applyAndAnimateMovedItems(items);
    }

}
