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

package augsburg.se.alltagsguide.start;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.graphics.ColorUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.network.LanguageLoader;
import augsburg.se.alltagsguide.utilities.BaseListFragment;
import augsburg.se.alltagsguide.utilities.LoadingType;
import augsburg.se.alltagsguide.utilities.ui.BaseAdapter;
import roboguice.inject.InjectView;


public class LanguageFragment extends BaseListFragment<Language> {
    private static final String ARG_LOCATION = "location";
    private Location mLocation;
    private OnLanguageFragmentInteractionListener mListener;
    private LanguageAdapter mAdapter;

    @InjectView(R.id.city_name)
    private TextView cityTextView;

    public static LanguageFragment newInstance(Location location) {
        LanguageFragment fragment = new LanguageFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LOCATION, location);
        fragment.setArguments(args);
        return fragment;
    }

    public LanguageFragment() {
        // Required empty public constructor
    }

    @Override
    protected String getScreenName() {
        if (mLocation != null) {
            return String.format("Language-Overview(%s)", mLocation.getName());
        }
        return "Language-Overview";
    }

    @Override
    @ColorInt
    protected int getBackgroundColor() {
        return ContextCompat.getColor(getActivity(), R.color.primary);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLocation = (Location) getArguments().getSerializable(ARG_LOCATION);
            if (mLocation == null) {
                throw new IllegalStateException("Location should not be null");
            }
            sendEvent("Location", mLocation.getName());
        } else {
            throw new IllegalStateException("Location should not be null");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_language, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(getString(R.string.language_fragment_title));
        setSubTitle(getString(R.string.language_fragment_subtitle));
        cityTextView.setText(mLocation.getName());
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnLanguageFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLanguageFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public int getRows() {
        return getResources().getInteger(R.integer.grid_rows_welcome);
    }

    @Override
    public Loader<List<Language>> getLoader(LoadingType loadingType) {
        return new LanguageLoader(getActivity(), mLocation, loadingType);
    }

    @Override
    public void loaded() {

    }

    @Override
    public void onLoaderReset(Loader<List<Language>> loader) {
    }

    public interface OnLanguageFragmentInteractionListener {
        void onLanguageSelected(Location location, Language language);
    }

    @Override
    public BaseAdapter getOrCreateAdapter(List<Language> items) {
        if (mAdapter == null) {
            mAdapter = new LanguageAdapter(items, new LanguageAdapter.LanguageClickListener() {
                @Override
                public void onLanguageClick(Language language) {
                    mListener.onLanguageSelected(mLocation, language);
                }
            }, getActivity());
        } else {
            mAdapter.setItems(items);
        }
        return mAdapter;
    }
}
