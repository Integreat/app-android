package augsburg.se.alltagsguide.start;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Amadeus on 28. Nov. 2015.
 * <p/>
 * (c) November 2015 by Amadeus Gebauer
 */
public class LanguageSelectionFragment extends ViewPagerAnimationFragment implements SwipeRefreshLayout.OnRefreshListener {

    private TextView tvTitle;
    private TextView tvDescription;
    private SuperRecyclerView recyclerView;
    private LanguageListViewAdapter adapter;
    private List<Language> languages;
    private int rowCount;
    private OnLanguageSelectedListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.adapter = new LanguageListViewAdapter(this);
        this.adapter.setLanguages(this.languages);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_language_selection, container, false);


        tvTitle = (TextView) v.findViewById(R.id.appInstructionFragmentPageTitle);
        tvDescription = (TextView) v.findViewById(R.id.appInstructionFragmentPageDescription);
        recyclerView = (SuperRecyclerView) v.findViewById(R.id.languageSelectionResultList);
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setRefreshListener(this);

        rowCount = getContext().getResources().getInteger(R.integer.grid_rows_locations);

        this.adapter.setRows(rowCount);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), rowCount);
        this.recyclerView.setLayoutManager(gridLayoutManager);

        return v;
    }

    public void notifyDataSetChanged() {
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setScrollOffset(float scrollOffset) {
        super.setScrollOffset(scrollOffset);
        if (this.adapter != null) this.adapter.setScrollOffset(scrollOffset);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && tvTitle != null) {
            this.tvTitle.setTranslationX(scrollOffset * px2dp(50));
            this.tvDescription.setTranslationX(scrollOffset * px2dp(100));
        }
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
        if (this.adapter != null) {
            this.adapter.setLanguages(this.languages);
            notifyDataSetChanged();
        }
    }

    private float px2dp(float px) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, this.getContext().getResources().getDisplayMetrics());
    }

    public void selectLanguage(Language language) {
        if(this.listener != null) this.listener.onLanguageSelected(language);
    }

    public void setOnLanguageSelectedListener(OnLanguageSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onRefresh() {
        if(this.listener != null) this.listener.onForceReloadLanguages();
    }

    public interface OnLanguageSelectedListener {
        void onLanguageSelected(Language language);
        void onForceReloadLanguages();
    }
}


class LanguageListViewAdapter extends RecyclerView.Adapter<LanguageSelectionViewHolder> {

    private LanguageSelectionFragment context;
    private LayoutInflater inflater;
    private List<Language> languages;
    private Picasso mPicasso;
    private float scrollOffset;
    private int rowsCount;

    public LanguageListViewAdapter(LanguageSelectionFragment context) {
        this.context = context;
        this.inflater = LayoutInflater.from(this.context.getContext());
        this.mPicasso = Picasso.with(this.context.getContext());
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public int getRowsCount() {
        return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
        this.rowsCount = rowsCount;
    }

    public void setScrollOffset(float scrollOffset) {
        this.scrollOffset = scrollOffset;
        notifyDataSetChanged();
    }

    public Language getLocationFromPosition(int pos) {
        return this.languages.get(pos);
    }

    @Override
    public LanguageSelectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LanguageSelectionViewHolder(inflater.inflate(R.layout.language_item_view, parent, false), this.context);
    }

    private float px2dp(float px) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, this.context.getResources().getDisplayMetrics());
    }

    @Override
    public void onBindViewHolder(LanguageSelectionViewHolder holder, int position) {
        holder.language = languages.get(position);
        holder.languageName.setText(holder.language.getName());
        this.mPicasso.load(holder.language.getIconPath())
                .placeholder(R.drawable.icon_location_loading)
                .error(R.drawable.icon_location_loading_error)
                .into(holder.previewImage);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            holder.container.setTranslationX(scrollOffset * (float) Math.sqrt(position + 1) * px2dp(150));
    }

    @Override
    public int getItemCount() {
        if (languages == null) return 0;
        return languages.size();
    }


    public void setRows(int rows) {
        this.rowsCount = rows;
    }
}


class LanguageSelectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public CardView container;
    public ImageView previewImage;
    public TextView languageName;
    public Language language;
    private LanguageSelectionFragment frag;

    public LanguageSelectionViewHolder(View itemView, LanguageSelectionFragment frag) {
        super(itemView);
        previewImage = (ImageView) itemView.findViewById(R.id.image);
        languageName = (TextView) itemView.findViewById(R.id.title);
        this.container = (CardView) itemView.findViewById(R.id.container);
        this.container.setOnClickListener(this);
        this.frag = frag;
    }

    @Override
    public void onClick(View v) {
        frag.selectLanguage(this.language);
    }
}