package augsburg.se.alltagsguide.start;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.inject.Inject;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.utilities.BaseAdapter;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.RoboGuice;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class LanguageAdapter extends BaseAdapter<LanguageAdapter.LanguageViewHolder, Language> {

    private LanguageClickListener mListener;
    private Context mContext;

    @Inject
    private Picasso mPicasso;


    public interface LanguageClickListener {
        void onLanguageClick(Language language);
    }

    public LanguageAdapter(List<Language> languages, LanguageClickListener listener, Context context) {
        super(languages);
        mListener = listener;
        mContext = context;
        RoboGuice.injectMembers(context, this);
    }

    @Override
    public LanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
                .placeholder(R.drawable.ic_location_not_found_black)
                .error(R.drawable.ic_location_not_found_black)
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

    public class LanguageViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView image;

        public LanguageViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
