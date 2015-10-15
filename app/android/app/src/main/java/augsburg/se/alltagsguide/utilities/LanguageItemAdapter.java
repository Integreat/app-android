package augsburg.se.alltagsguide.utilities;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.squareup.picasso.Picasso;

import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.AvailableLanguage;
import augsburg.se.alltagsguide.common.Language;
import roboguice.RoboGuice;

/**
 * Simple adapter example for custom items in the dialog
 */
public class LanguageItemAdapter extends BaseAdapter implements View.OnClickListener {

    private final Context mContext;
    private final List<AvailableLanguage> mItems;
    @Inject
    private Picasso mPicasso;

    public LanguageItemAdapter(Context context, List<AvailableLanguage> items) {
        mContext = context;
        mItems = items;
        RoboGuice.injectMembers(context, this);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
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
        AvailableLanguage availableLanguage = mItems.get(position);
        Language language = availableLanguage.getLoadedLanguage();

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