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

import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.network.LocationLoader;
import augsburg.se.alltagsguide.utilities.BaseFragment;
import augsburg.se.alltagsguide.utilities.EmptyRecyclerView;
import roboguice.inject.InjectView;


public class LocationFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<Location>>, SwipeRefreshLayout.OnRefreshListener {

    private OnLocationFragmentInteractionListener mListener;
    private LocationAdapter mAdapter;

    @InjectView(R.id.swipe_refresh)
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @InjectView(R.id.recycler_view)
    private EmptyRecyclerView mRecyclerView;

    @InjectView(R.id.emptyView)
    private View mEmptyView;

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
        mSwipeRefreshLayout.setOnRefreshListener(this);
        setTitle("SELECT A CITY");
        setSubTitle("Where do you live?");

        mRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));

        mAdapter = new LocationAdapter(new LocationAdapter.LocationClickListener() {
            @Override
            public void onLocationClick(Location location) {
                mListener.onLocationSelected(location);
            }
        }, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setEmptyView(mEmptyView);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh();
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
    public Loader<List<Location>> onCreateLoader(int i, Bundle bundle) {
        Loader<List<Location>> loader = new LocationLoader(getActivity());
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        return loader;
    }


    @Override
    public void onLoadFinished(Loader<List<Location>> loader, List<Location> locations) {
        mAdapter.add(locations);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    @Override
    public void onLoaderReset(Loader<List<Location>> loader) {
    }

    @Override
    public void onRefresh() {
        getLoaderManager().initLoader(0, null, this);
    }

    public interface OnLocationFragmentInteractionListener {
        void onLocationSelected(Location location);
    }

}
