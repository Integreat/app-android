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

package augsburg.se.alltagsguide.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.google.inject.Inject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.network.DisclaimersLoader;
import augsburg.se.alltagsguide.utilities.LoadingType;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.RoboGuice;

/**
 * Created by Daniel-L on 20.09.2015.
 */
public class PrefFragment extends PreferenceFragmentCompat  implements
        LoaderManager.LoaderCallbacks<List<Page>>{
    private static final String LOADING_TYPE_KEY = "FORCED_PREF";
    private static final String PAGE_KEY = "PAGE_KEY";

    @Inject
    protected PrefUtilities mPrefUtilities;
    @NonNull private List<Page> mPages = new ArrayList<>();

    public OnPreferenceListener mListener;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public Loader<List<Page>> onCreateLoader(int id, Bundle args) {
        LoadingType loadingType = (LoadingType) args.getSerializable(LOADING_TYPE_KEY);
        return new DisclaimersLoader(getActivity(), mPrefUtilities.getLocation(), mPrefUtilities.getLanguage(), loadingType);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
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

    public void refresh(LoadingType type) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(LOADING_TYPE_KEY, type);
        getLoaderManager().restartLoader(0, bundle, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnPreferenceListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnPageFragmentInteractionListener");
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Page>> loader, List<Page> pages) {
        pagesLoaded(pages);
    }

    private void pagesLoaded(List<Page> pages) {
        mPages = pages;
        PreferenceCategory category = (PreferenceCategory) findPreference("disclaimer");
        category.removeAll();
        for (Page page : pages){
            final Page mPage = page;
            Preference button = new Preference(getActivity());
            button.setTitle(page.getTitle());
            button.setSummary(page.getDescription());
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    mListener.onOpenPage(mPage);
                    return true;
                }
            });
            category.addPreference(button);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Page>> loader) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(PAGE_KEY, new ArrayList<>(mPages));
        super.onSaveInstanceState(outState);
    }

    public interface OnPreferenceListener {
        void onOpenPage(Page page);
    }
}