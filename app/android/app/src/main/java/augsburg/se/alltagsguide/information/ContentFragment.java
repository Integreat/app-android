package augsburg.se.alltagsguide.information;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Content;
import augsburg.se.alltagsguide.common.Information;

public class ContentFragment extends Fragment {
    private static final String ARG_CONTENT = "content";
    private Content mContent;
    private RecyclerView mRecyclerView;
    private ContentAdapter mContentAdapter;

    private OnContentFragmentInteractionListener mListener;

    public static ContentFragment newInstance(Content content) {
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    public ContentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mContent = (Content) getArguments().getSerializable(ARG_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mContentAdapter = new ContentAdapter(mContent, mListener);
        mRecyclerView.setAdapter(mContentAdapter);
        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnContentFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnContentFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void changeContent(Content content) {
        mContentAdapter.replace(content);
    }

    public interface OnContentFragmentInteractionListener {
        void onInformationClicked(Information information);
    }

}
