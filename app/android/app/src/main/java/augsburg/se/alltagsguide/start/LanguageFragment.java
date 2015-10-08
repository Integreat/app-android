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
import android.widget.TextView;

import com.google.inject.Inject;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.network.LanguageLoader;
import augsburg.se.alltagsguide.utilities.BaseFragment;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.inject.InjectView;


public class LanguageFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<Language>> {
    private static final String ARG_LOCATION = "location";
    private Location mLocation;
    private OnLanguageFragmentInteractionListener mListener;
    private LanguageAdapter mAdapter;

    @InjectView(R.id.recycler_view)
    private SuperRecyclerView mRecyclerView;

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
        setTitle("SELECT A LANGUAGE");
        setSubTitle("What language do you speak?");
        cityTextView.setText(mLocation.getName());

        mRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        mRecyclerView.getEmptyView().setBackgroundColor(mPrefUtilities.getCurrentColor());
        mRecyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refresh();
    }

    private void refresh() {
        getLoaderManager().restartLoader(0, null, this);
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
    public Loader<List<Language>> onCreateLoader(int i, Bundle bundle) {
        return new LanguageLoader(getActivity(), mLocation);
    }

    @Override
    public void onLoadFinished(Loader<List<Language>> loader, List<Language> languages) {
        if (mAdapter == null) {
            mAdapter = new LanguageAdapter(languages, new LanguageAdapter.LanguageClickListener() {
                @Override
                public void onLanguageClick(Language language) {
                    mListener.onLanguageSelected(mLocation, language);
                }
            }, getActivity());
        } else {
            mAdapter.add(languages);
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
    public void onLoaderReset(Loader<List<Language>> loader) {
    }

    public interface OnLanguageFragmentInteractionListener {
        void onLanguageSelected(Location location, Language language);
    }

}
