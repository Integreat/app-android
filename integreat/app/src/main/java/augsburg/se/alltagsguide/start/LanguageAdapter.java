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

package augsburg.se.alltagsguide.start;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.utilities.ui.BaseAdapter;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class LanguageAdapter extends BaseAdapter<LanguageAdapter.LanguageViewHolder, Language> {

    @NonNull private LanguageClickListener mListener;

    @Inject private Picasso mPicasso;


    public interface LanguageClickListener {
        void onLanguageClick(Language language);
    }

    public LanguageAdapter(@NonNull List<Language> languages, @NonNull LanguageClickListener listener, @NonNull Context context) {
        super(languages, context);
        mListener = listener;
    }


    @Override
    public LanguageViewHolder getViewHolder(View view) {
        return new LanguageViewHolder(view);
    }

    @Override
    public LanguageViewHolder onCreateViewHolder(ViewGroup parent) {
        return new LanguageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.language_item_view, parent, false));
    }

    Transformation transformation = new RoundedTransformationBuilder()
            .cornerRadiusDp(8)
            .oval(false)
            .build();

    @Override
    public void onBindViewHolder(LanguageViewHolder holder, int position) {
        final Language language = get(position);
        holder.title.setText(language.getName());
        mPicasso.load(language.getIconPath())
                .transform(transformation)
                .placeholder(R.drawable.icon_language_loading)
                .error(R.drawable.icon_language_loading_error)
                .fit()
                .centerInside()
                .into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLanguageClick(language);
            }
        });
    }

    public class LanguageViewHolder extends UltimateRecyclerviewViewHolder {
        private TextView title;
        private ImageView image;

        public LanguageViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
