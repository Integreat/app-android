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

package augsburg.se.alltagsguide.navigation;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.utilities.ui.BitmapColorTransformation;
import augsburg.se.alltagsguide.utilities.ui.BitmapInvertTransformation;
import augsburg.se.alltagsguide.utilities.Objects;
import roboguice.RoboGuice;

public class NavigationAdapter extends UltimateViewAdapter<NavigationAdapter.NavigationViewHolder> {
    private OnNavigationSelected mListener;
    private Page mSelectedPage;

    private List<Page> mPages;

    @ColorInt private int mColor;
    private int mCurrentPageId;

    @Inject
    private Picasso mPicasso;
    private Transformation mInvertTransformation;
    private Transformation mColorTransformation;
    private int textColor;
    private int whiteColor;

    public void setPages(@NonNull List<Page> pages) {
        mPages = Page.filterParents(pages);
        notifyDataSetChanged();
    }

    public void setSelectedIndex(int index) {
        mCurrentPageId = index;
        notifyDataSetChanged();
    }

    public interface OnNavigationSelected {
        void onNavigationClicked(Page item);
    }

    public NavigationAdapter(OnNavigationSelected listener, @ColorInt int color, Context context, int currentPageId) {
        RoboGuice.injectMembers(context, this);
        mPages = new ArrayList<>();
        mListener = listener;
        mColor = color;
        mCurrentPageId = currentPageId;
        mColorTransformation = new BitmapColorTransformation(ContextCompat.getColor(context, android.R.color.tertiary_text_light));
        mInvertTransformation = new BitmapInvertTransformation();
        textColor = ContextCompat.getColor(context, android.R.color.primary_text_light);
        whiteColor = ContextCompat.getColor(context, android.R.color.white);
    }

    @Override
    public NavigationViewHolder getViewHolder(View view) {
        return new NavigationViewHolder(view);
    }

    @Override
    public NavigationViewHolder onCreateViewHolder(ViewGroup parent) {
        return new NavigationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_item, parent, false));
    }


    public Page getSelectedPage() {
        return mSelectedPage;
    }

    @Override
    public void onBindViewHolder(NavigationViewHolder holder, int position) {
        if (customHeaderView != null) {
            if (position == 0) {
                return;
            }
            position--; //so you can see the first position because position = 0 is the header!
        }
        final Page item = mPages.get(position);
        boolean selected = Objects.equals(item.getId(), mCurrentPageId);
        if (selected) {
            mSelectedPage = item;
        }
        holder.title.setText(item.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onNavigationClicked(item);
            }
        });
        holder.itemView.setBackgroundColor(selected ? mColor : whiteColor);
        holder.title.setTextColor(selected ? whiteColor : textColor);
        if (!Objects.isNullOrEmpty(item.getThumbnail())) {
            RequestCreator creator = mPicasso.load(item.getThumbnail());
            if (selected) {
                creator = creator.transform(mInvertTransformation);
            } else {
                creator = creator.transform(mColorTransformation);
            }
            creator.into(holder.image);
        } else {
            holder.image.setImageDrawable(null);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getAdapterItemCount() {
        return mPages.size();
    }

    @Override
    public long generateHeaderId(int position) {
        return -1;
    }

    public class NavigationViewHolder extends UltimateRecyclerviewViewHolder {
        ImageView image;
        TextView title;

        public NavigationViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
