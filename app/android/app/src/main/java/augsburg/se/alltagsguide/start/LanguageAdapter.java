package augsburg.se.alltagsguide.start;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

    private List<Language> mLanguages;
    private LanguageClickListener mListener;
    private Context mContext;

    public interface LanguageClickListener {
        void onLanguageClick(Language language);
    }

    public LanguageAdapter(List<Language> languages, LanguageClickListener listener, Context context) {
        mLanguages = languages;
        mListener = listener;
        mContext = context;
    }

    @Override
    public LanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LanguageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.language_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(LanguageViewHolder holder, int position) {
        final Language language = mLanguages.get(position);
        holder.title.setText(language.getName());
        Picasso.with(mContext)
                .load(language.getIconPath())
                .placeholder(R.drawable.placeholder_language)
                .error(R.drawable.placeholder_language)
                .fit()
                .into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLanguageClick(language);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLanguages.size();
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
