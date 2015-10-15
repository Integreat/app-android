package augsburg.se.alltagsguide.overview;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.network.PagesLoader;
import augsburg.se.alltagsguide.utilities.BaseFragment;
import augsburg.se.alltagsguide.utilities.Objects;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.inject.InjectView;

public class PageOverviewFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<Page>> {

    @InjectView(R.id.recycler_view)
    private SuperRecyclerView mRecyclerView;
    private PageAdapter mAdapter;

    private OnPageFragmentInteractionListener mListener;
    private StaggeredGridLayoutManager mLayoutManager;
    private List<Page> mPages;

    public static PageOverviewFragment newInstance() {
        return new PageOverviewFragment();
    }

    public PageOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            mLayoutManager.setSpanCount(getSpanCount());
        }
    }


    private int getSpanCount() {
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                return mPrefUtilities.useMultipleColumnsLandscape() ? 2 : 1;
            case Configuration.ORIENTATION_PORTRAIT:
                return mPrefUtilities.useMultipleColumnsPortrait() ? 2 : 1;
        }
        return 1;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(getString(R.string.welcome_to_x, mPrefUtilities.getLocation().getName()));
        //setSubTitle("This is the subtitle");
        mLayoutManager = new StaggeredGridLayoutManager(getSpanCount(), StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.getEmptyView().setBackgroundColor(mPrefUtilities.getCurrentColor());
        mRecyclerView.setRefreshListener(this);
        addListener();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                    + " must implement OnPageFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void changePage(@NonNull Page item) {
        setOrInitPageAdapter(item.getSubPagesRecursively());
    }

    @Override
    public void onRefresh() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<List<Page>> onCreateLoader(int id, Bundle args) {
        return new PagesLoader(getActivity(), mPrefUtilities.getLocation(), mPrefUtilities.getLanguage());
    }

    @Override
    public void onLoadFinished(Loader<List<Page>> loader, final List<Page> pages) {
        mPages = pages;
        setOrInitPageAdapter(restoreVisiblePages(pages));
        mListener.onPagesLoaded(pages);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.getSwipeToRefresh().setRefreshing(false);
            }
        }, 500);
    }

    private List<Page> restoreVisiblePages(List<Page> pages) {
        int selectedPageId = mPrefUtilities.getSelectedPageId();
        if (selectedPageId >= 0) {
            List<Page> hierarchyPages = Page.filterParents(pages);
            for (Page page : hierarchyPages) {
                if (Objects.equals(selectedPageId, page.getId())) {
                    return page.getSubPagesRecursively();
                }
            }
        }
        return pages;
    }

    private void setOrInitPageAdapter(List<Page> pages) {
        Collections.sort(pages);
        if (mAdapter == null) {
            mAdapter = new PageAdapter(pages, mListener, mPrefUtilities.getCurrentColor(), getActivity());
        } else {
            mAdapter.setItems(pages);
        }
        if (mRecyclerView.getAdapter() == null) {
            mRecyclerView.setAdapter(mAdapter);
        }
    }


    @Override
    public void onLoaderReset(Loader<List<Page>> loader) {
    }

    public void filterByText(String filterText) {
        if (mPages != null) { //TODO save attributes at orientation changes
            if (Objects.isNullOrEmpty(filterText)) {
                setOrInitPageAdapter(restoreVisiblePages(mPages));
            } else {
                List<Page> pages = new ArrayList<>();
                for (Page page : mPages) {
                    String relevantContent = page.getTitle();
                    if (page.getContent() != null) {
                        relevantContent += page.getContent();
                    }

                    if (relevantContent.toLowerCase().contains(filterText.toLowerCase())) {
                        pages.add(page);
                    }
                }
                setOrInitPageAdapter(pages);
            }
        }
    }

    public interface OnPageFragmentInteractionListener {
        void onOpenPage(Page page);

        void onPagesLoaded(List<Page> pages);
    }

}
