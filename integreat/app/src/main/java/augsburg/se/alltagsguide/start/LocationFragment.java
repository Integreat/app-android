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
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.inject.Inject;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.network.LocationLoader;
import augsburg.se.alltagsguide.utilities.LoadingType;
import augsburg.se.alltagsguide.utilities.Objects;
import augsburg.se.alltagsguide.utilities.ui.BaseFragment;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import de.greenrobot.event.EventBus;
import roboguice.inject.InjectView;


public class LocationFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<Location>>, TextWatcher {
    private static final String LOADING_TYPE_KEY = "FORCED";
    private OnLocationFragmentInteractionListener mListener;
    private LocationAdapter mAdapter;

    @InjectView(R.id.recycler_view)
    private UltimateRecyclerView mRecyclerView;

    @InjectView(R.id.locationSelectionSearch)
    private EditText mSearchView;

    @Inject
    private PrefUtilities mPrefUtilities;


    private List<Location> mLocations;
    private String mFilterText;

    public static LocationFragment newInstance() {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(getString(R.string.location_fragment_title));
        setSubTitle(getString(R.string.location_fragment_subtitle));

        int rows = getResources().getInteger(R.integer.grid_rows_welcome);
        mRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), rows));
        mRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(LoadingType.FORCE_NETWORK);
            }
        });
        mSearchView.addTextChangedListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refresh(LoadingType.NETWORK_OR_DATABASE);
    }

    public void refresh(LoadingType type) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(LOADING_TYPE_KEY, type);
        getLoaderManager().restartLoader(0, bundle, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnLocationFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<List<Location>> onCreateLoader(int i, Bundle args) {
        LoadingType loadingType = (LoadingType) args.getSerializable(LOADING_TYPE_KEY);
        return new LocationLoader(getActivity(), loadingType);
    }


    @Override
    public void onLoadFinished(Loader<List<Location>> loader, List<Location> locations) {
        mLocations = locations;
        updateAdapter();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setRefreshing(false);
            }
        }, 500);
    }

    private void updateAdapter() {
        List<Location> filtered = filterLocations();
        if (filtered.isEmpty()){
            mRecyclerView.showEmptyView();
        }else{
            mRecyclerView.hideEmptyView();
        }
        if (mAdapter == null) {
            mAdapter = new LocationAdapter(filtered, new LocationAdapter.LocationClickListener() {
                @Override
                public void onLocationClick(Location location) {
                    mListener.onLocationSelected(location);
                }
            }, getActivity());
        } else {
            mAdapter.setItems(filtered);
        }
        if (mRecyclerView.getAdapter() == null) {
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Location>> loader) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mFilterText = s.toString();
        updateAdapter();
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public interface OnLocationFragmentInteractionListener {
        void onLocationSelected(Location location);
    }

    private List<Location> filterLocations() {
        List<Location> filteredLocations = new ArrayList<>();
        if (mLocations == null) {
            return filteredLocations;
        }
        if (Objects.isNullOrEmpty(mFilterText)) {
            return mLocations;
        }
        for (Location location : mLocations) {
            if (Objects.containsIgnoreCase(location.getSearchString(), mFilterText)) {
                filteredLocations.add(location);
            }
        }
        return filteredLocations;
    }

    @Override
    public void networkStateSwitchedToOnline() {
        if (mLocations.isEmpty()) {
            mRecyclerView.setRefreshing(true);
            refresh(LoadingType.NETWORK_OR_DATABASE);
        }
    }
}
