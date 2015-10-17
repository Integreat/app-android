package augsburg.se.alltagsguide.event;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;


import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.network.EventPagesLoader;
import augsburg.se.alltagsguide.utilities.BaseFragment;
import augsburg.se.alltagsguide.utilities.MyLinearLayoutManager;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.inject.InjectView;

/**
 * Created by Daniel-L on 10.10.2015.
 */
public class EventOverviewFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<EventPage>> {

    @InjectView(R.id.recycler_view)
    private RecyclerView mRecyclerView;

    private EventPageAdapter mAdapter;

    @Inject
    private PrefUtilities mPrefUtilities;

    private OnEventPageFragmentInteractionListener mListener;

    //TODO categories, tags

    @Override
    public Loader<List<EventPage>> onCreateLoader(int i, Bundle bundle) {
        return new EventPagesLoader(getActivity(), mPrefUtilities.getLocation(), mPrefUtilities.getLanguage());
    }

    @Override
    public void onLoadFinished(Loader<List<EventPage>> loader, List<EventPage> eventPages) {
        if (mAdapter == null) {
            mAdapter = new EventPageAdapter(eventPages, mListener, mPrefUtilities.getCurrentColor(), getActivity());
        } else {
            mAdapter.setItems(eventPages);
        }
        if (mRecyclerView.getAdapter() == null) {
            mRecyclerView.setAdapter(mAdapter);
        }
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

    public void onRefresh() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh();
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

    public void changeEventPages(List<EventPage> items) {
        if (mAdapter != null) {
            mAdapter.setItems(items);
        }
    }

    public interface OnEventPageFragmentInteractionListener {
        void onOpenEventPage(EventPage page);

        void onEventPagesLoaded(List<EventPage> pages);
    }
}
