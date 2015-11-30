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
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.network.LocationLoader;
import augsburg.se.alltagsguide.utilities.BaseListFragment;
import augsburg.se.alltagsguide.utilities.LoadingType;
import augsburg.se.alltagsguide.utilities.Objects;
import augsburg.se.alltagsguide.utilities.ui.BaseAdapter;
import roboguice.inject.InjectView;


public class LocationFragment extends BaseListFragment<Location> implements TextWatcher {
    private OnLocationFragmentInteractionListener mListener;
    private LocationAdapter mAdapter;

    @InjectView(R.id.locationSelectionSearch)
    private EditText mSearchView;

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

        mSearchView.addTextChangedListener(this);
    }

    @Override
    public int getRows() {
        return getResources().getInteger(R.integer.grid_rows_welcome);
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
    public Loader<List<Location>> getLoader(LoadingType loadingType) {
        return new LocationLoader(getActivity(), loadingType);
    }

    @Override
    public void loaded() {

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
        setOrInitPageAdapter(mList);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public interface OnLocationFragmentInteractionListener {
        void onLocationSelected(Location location);
    }

    @Override
    protected void setOrInitPageAdapter(@NonNull List<Location> elements) {
        super.setOrInitPageAdapter(filterLocations(elements));
    }

    private List<Location> filterLocations(List<Location> elements) {
        List<Location> filteredLocations = new ArrayList<>();
        if (elements == null) {
            return filteredLocations;
        }
        if (Objects.isNullOrEmpty(mFilterText)) {
            return elements;
        }
        for (Location location : elements) {
            if (Objects.containsIgnoreCase(location.getSearchString(), mFilterText)) {
                filteredLocations.add(location);
            }
        }
        return filteredLocations;
    }

    @Override
    public BaseAdapter getOrCreateAdapter(List<Location> items) {
        if (mAdapter == null) {
            mAdapter = new LocationAdapter(items, new LocationAdapter.LocationClickListener() {
                @Override
                public void onLocationClick(Location location) {
                    mListener.onLocationSelected(location);
                }
            }, getActivity());
        } else {
            mAdapter.setItems(items);
        }
        return mAdapter;
    }
}
