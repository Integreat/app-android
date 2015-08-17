package augsburg.se.alltagsguide.information;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Information;

public class InformationFragment extends Fragment {
    private static final String ARG_INFO = "info";

    private Information mInformation;

    private OnInformationFragmentInteractionListener mListener;

    public static InformationFragment newInstance(Information information) {
        InformationFragment fragment = new InformationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_INFO, information);
        fragment.setArguments(args);
        return fragment;
    }

    public InformationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mInformation = (Information) getArguments().getSerializable(ARG_INFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_information, container, false);
        TextView titleView = (TextView) rootView.findViewById(R.id.title);
        titleView.setText(mInformation.getTitle());

        TextView desriptionView = (TextView) rootView.findViewById(R.id.description);
        titleView.setText(mInformation.getDescription());

        ImageView imageView = (ImageView) rootView.findViewById(R.id.image);
        if (mInformation.getImage() != null) {
            //TODO inject
            //mInformation.getImage();
        } else {
            imageView.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnInformationFragmentInteractionListener) activity;
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

    public interface OnInformationFragmentInteractionListener {
        void onBackClicked();
    }

}
