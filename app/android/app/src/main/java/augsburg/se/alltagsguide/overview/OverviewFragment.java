package augsburg.se.alltagsguide.overview;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.network.PagesLoader;
import augsburg.se.alltagsguide.utilities.BaseFragment;
import augsburg.se.alltagsguide.utilities.EmptyRecyclerView;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.inject.InjectView;

public class OverviewFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<Page>> {
    private ArrayList<Page> mPages;

    @InjectView(R.id.recycler_view)
    private EmptyRecyclerView mRecyclerView;

    @InjectView(R.id.emptyView)
    private View mEmptyView;

    @InjectView(R.id.swipe_refresh)
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private PageAdapter mPageAdapter;

    @Inject
    private PrefUtilities mPrefUtilities;

    private OnPageFragmentInteractionListener mListener;
    private StaggeredGridLayoutManager mLayoutManager;
    private boolean shouldRefreshWhenInitialized = false;

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPages = new ArrayList<>();
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
        mSwipeRefreshLayout.setOnRefreshListener(this);
        if (shouldRefreshWhenInitialized) {
            mSwipeRefreshLayout.setRefreshing(true);
            shouldRefreshWhenInitialized = false;
        }
        mPageAdapter = new PageAdapter(mPages, mListener, mPrefUtilities.getCurrentColor());
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.setAdapter(mPageAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        onRefresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pages, container, false);
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
        mPageAdapter.setItems(item.getSubPagesRecursively());
    }

    @Override
    public void onRefresh() {
        mListener.onRefreshOverview();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<Page>> onCreateLoader(int id, Bundle args) {
        return new PagesLoader(getActivity(), mPrefUtilities.getLocation(), mPrefUtilities.getLanguage());
    }

    @Override
    public void onLoadFinished(Loader<List<Page>> loader, final List<Page> pages) {
        mListener.onPagesLoaded(pages);
        mPageAdapter.setItems(pages);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<List<Page>> loader) {
    }


    public interface OnPageFragmentInteractionListener {
        void onOpenPage(Page page);

        void onPagesLoaded(List<Page> pages);

        void onRefreshOverview();
    }

}
