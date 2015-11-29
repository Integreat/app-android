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

package augsburg.se.alltagsguide.utilities.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.inject.Inject;

import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.fragment.RoboFragment;


public class BaseFragment extends RoboFragment {

    @Inject
    protected PrefUtilities mPrefUtilities;

    private OnBaseFragmentInteractionListener mListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context context = getActivity();
        if (!(context instanceof AppCompatActivity)) {
            throw new IllegalStateException("Activity needs to be AppCompatActivity");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnBaseFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLanguageFragmentInteractionListener");
        }
    }

    protected void setTitle(String title) {
        if (mListener != null) {
            mListener.setTitle(title);
        }
    }

    protected void setSubTitle(String subTitle) {
        if (mListener != null) {
            mListener.setSubTitle(subTitle);
        }
    }

    public interface OnBaseFragmentInteractionListener {
        void setTitle(String title);

        void setSubTitle(String title);
    }


}
