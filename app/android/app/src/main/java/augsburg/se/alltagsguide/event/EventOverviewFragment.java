package augsburg.se.alltagsguide.event;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.network.EventPagesLoader;
import augsburg.se.alltagsguide.utilities.BaseFragment;
import augsburg.se.alltagsguide.utilities.MyLinearLayoutManager;
import augsburg.se.alltagsguide.utilities.Objects;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.inject.InjectView;

/**
 * Created by Daniel-L on 10.10.2015.
 */
public class EventOverviewFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<EventPage>> {

    private static final String EVENT_PAGE_KEY = "EVENT_PAGE_KEY";
    private static final String FORCED_KEY = "FORCED";

    @InjectView(R.id.recycler_view)
    private RecyclerView mRecyclerView;

    private EventPageAdapter mAdapter;

    @Inject
    private PrefUtilities mPrefUtilities;

    private OnEventPageFragmentInteractionListener mListener;

    @NonNull private List<EventPage> mEventPages = new ArrayList<>();

    @Override
    public Loader<List<EventPage>> onCreateLoader(int i, Bundle args) {
        boolean forced = false;
        if (args != null && args.containsKey(FORCED_KEY)) {
            forced = args.getBoolean(FORCED_KEY);
        }
        return new EventPagesLoader(getActivity(), mPrefUtilities.getLocation(), mPrefUtilities.getLanguage(), forced);
    }

    @Override
    public void onLoadFinished(Loader<List<EventPage>> loader, List<EventPage> eventPages) {
        pagesLoaded(eventPages);
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void refresh(boolean forced) {
        Bundle bundle = null;
        if (forced) {
            bundle = new Bundle();
            bundle.putBoolean(FORCED_KEY, true);
        }
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
        refresh(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_pages, container, false);
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
        if (mAdapter == null) {
            mAdapter = new EventPageAdapter(eventPages, mListener, mPrefUtilities.getCurrentColor(), getActivity());
        } else {
            mAdapter.setItems(eventPages);
        }
        if (mRecyclerView.getAdapter() == null) {
            mRecyclerView.setAdapter(mAdapter);
        }
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
}
