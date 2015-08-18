package augsburg.se.alltagsguide.start;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.network.NetworkHandler;
import augsburg.se.alltagsguide.network.NetworkHandlerMock;
import augsburg.se.alltagsguide.utilities.PrefUtilities;


public class LanguageFragment extends Fragment {
    private static final String ARG_LOCATION = "location";

    private Location mLocation;

    private OnLanguageFragmentInteractionListener mListener;

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_language, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), 2));
        NetworkHandler network = new NetworkHandlerMock();
        LanguageAdapter adapter = new LanguageAdapter(network.getAvailableLanguages(mLocation), new LanguageAdapter.LanguageClickListener() {
            @Override
            public void onLanguageClick(Language language) {
                mListener.onLanguageSelected(mLocation, language);
            }
        }, getActivity());
        recyclerView.setAdapter(adapter);


        TextView cityText = (TextView) rootView.findViewById(R.id.city_name);
        cityText.setText(mLocation.getName());
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("SELECT A LANGUAGE");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("What language do you speak?");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLanguageFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLanguageFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnLanguageFragmentInteractionListener {
        void onLanguageSelected(Location location, Language language);
    }

}
