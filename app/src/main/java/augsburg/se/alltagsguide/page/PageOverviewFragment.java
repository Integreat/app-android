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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.network.PagesLoader;
import augsburg.se.alltagsguide.utilities.BaseListFragment;
import augsburg.se.alltagsguide.utilities.ui.BaseAdapter;
import augsburg.se.alltagsguide.utilities.LoadingType;
import augsburg.se.alltagsguide.utilities.Objects;

public class PageOverviewFragment extends BaseListFragment<Page> {

    private PageAdapter mPageAdapter;
    private String mFilterText;

    private OnPageFragmentInteractionListener mListener;

    public static PageOverviewFragment newInstance() {
        return new PageOverviewFragment();
    }

    public PageOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDefaultTitle();
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

    public void indexUpdated() {
        setOrInitPageAdapter(mList);
    }

    @Override
    public Loader<List<Page>> getLoader(LoadingType loadingType) {
        return new PagesLoader(getActivity(), mPrefUtilities.getLocation(), mPrefUtilities.getLanguage(), loadingType);
    }

    @Override
    public void loaded() {
        mListener.onPagesLoaded(mList);
        mListener.onSetItemsChanged();
    }

    private List<Page> restoreVisiblePages(List<Page> pages) {
        int currentPageId = mPrefUtilities.getSelectedPageId();
        boolean selectCurrentPage = currentPageId != -1;
        Page foundPage = null;
        if (selectCurrentPage) {
            //check if page exists
            for (Page page : pages) {
                if (page.getId() == currentPageId) {
                    foundPage = page;
                    break;
                }
            }
        }
        if (foundPage == null || foundPage.getSubPages().isEmpty()) {
            //select first page with children
            for (Page page : pages) {
                foundPage = page;
                if (!page.getSubPages().isEmpty()) {
                    break;
                }
            }
        }
        if (foundPage != null) {
            currentPageId = foundPage.getId();
        }

        if (currentPageId >= 0) {
            mPrefUtilities.setSelectedPage(currentPageId);
            mListener.onPageIndexChanged(currentPageId);
            List<Page> hierarchyPages = Page.filterParents(pages);
            for (Page page : hierarchyPages) {
                if (Objects.equals(currentPageId, page.getId())) {
                    setCategoryTitle(page);
                    return page.getSubPagesRecursively();
                }
            }
        }
        setDefaultTitle();
        return pages;
    }


    @Override
    public BaseAdapter getOrCreateAdapter(List<Page> items) {
        if (mPageAdapter == null) {
            mPageAdapter = new PageAdapter(items, mListener, mPrefUtilities.getCurrentColor(), getActivity());
        } else {
            mPageAdapter.setItems(items);
        }
        return mPageAdapter;
    }

    @Override
    protected void setOrInitPageAdapter(@NonNull List<Page> elements) {
        super.setOrInitPageAdapter(filter(elements));
    }


    public List<Page> filter(List<Page> elements) {
        if (Objects.isNullOrEmpty(mFilterText)) {
            return restoreVisiblePages(elements);
        } else {
            List<Page> pages = new ArrayList<>();
            for (Page page : elements) {
                String relevantContent = page.getTitle();
                if (page.getContent() != null) {
                    relevantContent += page.getContent();
                }
                if (relevantContent.toLowerCase().contains(mFilterText.toLowerCase())) {
                    pages.add(page);
                }
            }
            return pages;
        }
    }

    public void filterByText(String filterText) {
        mFilterText = filterText;
        setOrInitPageAdapter(mList);
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

        void onPageIndexChanged(int index);
    }

}
