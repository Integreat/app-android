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

package augsburg.se.alltagsguide.page;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.utilities.Objects;
import augsburg.se.alltagsguide.utilities.ui.BaseAdapter;
import augsburg.se.alltagsguide.utilities.ui.BitmapColorTransformation;

public class PageAdapter extends BaseAdapter<PageAdapter.PageViewHolder, Page> {

    private PageOverviewFragment.OnPageFragmentInteractionListener mListener;
    @ColorInt
    private int mColor;
    @Inject
    private Picasso mPicasso;
    private Transformation mTransformation;

    public enum ViewMode {CARD, INFO}

    private ViewMode viewMode = ViewMode.CARD;

    public PageAdapter(@NonNull List<Page> pages, PageOverviewFragment.OnPageFragmentInteractionListener listener, @ColorInt int primaryColor, @NonNull Context context) {
        super(pages, context);
        mListener = listener;
        mColor = primaryColor;
        mContext = context;
        mTransformation = new BitmapColorTransformation(mColor);
    }

    public void setViewMode(ViewMode mode){
        if (viewMode != mode){
            viewMode = mode;
            notifyDataSetChanged(); //update everything
        }
    }

    @Override
    public PageViewHolder getViewHolder(View view) {
        if (viewMode == ViewMode.CARD) {
            return new ParentPageViewHolder(view);
        }
        return new SubPageViewHolder(view);
    }

    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent) {
        if (viewMode == ViewMode.CARD) {
            return new ParentPageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_page_item, parent, false));
        }
        return new SubPageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.page_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PageViewHolder contentHolder, int position) {
        final Page page = get(position);
        String desc = page.getDescription();
        if (viewMode == ViewMode.INFO) {
            SubPageViewHolder viewHolder = (SubPageViewHolder) contentHolder;
            viewHolder.description.setText(Html.fromHtml(desc));
            viewHolder.description.setVisibility(Objects.isNullOrEmpty(desc) ? View.GONE : View.VISIBLE);
        }

        contentHolder.title.setText(page.getTitle());
        contentHolder.title.setTextColor(mColor);
        contentHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onOpenPage(page);
            }
        });

        if (!Objects.isNullOrEmpty(page.getThumbnail())) {
            RequestCreator creator = mPicasso.load(page.getThumbnail());
            creator.transform(mTransformation)
                    .fit()
                    .centerInside()
                    .into(contentHolder.image);
            contentHolder.image.setVisibility(View.VISIBLE);
        } else {
            contentHolder.image.setVisibility(View.GONE);
        }
    }

    public abstract class PageViewHolder extends UltimateRecyclerviewViewHolder {
        public TextView title;
        public ImageView image;

        public PageViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    public class ParentPageViewHolder extends PageViewHolder {
        public ParentPageViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class SubPageViewHolder extends PageViewHolder {
        public TextView description;

        public SubPageViewHolder(View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.description);
        }
    }
}
