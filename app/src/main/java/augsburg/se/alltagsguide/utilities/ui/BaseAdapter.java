/*
 * This file is part of Integreat.
 *
 * Integreat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Integreat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Integreat.  If not, see <http://www.gnu.org/licenses/>.
 */

package augsburg.se.alltagsguide.utilities.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import augsburg.se.alltagsguide.utilities.Newer;
import roboguice.RoboGuice;


public abstract class BaseAdapter<VH extends UltimateRecyclerviewViewHolder, Item extends Newer<Item>> extends UltimateViewAdapter<VH> {

    @NonNull private List<Item> mItems;
    @NonNull protected Context mContext;

    protected BaseAdapter(@NonNull List<Item> items, @NonNull Context context) {
        super();
        RoboGuice.injectMembers(context, this);
        mContext = context;
        mItems = new ArrayList<>();
        setItems(items);
    }

    @Override
    public int getAdapterItemCount() {
        return mItems.size();
    }

    @Override
    public long generateHeaderId(int position) {
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    private void applyAndAnimateRemovals(@NonNull List<Item> newModels) {
        for (int i = mItems.size() - 1; i >= 0; i--) {
            final Item model = mItems.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(@NonNull List<Item> newModels) {
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
                addItem(binarySearch(model), model);
            }
        }
    }

    private int binarySearch(Item value) {
        int low = 0;
        int high = mItems.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            Item midVal = mItems.get(mid);
            int cmp = midVal.compareTo(value);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return low;
    }

    private void applyAndAnimateMovedItems(@NonNull List<Item> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Item model = newModels.get(toPosition);
            final int fromPosition = mItems.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public void removeItem(int position) {
        remove(mItems, position);
    }

    public void addItem(int position, @NonNull Item model) {
        insert(mItems, model, position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        int size = mItems.size();
        if (fromPosition < 0 || fromPosition >= size || toPosition < 0 || toPosition >= size) {
            //avoid out of bounds exception which shouldnt occur in the first place
            return;
        }
        swapPositions(mItems, fromPosition, toPosition);
    }

    @NonNull
    public Item get(int index) {
        return mItems.get(index);
    }

    public void setItems(@NonNull List<Item> items) {
        Collections.sort(items);
        applyAndAnimateRemovals(items);
        applyAndAnimateAdditions(items);
        applyAndAnimateMovedItems(items);
    }
}
