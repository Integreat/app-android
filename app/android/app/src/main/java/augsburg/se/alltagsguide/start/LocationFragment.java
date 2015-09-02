package augsburg.se.alltagsguide.start;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.network.NetworkHandler;
import augsburg.se.alltagsguide.network.NetworkHandlerMock;
import augsburg.se.alltagsguide.network.SimpleCallback;
import augsburg.se.alltagsguide.utilities.BaseFragment;
import retrofit.client.Response;


public class LocationFragment extends BaseFragment {

    private OnLocationFragmentInteractionListener mListener;
    private List<Location> mLocations;

    public static LocationFragment newInstance() {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public LocationFragment() {
        // Required empty public constructor
        mLocations = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), 2));
        final LocationAdapter adapter = new LocationAdapter(mLocations, new LocationAdapter.LocationClickListener() {
            @Override
            public void onLocationClick(Location location) {
                mListener.onLocationSelected(location);
            }
        }, getActivity());
        recyclerView.setAdapter(adapter);

        NetworkHandler network = new NetworkHandlerMock();
        network.getAvailableLocations(new SimpleCallback<List<Location>>() {
            @Override
            public void onSuccess(List<Location> locations, Response response) {
                adapter.add(locations);
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle("SELECT A CITY");
        setSubTitle("Where do you live?");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLocationFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnLocationFragmentInteractionListener {
        void onLocationSelected(Location location);
    }

}
