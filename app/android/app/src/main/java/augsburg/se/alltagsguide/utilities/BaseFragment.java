package augsburg.se.alltagsguide.utilities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import roboguice.activity.RoboActionBarActivity;
import roboguice.fragment.RoboFragment;


public class BaseFragment extends RoboFragment {

    private ActionBar mActionBar;
    private String title;
    private String subTitle;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context context = getActivity();
        if (!(context instanceof AppCompatActivity)) {
            throw new IllegalStateException("Activity needs to be AppCompatActivity");
        }
        mActionBar = ((RoboActionBarActivity) context).getSupportActionBar();
        if (mActionBar == null) {
            throw new IllegalStateException("ActionBar is null");
        } else {
            if (title != null) {
                mActionBar.setTitle(title);
            }
            if (subTitle != null) {
                mActionBar.setSubtitle(subTitle);
            }
        }
    }


    protected void setTitle(String title) {
        this.title = title;
        if (mActionBar != null) {
            mActionBar.setTitle(title);
        }
    }

    protected void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        if (mActionBar != null) {
            mActionBar.setSubtitle(subTitle);
        }
    }
}
