package augsburg.se.alltagsguide.utilities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.inject.Inject;

import roboguice.RoboGuice;
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
        mListener.setTitle(title);
    }

    protected void setSubTitle(String subTitle) {
        mListener.setSubTitle(subTitle);
    }

    public interface OnBaseFragmentInteractionListener {
        void setTitle(String title);

        void setSubTitle(String title);
    }


}
