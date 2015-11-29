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
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.network.LanguageLoader;
import augsburg.se.alltagsguide.utilities.LoadingType;
import augsburg.se.alltagsguide.utilities.ui.BaseFragment;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.inject.InjectView;


public class LanguageFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<Language>> {
    private static final String ARG_LOCATION = "location";
    private static final String LOADING_TYPE_KEY = "FORCED";
    private Location mLocation;
    private OnLanguageFragmentInteractionListener mListener;
    private LanguageAdapter mAdapter;

    @InjectView(R.id.recycler_view)
    private UltimateRecyclerView mRecyclerView;

    @InjectView(R.id.city_name)
    private TextView cityTextView;

    @Inject
    private PrefUtilities mPrefUtilities;

    public static LanguageFragment newInstance(Location location) {
        LanguageFragment fragment = new LanguageFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LOCATION, location);
        fragment.setArguments(args);
        return fragment;
    }

    public LanguageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLocation = (Location) getArguments().getSerializable(ARG_LOCATION);
        } else {
            throw new IllegalStateException("Location should not be null");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_language, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(getString(R.string.language_fragment_title));
        setSubTitle(getString(R.string.language_fragment_subtitle));
        cityTextView.setText(mLocation.getName());

        int rows = getResources().getInteger(R.integer.grid_rows_welcome);
        mRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), rows));
        mRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(LoadingType.FORCE_NETWORK);
            }
        });
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
            mListener = (OnLanguageFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLanguageFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<List<Language>> onCreateLoader(int i, Bundle args) {
        LoadingType loadingType = (LoadingType) args.getSerializable(LOADING_TYPE_KEY);
        return new LanguageLoader(getActivity(), mLocation, loadingType);
    }

    @Override
    public void onLoadFinished(Loader<List<Language>> loader, @NonNull List<Language> languages) {
        if (languages.isEmpty()){
            mRecyclerView.showEmptyView();
        }else{
            mRecyclerView.hideEmptyView();
        }
        if (mAdapter == null) {
            mAdapter = new LanguageAdapter(languages, new LanguageAdapter.LanguageClickListener() {
                @Override
                public void onLanguageClick(Language language) {
                    mListener.onLanguageSelected(mLocation, language);
                }
            }, getActivity());
        } else {
            mAdapter.setItems(languages);
        }
        if (mRecyclerView.getAdapter() == null) {
            mRecyclerView.setAdapter(mAdapter);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setRefreshing(false);
            }
        }, 500);
    }

    @Override
    public void onLoaderReset(Loader<List<Language>> loader) {
    }

    public interface OnLanguageFragmentInteractionListener {
        void onLanguageSelected(Location location, Language language);
    }

}
