package augsburg.se.alltagsguide.start;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.GPSCoordinate;
import augsburg.se.alltagsguide.common.Location;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.swipe.SwipeDismissRecyclerViewTouchListener;
import com.squareup.picasso.Picasso;

import java.util.*;

/**
 * Created by Amadeus on 08. Nov. 2015.
 */
public class LocationSelectionFragment extends ViewPagerAnimationFragment implements TextWatcher, SwipeRefreshLayout.OnRefreshListener {

    private List<Location> allNearbyLocations;
    private List<Location> allLocations;
    private List<Location> filteredNearbyLocations;
    private List<Location> filteredLocations;
    private Location loadingLocation;
    private Location selectedLocation;

    private String currentFilter;

    private boolean addPadding;

    private com.malinskiy.superrecyclerview.SuperRecyclerView resultListView;
    private LocationListViewAdapter adapter;
    private EditText searchView;
    private TextView titleText;
    private TextView descriptionText;
    private TextInputLayout inputLayout;

    private boolean showTitles;
    private boolean showSearch;

    private int rowCount;

    @Nullable
    private GPSCoordinate usersLocation;
    @Nullable
    private OnLocationSelectedListener listener;
    @Nullable
    private String searchString;

    public LocationSelectionFragment() {
        this.allNearbyLocations = new ArrayList<>();
        this.filteredNearbyLocations = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_selection, container, false);

        rowCount = getContext().getResources().getInteger(R.integer.grid_rows_locations);

        // Add padding android > 4.4 to fit the screen (Height of the status bar)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT && addPadding)
            v.setPaddingRelative(0, (int) px2dp(24), 0, 0);

        this.titleText = (TextView) v.findViewById(R.id.appInstructionFragmentPageTitle);
        this.descriptionText = (TextView) v.findViewById(R.id.appInstructionFragmentPageDescription);
        this.inputLayout = (TextInputLayout) v.findViewById(R.id.appInstructionFragmentPageSearchInputLayout);
        this.resultListView = (SuperRecyclerView) v.findViewById(R.id.locationSelectionResultList);
        this.searchView = (EditText) v.findViewById(R.id.locationSelectionSearch);
        this.titleText.setVisibility((this.showTitles) ? View.VISIBLE : View.GONE);
        this.descriptionText.setVisibility((this.showTitles) ? View.VISIBLE : View.GONE);
        this.inputLayout.setVisibility((this.showSearch) ? View.VISIBLE : View.GONE);
        this.searchView.setText(searchString);
        refreshFilter();

        this.resultListView.setRefreshListener(this);

        this.adapter = new LocationListViewAdapter(this);
        this.adapter.setLocations(this.filteredLocations);
        this.adapter.setNearbyLocations(this.filteredNearbyLocations);
        this.adapter.setShowNearby(true);
        this.adapter.setScrollOffset(getScrollOffset());
        this.adapter.setRows(rowCount);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), rowCount);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case LocationListViewAdapter.VIEW_TYPE_ITEM:
                        return 1;
                    case LocationListViewAdapter.VIEW_TYPE_SECTION_TITLE:
                        return rowCount;
                    default:
                        return -1;
                }
            }
        });
        this.resultListView.setLayoutManager(gridLayoutManager);

        this.resultListView.setAdapter(this.adapter);

        this.searchView.addTextChangedListener(this);
        // ToDo: Add listener to search view and initiate searching/filtering immediately after typing
        return v;
    }

    public boolean showTitles() {
        return showTitles;
    }

    public void setShowSearch(boolean showSearch) {
        this.showSearch = showSearch;
        if (this.inputLayout != null) this.inputLayout.setVisibility((showSearch) ? View.VISIBLE : View.GONE);
    }

    public boolean showSearch() {
        return showSearch;
    }

    public void setShowTitles(boolean showTitles) {
        this.showTitles = showTitles;
        if (this.titleText != null) this.titleText.setVisibility((showTitles) ? View.VISIBLE : View.GONE);
        if (this.descriptionText != null) this.titleText.setVisibility((showTitles) ? View.VISIBLE : View.GONE);
    }

    public void selectLocation(Location location) {
        if (this.listener != null) this.listener.onLocationSelected(location);
    }

    public void refreshFilter() {
        if (this.searchView != null) applyFilter(this.searchView.getText().toString());
    }

    private void applyFilter(String filter) {
        // compute search
        if (filteredLocations == null) {
            filteredLocations = new ArrayList<>();
            if (this.adapter != null) this.adapter.setLocations(this.filteredLocations);
        } else filteredLocations.clear();

        if (filteredNearbyLocations == null) {
            filteredNearbyLocations = new ArrayList<>();
            if (this.adapter != null) this.adapter.setNearbyLocations(this.filteredNearbyLocations);
        } else filteredNearbyLocations.clear();

        if ((allLocations == null || allLocations.size() == 0) && (allNearbyLocations == null || allNearbyLocations.size() == 0))
            return;
        if (filter == null || filter.trim().length() == 0) {
            filteredLocations.addAll(allLocations);
            filteredNearbyLocations.addAll(allNearbyLocations);
        } else {
            filter = filter.toUpperCase(Locale.GERMAN).trim();
            if (allLocations != null)
                for (Location loc : allLocations)
                    if (loc.getName().toUpperCase(Locale.GERMAN).contains(filter) || loc.getDescription().toUpperCase(Locale.GERMAN).contains(filter))
                        filteredLocations.add(loc);
            if (allNearbyLocations != null)
                for (Location loc : allNearbyLocations)
                    if (loc.getName().toUpperCase(Locale.GERMAN).contains(filter) || loc.getDescription().toUpperCase(Locale.GERMAN).contains(filter))
                        filteredNearbyLocations.add(loc);
        }
        if (this.adapter != null) this.adapter.notifyDataSetChanged();
    }

    public boolean isAddPadding() {
        return addPadding;
    }

    public void setAddPadding(boolean addPadding) {
        this.addPadding = addPadding;
    }

    public List<Location> getLocations() {
        return allLocations;
    }

    public void invalidateSort() {
        if (this.allLocations == null) return;
        if (usersLocation != null) {

            // Save distances for faster sorting
            final HashMap<Integer, Double> distanceMap = new HashMap<>();
            for (Location l : allLocations) {
                if (l.getGPSCoordinate() == null) distanceMap.put(l.getId(), Double.POSITIVE_INFINITY);
                else distanceMap.put(l.getId(), usersLocation.distanceTo(l.getGPSCoordinate()));
            }

            // Sort locations by distance
            Collections.sort(allLocations, new Comparator<Location>() {
                @Override
                public int compare(Location lhs, Location rhs) {
                    if (lhs.getGPSCoordinate() == null && rhs.getGPSCoordinate() == null) return 0;
                    return distanceMap.get(lhs.getId()).compareTo(distanceMap.get(rhs.getId()));
                }
            });

            allNearbyLocations.clear();
            // Add the nearest {rowCount} Locations that are within 50km to an extra array
            for (int i = 0; i < rowCount; i++) {
                double dist = distanceMap.get(allLocations.get(i).getId());
                if (dist > 50000 || dist == Double.NaN) break;
                allNearbyLocations.add(allLocations.get(i));
            }
        }

        // Sort city by names
        Collections.sort(allLocations, new Comparator<Location>() {
            @Override
            public int compare(Location lhs, Location rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
    }

    public void setLocations(List<Location> locations) {
        this.allLocations = locations;
        invalidateSort();
        applyFilter(this.currentFilter);
        if (this.adapter != null) {
            this.adapter.setLocations(filteredLocations);
            this.adapter.setNearbyLocations(filteredNearbyLocations);
            this.adapter.setShowNearby(filteredNearbyLocations != null && filteredNearbyLocations.size() > 0);
            this.adapter.notifyDataSetChanged();
        }

    }

    @Nullable
    public GPSCoordinate getUsersLocation() {
        return usersLocation;
    }

    public void setUsersLocation(@Nullable GPSCoordinate usersLocation) {
        this.usersLocation = usersLocation;
    }

    private float px2dp(float px) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, this.getContext().getResources().getDisplayMetrics());
    }

    @Override
    public void setScrollOffset(float scrollOffset) {
        super.setScrollOffset(scrollOffset);
        if (this.adapter != null) this.adapter.setScrollOffset(scrollOffset);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && titleText != null) {
            this.titleText.setTranslationX(scrollOffset * px2dp(50));
            this.descriptionText.setTranslationX(scrollOffset * px2dp(100));
            this.inputLayout.setTranslationX(scrollOffset * px2dp(150));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        applyFilter(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void setOnLocationSelectedListener(OnLocationSelectedListener listener) {
        this.listener = listener;
    }

    public String getSearchString() {
        return (this.searchView == null) ? null : this.searchView.getText().toString();
    }

    public void setSearchString(String searchString) {
        if (this.searchView != null) this.searchView.setText(searchString);
        this.searchString = searchString;
        applyFilter(searchString);
    }

    @Override
    public void onRefresh() {
        if (this.listener != null) this.listener.onForceReloadLocations();
    }

    public void setLoadingLanguage(Location selectedLocation, boolean selected) {
        if(selected) {
            this.adapter.setLoadingLocation(selectedLocation);
        } else {
            this.adapter.setLoadingLocation(null);
        }
    }

    public void setLocationSingleSelected(Location selectedLocation) {
        this.adapter.setSelectedLocation(selectedLocation);
    }

    public interface OnLocationSelectedListener {
        void onLocationSelected(Location location);

        void onForceReloadLocations();
    }
}

class LocationListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_SECTION_TITLE = 2;

    private LocationSelectionFragment context;
    private LayoutInflater inflater;
    private List<Location> locations;
    private List<Location> nearbyLocations;
    private Picasso mPicasso;
    private boolean showNearby;
    private float scrollOffset;
    private int rowsCount;
    private Location loadingLocation;
    private Location selectedLocation;

    public LocationListViewAdapter(LocationSelectionFragment context) {
        this.context = context;
        this.inflater = LayoutInflater.from(this.context.getContext());
        this.mPicasso = Picasso.with(this.context.getContext());
    }

    public Location getLoadingLocation() {
        return loadingLocation;
    }

    public void setLoadingLocation(Location loadingLocation) {
        this.loadingLocation = loadingLocation;
        notifyDataSetChanged();
    }

    public Location getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(Location selectedLocation) {
        this.selectedLocation = selectedLocation;
        notifyDataSetChanged();
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Location> getNearbyLocations() {
        return nearbyLocations;
    }

    public void setNearbyLocations(List<Location> nearbyLocations) {
        this.nearbyLocations = nearbyLocations;
    }

    public boolean isShowNearby() {
        return showNearby;
    }

    public void setShowNearby(boolean showNearby) {
        this.showNearby = showNearby;
    }

    private boolean showNearby() {
        return !(this.showNearby == false || this.nearbyLocations == null || this.nearbyLocations.size() == 0);
    }

    public void setScrollOffset(float scrollOffset) {
        this.scrollOffset = scrollOffset;
        notifyDataSetChanged(); // ToDo: Make animation more efficient!
    }

    public Location getLocationFromPosition(int pos) {
        if (!showNearby()) return locations.get(pos);
        if (pos == 0 || pos == nearbyLocations.size() + 1) return null;

        if (pos <= nearbyLocations.size()) {
            pos -= 1;
            return nearbyLocations.get(pos);
        } else {
            pos -= 2 + nearbyLocations.size();
            return locations.get(pos);

        }
    }

    public String getSectionTitle(int position) {
        if (position == 0) {
            return "In deiner Nähe";
        } else if (position == nearbyLocations.size() + 1) {
            return "Alle Städte";
        }
        return "";
    }

    @Override
    public int getItemViewType(int position) {
        if (!showNearby()) return VIEW_TYPE_ITEM;
        if (position == 0 || position == this.nearbyLocations.size() + 1) return VIEW_TYPE_SECTION_TITLE;
        return VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // ToDo: Different ViewHolder for titles
        switch (viewType) {
            case VIEW_TYPE_ITEM:
                return new LocationSelectionViewHolder(inflater.inflate(R.layout.location_item_view, parent, false), this.context);
            case VIEW_TYPE_SECTION_TITLE:
                return new SectionHeaderViewHolder(inflater.inflate(R.layout.section_title, parent, false));
        }
        return null;
    }

    private float px2dp(float px) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, this.context.getResources().getDisplayMetrics());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LocationSelectionViewHolder) {
            LocationSelectionViewHolder locationViewHolder = (LocationSelectionViewHolder) holder;
            Location location = getLocationFromPosition(position);
            locationViewHolder.location = location;

            locationViewHolder.cityName.setText(location.getName());
            this.mPicasso.load(location.getCityImage())
                    .placeholder(R.drawable.icon_location_loading)
                    .error(R.drawable.icon_location_loading_error)
                    .into(locationViewHolder.previewImage);

            if(selectedLocation == location) {
                locationViewHolder.selectedImage.setVisibility(View.VISIBLE);
            } else {
                locationViewHolder.selectedImage.setVisibility(View.GONE);
            }
            if(loadingLocation == location) {
                locationViewHolder.loadingImage.setVisibility(View.VISIBLE);
            } else {
                locationViewHolder.loadingImage.setVisibility(View.GONE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                locationViewHolder.container.setTranslationX(scrollOffset * (float) Math.sqrt(position + 1) * px2dp(150));
        } else if (holder instanceof SectionHeaderViewHolder) {
            SectionHeaderViewHolder headerViewHolder = (SectionHeaderViewHolder) holder;
            headerViewHolder.sectionTitle.setText(getSectionTitle(position));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                headerViewHolder.container.setTranslationX(scrollOffset * (float) Math.log(position + 2) * px2dp(150));
        }
    }

    @Override
    public int getItemCount() {
        if (locations == null && showNearby() == false) return 0;
        else return (showNearby()) ? nearbyLocations.size() + locations.size() + 2 : locations.size();
    }


    public void setRows(int rows) {
        this.rowsCount = rows;
    }
}


class LocationSelectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public CardView container;
    public ImageView previewImage;
    public TextView cityName;
    public Location location;
    private LocationSelectionFragment frag;
    public ImageView selectedImage;
    public ProgressBar loadingImage;

    public LocationSelectionViewHolder(View itemView, LocationSelectionFragment frag) {
        super(itemView);
        previewImage = (ImageView) itemView.findViewById(R.id.image);
        cityName = (TextView) itemView.findViewById(R.id.title);
        this.container = (CardView) itemView.findViewById(R.id.container);
        this.selectedImage = (ImageView) itemView.findViewById(R.id.selectedItem);
        this.loadingImage = (ProgressBar) itemView.findViewById(R.id.progressBar);

        this.container.setOnClickListener(this);
        this.frag = frag;
    }

    @Override
    public void onClick(View v) {
        frag.selectLocation(this.location);
    }
}

class SectionHeaderViewHolder extends RecyclerView.ViewHolder {

    public TextView sectionTitle;
    public LinearLayout container;

    public SectionHeaderViewHolder(View itemView) {
        super(itemView);
        this.sectionTitle = (TextView) itemView.findViewById(R.id.sectionTitle);
        this.container = (LinearLayout) itemView.findViewById(R.id.container);
    }
}