package augsburg.se.alltagsguide.page;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import roboguice.RoboGuice;

/**
 * Simple adapter example for custom items in the dialog
 */
class LanguageItemAdapter extends BaseAdapter implements View.OnClickListener {

    private final Context mContext;
    private final List<Language> mItems;
    @Inject
    private Picasso mPicasso;

    public LanguageItemAdapter(Context context, List<Language> items) {
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

    Transformation transformation = new RoundedTransformationBuilder()
            .cornerRadiusDp(8)
            .oval(false)
            .build();


    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Language language = mItems.get(position);
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.dialog_language_item, null);
        }
        ((TextView) convertView.findViewById(R.id.language_name)).setText(language.getName());
        mPicasso.load(language.getIconPath())
                .transform(transformation)
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