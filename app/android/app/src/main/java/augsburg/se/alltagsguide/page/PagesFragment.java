package augsburg.se.alltagsguide.page;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.utilities.BaseFragment;
import augsburg.se.alltagsguide.utilities.EmptyRecyclerView;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.inject.InjectView;

public class PagesFragment extends BaseFragment {
    private static final String ARG_CONTENT = "content";
    private ArrayList<Page> mPages;

    @InjectView(R.id.recycler_view)
    private EmptyRecyclerView mRecyclerView;

    @InjectView(R.id.emptyView)
    private View mEmptyView;

    private PageAdapter mPageAdapter;

    @Inject
    private PrefUtilities mPrefUtilities;

    private OnPageFragmentInteractionListener mListener;
    private StaggeredGridLayoutManager mLayoutManager;

    public static PagesFragment newInstance(List<Page> pages) {
        PagesFragment fragment = new PagesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONTENT, new ArrayList<>(pages));
        fragment.setArguments(args);
        return fragment;
    }

    public PagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPages = (ArrayList<Page>) getArguments().getSerializable(ARG_CONTENT);
        }
        addListener();
    }

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private void addListener() {
        if (listener == null) {
            listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    switch (key) {
                        case PrefUtilities.MULTIPLE_COLUMNS_LANDSCAPE:
                            gridSettingsChanged();
                            break;
                        case PrefUtilities.MULTIPLE_COLUMNS_PORTRAIT:
                            gridSettingsChanged();
                            break;
                    }
                }
            };
            mPrefUtilities.addListener(listener);
        }
    }

    private void gridSettingsChanged() {
        updateColumnCount();
    }

    private void updateColumnCount() {
        if (mLayoutManager != null && isAdded()) {
            switch (getResources().getConfiguration().orientation) {
                case Configuration.ORIENTATION_LANDSCAPE:
                    boolean useMultipleLandscape = mPrefUtilities.useMultipleColumnsLandscape();
                    mLayoutManager.setSpanCount(useMultipleLandscape ? 2 : 1);
                    break;
                case Configuration.ORIENTATION_PORTRAIT:
                    boolean useMultiplePortrait = mPrefUtilities.useMultipleColumnsPortrait();
                    mLayoutManager.setSpanCount(useMultiplePortrait ? 2 : 1);
                    break;
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPageAdapter = new PageAdapter(mPages, mListener, mPrefUtilities.getCurrentColor());
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.setAdapter(mPageAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnPageFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnContentFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void changePage(Page item) {

    }

    public interface OnPageFragmentInteractionListener {
        void onOpenPage(Page page);
    }

}
