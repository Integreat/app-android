/*
 * This file is part of Integreat.
 *
 * Integreat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Integreat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Integreat.  If not, see <http://www.gnu.org/licenses/>.
 */

package augsburg.se.alltagsguide.page;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.network.PagesLoader;
import augsburg.se.alltagsguide.utilities.ui.BaseFragment;
import augsburg.se.alltagsguide.utilities.LoadingType;
import augsburg.se.alltagsguide.utilities.Objects;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.inject.InjectView;

public class PageOverviewFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<Page>> {
    private static final String PAGE_KEY = "PAGE_KEY";
    private static final String LOADING_TYPE_KEY = "FORCED";

    @InjectView(R.id.recycler_view)
    private UltimateRecyclerView mRecyclerView;
    private PageAdapter mAdapter;

    private OnPageFragmentInteractionListener mListener;
    private StaggeredGridLayoutManager mLayoutManager;
    @NonNull private List<Page> mPages = new ArrayList<>();

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
        return getResources().getInteger(R.integer.grid_rows_page);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDefaultTitle();
        //setSubTitle("This is the subtitle");
        mLayoutManager = new StaggeredGridLayoutManager(getSpanCount(), StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setDefaultOnRefreshListener(this);
        mRecyclerView.setBackgroundColor(mPrefUtilities.getCurrentColor());
        addListener();
    }

    private void setDefaultTitle() {
        Location location = mPrefUtilities.getLocation();
        if (location != null) {
            setTitle(getString(R.string.refguide, mPrefUtilities.getLocation().getName()));
        } else {
            setTitle("");
        }
        setSubTitle("");
    }

    private void setCategoryTitle(Page page) {
        setTitle("");
        setSubTitle(page.getTitle());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            Serializable serializable = savedInstanceState.getSerializable(PAGE_KEY);
            if (serializable != null) {
                pagesLoaded((ArrayList<Page>) serializable);
                return;
            }
        }
        refresh(LoadingType.FORCE_DATABASE);
        refresh(LoadingType.NETWORK_OR_DATABASE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pages, container, false);
        return rootView;
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

    @Override
    public void onRefresh() {
        refresh(LoadingType.FORCE_NETWORK);
    }

    public void refresh(LoadingType type) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(LOADING_TYPE_KEY, type);
        getLoaderManager().restartLoader(0, bundle, this);
    }

    public void indexUpdated() {
        setOrInitPageAdapter(restoreVisiblePages(mPages));
    }


    @Override
    public Loader<List<Page>> onCreateLoader(int id, Bundle args) {
        LoadingType loadingType = (LoadingType) args.getSerializable(LOADING_TYPE_KEY);
        mRecyclerView.setRefreshing(true);
        return new PagesLoader(getActivity(), mPrefUtilities.getLocation(), mPrefUtilities.getLanguage(), loadingType);
    }

    @Override
    public void onLoadFinished(Loader<List<Page>> loader, final List<Page> pages) {
        pagesLoaded(pages);
        mRecyclerView.setRefreshing(false);
    }

    private void pagesLoaded(List<Page> pages) {
        mPages = pages;
        setOrInitPageAdapter(restoreVisiblePages(pages));
        mListener.onPagesLoaded(pages);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setRefreshing(false);
            }
        }, 500);
    }

    private List<Page> restoreVisiblePages(List<Page> pages) {
        int selectedPageId = mPrefUtilities.getSelectedPageId();
        if (selectedPageId >= 0) {
            List<Page> hierarchyPages = Page.filterParents(pages);
            for (Page page : hierarchyPages) {
                if (Objects.equals(selectedPageId, page.getId())) {
                    setCategoryTitle(page);
                    return page.getSubPagesRecursively();
                }
            }
        }
        setDefaultTitle();
        return pages;
    }

    private void setOrInitPageAdapter(@NonNull List<Page> pages) {
        if (pages.isEmpty()){
            mRecyclerView.showEmptyView();
        }else{
            mRecyclerView.hideEmptyView();
        }
        if (mAdapter == null) {
            mAdapter = new PageAdapter(pages, mListener, mPrefUtilities.getCurrentColor(), getActivity());
        } else {
            mAdapter.setItems(pages);
        }
        if (mRecyclerView.getAdapter() == null) {
            mRecyclerView.setAdapter(mAdapter);
        }
        mListener.onSetItemsChanged();
    }


    @Override
    public void onLoaderReset(Loader<List<Page>> loader) {
    }

    public void filterByText(String filterText) {
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(PAGE_KEY, new ArrayList<>(mPages));
        super.onSaveInstanceState(outState);
    }

    public boolean goBack() {
        if (canGoBack()) {
            mPrefUtilities.setSelectedPage(-1);
            indexUpdated();
            return true;
        }
        return false;
    }

    public boolean canGoBack() {
        return mPrefUtilities.getSelectedPageId() != -1;
    }

    public interface OnPageFragmentInteractionListener {
        void onOpenPage(Page page);

        void onPagesLoaded(List<Page> pages);

        void onSetItemsChanged();
    }

    @Override
    public void networkStateSwitchedToOnline() {
        if (mPages.isEmpty()) {
            mRecyclerView.setRefreshing(true);
            refresh(LoadingType.NETWORK_OR_DATABASE);
        }
        Log.d("PageOverviewFragment", "Network state switched");
    }

}
