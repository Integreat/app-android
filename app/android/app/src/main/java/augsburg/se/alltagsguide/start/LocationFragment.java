package augsburg.se.alltagsguide.start;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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


public class LocationFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<Location>> {

    private OnLocationFragmentInteractionListener mListener;
    private LocationAdapter mAdapter;

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
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        return new LocationLoader(getActivity());
    }


    @Override
    public void onLoadFinished(Loader<List<Location>> loader, List<Location> locations) {
        mAdapter.add(locations);
    }

    @Override
    public void onLoaderReset(Loader<List<Location>> loader) {
    }

    public interface OnLocationFragmentInteractionListener {
        void onLocationSelected(Location location);
    }

}
