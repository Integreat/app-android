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

public class PageAdapter extends BaseAdapter<PageAdapter.ContentViewHolder, Page> {

    private PageOverviewFragment.OnPageFragmentInteractionListener mListener;
    @ColorInt private int mColor;

    @NonNull private SimpleDateFormat dateFormatTo;
    @Inject private Picasso mPicasso;
    private Transformation mTransformation;

    public PageAdapter(@NonNull List<Page> pages, PageOverviewFragment.OnPageFragmentInteractionListener listener, @ColorInt int primaryColor, @NonNull Context context) {
        super(pages, context);
        mListener = listener;
        mColor = primaryColor;
        mContext = context;
        dateFormatTo = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY);
        mTransformation = new BitmapColorTransformation(mColor);
    }

    @Override
    public ContentViewHolder getViewHolder(View view) {
        return new ContentViewHolder(view);
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent) {
        return new ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.page_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ContentViewHolder contentHolder, int position) {
        final Page page = get(position);
        contentHolder.title.setText(page.getTitle());
        contentHolder.title.setTextColor(mColor);
        String desc = page.getDescription();
        contentHolder.date.setText(dateFormatTo.format(page.getModified()));
        contentHolder.description.setText(Html.fromHtml(desc));
        contentHolder.description.setVisibility(Objects.isNullOrEmpty(desc) ? View.GONE : View.VISIBLE);
        contentHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onOpenPageRecursively(page);
            }
        });
        //contentHolder.more.setTextColor(mColor);
        contentHolder.date.setTextColor(mColor);

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

    public class ContentViewHolder extends UltimateRecyclerviewViewHolder {
        TextView title;
        TextView description;
        ImageView image;
        //TextView more;
        TextView date;

        public ContentViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            image = (ImageView) itemView.findViewById(R.id.image);
            //more = (TextView) itemView.findViewById(R.id.more);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }
}
