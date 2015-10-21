package augsburg.se.alltagsguide.utilities;


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
                .placeholder(R.drawable.ic_location_not_found_black)
                .error(R.drawable.ic_location_not_found_black)
                .fit()
                .into((ImageView) convertView.findViewById(R.id.language_icon));

        return convertView;
    }

    @Override
    public void onClick(View v) {
        Integer index = (Integer) v.getTag();
    }
}