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

package augsburg.se.alltagsguide.event;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.network.EventPagesLoader;
import augsburg.se.alltagsguide.utilities.LoadingType;
import augsburg.se.alltagsguide.utilities.Objects;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import augsburg.se.alltagsguide.utilities.ui.BaseFragment;
import roboguice.inject.InjectView;

/**
 * Created by Daniel-L
 * on 10.10.2015
 */
public class EventOverviewFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<EventPage>>, SwipeRefreshLayout.OnRefreshListener {

    private static final String EVENT_PAGE_KEY = "EVENT_PAGE_KEY";
    private static final String LOADING_TYPE_KEY = "FORCED";

    @InjectView(R.id.recycler_view)
    private UltimateRecyclerView mRecyclerView;

    private EventPageAdapter mAdapter;

    @Inject
    private PrefUtilities mPrefUtilities;

    private OnEventPageFragmentInteractionListener mListener;
    private StaggeredGridLayoutManager mLayoutManager;

    @NonNull private List<EventPage> mEventPages = new ArrayList<>();

    @Override
    public Loader<List<EventPage>> onCreateLoader(int i, Bundle args) {
        LoadingType loadingType = (LoadingType) args.getSerializable(LOADING_TYPE_KEY);
        mRecyclerView.setRefreshing(true);
        return new EventPagesLoader(getActivity(), mPrefUtilities.getLocation(), mPrefUtilities.getLanguage(), loadingType);
    }

    @Override
    public void onLoadFinished(Loader<List<EventPage>> loader, List<EventPage> eventPages) {
        pagesLoaded(eventPages);
        mRecyclerView.setRefreshing(false);
    }

    private void pagesLoaded(List<EventPage> eventPages) {
        mEventPages = eventPages;
        setOrInitPageAdapter(eventPages);
        mListener.onEventPagesLoaded(eventPages);
    }


    @Override
    public void onLoaderReset(Loader<List<EventPage>> loader) {

    }

    public static EventOverviewFragment newInstance() {
        return new EventOverviewFragment();
    }

    public EventOverviewFragment() {
        // Required empty public constructor
    }


    private int getSpanCount() {
        return getResources().getInteger(R.integer.grid_rows_page);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLayoutManager = new StaggeredGridLayoutManager(getSpanCount(), StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setDefaultOnRefreshListener(this);
        mRecyclerView.setBackgroundColor(mPrefUtilities.getCurrentColor());
    }

    public void refresh(LoadingType type) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(LOADING_TYPE_KEY, type);
        getLoaderManager().restartLoader(0, bundle, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            Serializable serializable = savedInstanceState.getSerializable(EVENT_PAGE_KEY);
            if (serializable != null) {
                pagesLoaded((ArrayList<EventPage>) serializable);
                return;
            }
        }
        refresh(LoadingType.FORCE_DATABASE);
        refresh(LoadingType.NETWORK_OR_DATABASE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_pages, container, false);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnEventPageFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnEventPageFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void filterByText(String filterText) {
        if (Objects.isNullOrEmpty(filterText)) {
            setOrInitPageAdapter(mEventPages);
        } else {
            List<EventPage> pages = new ArrayList<>();
            for (EventPage page : mEventPages) {
                String relevantContent = page.getSearchableString();
                if (relevantContent.toLowerCase().contains(filterText.toLowerCase())) {
                    pages.add(page);
                }
            }
            setOrInitPageAdapter(pages);
        }
    }

    private void setOrInitPageAdapter(@NonNull List<EventPage> eventPages) {
        if (eventPages.isEmpty()){
            mRecyclerView.showEmptyView();
            eventPages = new ArrayList<>();
        }else{
            mRecyclerView.hideEmptyView();
        }
        if (mAdapter == null) {
            mAdapter = new EventPageAdapter(eventPages, mListener, mPrefUtilities.getCurrentColor(), getActivity());
        } else {
            mAdapter.setItems(new ArrayList<Page>(eventPages));
        }
        if (mRecyclerView.getAdapter() == null) {
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onRefresh() {
        refresh(LoadingType.FORCE_NETWORK);
    }

    public interface OnEventPageFragmentInteractionListener {
        void onOpenEventPage(EventPage page);

        void onEventPagesLoaded(List<EventPage> pages);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(EVENT_PAGE_KEY, new ArrayList<>(mEventPages));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void networkStateSwitchedToOnline() {
        if (mEventPages.isEmpty()) {
            mRecyclerView.setRefreshing(true);
            refresh(LoadingType.NETWORK_OR_DATABASE);
        }
        Log.d("EventPageOverview", "Network state switched");
    }
}
