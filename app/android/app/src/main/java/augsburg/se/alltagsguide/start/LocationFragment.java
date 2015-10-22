package augsburg.se.alltagsguide.start;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.network.LocationLoader;
import augsburg.se.alltagsguide.utilities.BaseFragment;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.inject.InjectView;


public class LocationFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<Location>> {

    private static final String FORCED_KEY = "FORCED";
    private OnLocationFragmentInteractionListener mListener;
    private LocationAdapter mAdapter;

    @InjectView(R.id.recycler_view)
    private SuperRecyclerView mRecyclerView;

    @Inject
    private PrefUtilities mPrefUtilities;

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
        mRecyclerView.getEmptyView().setBackgroundColor(mPrefUtilities.getCurrentColor());
        mRecyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(true);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refresh(false);
    }

    private void refresh(boolean forced) {
        Bundle bundle = null;
        if (forced) {
            bundle = new Bundle();
            bundle.putBoolean(FORCED_KEY, true);
        }
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
        boolean forced = false;
        if (args != null && args.containsKey(FORCED_KEY)) {
            forced = args.getBoolean(FORCED_KEY);
        }
        return new LocationLoader(getActivity(), forced);
    }


    @Override
    public void onLoadFinished(Loader<List<Location>> loader, List<Location> locations) {
        if (mAdapter == null) {
            mAdapter = new LocationAdapter(locations, new LocationAdapter.LocationClickListener() {
                @Override
                public void onLocationClick(Location location) {
                    mListener.onLocationSelected(location);
                }
            }, getActivity());
        } else {
            mAdapter.setItems(locations);
        }
        if (mRecyclerView.getAdapter() == null) {
            mRecyclerView.setAdapter(mAdapter);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.getSwipeToRefresh().setRefreshing(false);
            }
        }, 500);
    }

    @Override
    public void onLoaderReset(Loader<List<Location>> loader) {
    }

    public interface OnLocationFragmentInteractionListener {
        void onLocationSelected(Location location);
    }

}
