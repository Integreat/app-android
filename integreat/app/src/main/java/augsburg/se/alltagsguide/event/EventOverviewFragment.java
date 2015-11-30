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
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.network.EventPagesLoader;
import augsburg.se.alltagsguide.utilities.BaseListFragment;
import augsburg.se.alltagsguide.utilities.LoadingType;
import augsburg.se.alltagsguide.utilities.Objects;
import augsburg.se.alltagsguide.utilities.ui.BaseAdapter;

/**
 * Created by Daniel-L
 * on 10.10.2015
 */
public class EventOverviewFragment extends BaseListFragment<EventPage> {
    private EventPageAdapter mAdapter;

    private OnEventPageFragmentInteractionListener mListener;

    public Loader<List<EventPage>> getLoader(LoadingType loadingType) {
        return new EventPagesLoader(getActivity(), mPrefUtilities.getLocation(), mPrefUtilities.getLanguage(), loadingType);
    }

    @Override
    public void loaded() {
        mListener.onEventPagesLoaded(mList);
    }

    @Override
    public BaseAdapter getOrCreateAdapter(List<EventPage> items) {
        if (mAdapter == null) {
            mAdapter = new EventPageAdapter(items, mListener, mPrefUtilities.getCurrentColor(), getActivity());
        } else {
            mAdapter.setItems(new ArrayList<Page>(items));
        }
        return mAdapter;
    }

    public static EventOverviewFragment newInstance() {
        return new EventOverviewFragment();
    }

    public EventOverviewFragment() {
        // Required empty public constructor
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
            setOrInitPageAdapter(mList);
        } else {
            List<EventPage> pages = new ArrayList<>();
            for (EventPage page : mList) {
                String relevantContent = page.getSearchableString();
                if (relevantContent.toLowerCase().contains(filterText.toLowerCase())) {
                    pages.add(page);
                }
            }
            setOrInitPageAdapter(pages);
        }
    }

    public interface OnEventPageFragmentInteractionListener {
        void onOpenEventPage(EventPage page);

        void onEventPagesLoaded(List<EventPage> pages);
    }
}
