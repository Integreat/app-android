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


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.AvailableLanguage;
import augsburg.se.alltagsguide.common.Language;
import roboguice.RoboGuice;

/**
 * Simple adapter example for custom items in the dialog
 */
public class LanguageItemAdapter extends BaseAdapter implements View.OnClickListener {

    @NonNull private final Context mContext;
    @NonNull private final List<Language> mItems;

    @Inject
    private Picasso mPicasso;

    public LanguageItemAdapter(@NonNull Context context, @NonNull List<AvailableLanguage> items, boolean nothing) {
        mContext = context;
        List<Language> languages = new ArrayList<>();
        for (AvailableLanguage language : items) {
            languages.add(language.getLoadedLanguage());
        }
        mItems = languages;
        RoboGuice.injectMembers(context, this);
    }

    public LanguageItemAdapter(@NonNull Context context, @NonNull List<Language> items) {
        mContext = context;
        mItems = items;
        RoboGuice.injectMembers(context, this);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    @NonNull
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Language language = mItems.get(position);

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.dialog_language_item, null);
        }
        if (language == null) {
            return convertView;
        }
        ((TextView) convertView.findViewById(R.id.language_name)).setText(language.getName());
        mPicasso.load(language.getIconPath())
                .placeholder(R.drawable.icon_language_loading)
                .error(R.drawable.icon_language_loading_error)
                .fit()
                .into((ImageView) convertView.findViewById(R.id.language_icon));

        return convertView;
    }

    @Override
    public void onClick(View v) {
    }
}