package augsburg.se.alltagsguide.utilities;

import android.app.Activity;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Daniel-L on 01.09.2015.
 */
public class BaseFragment extends Fragment {

    private ActionBar mActionBar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof AppCompatActivity)) {
            throw new IllegalStateException("Activity needs to be AppCompatActivity");
        }
        mActionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (mActionBar == null) {
            throw new IllegalStateException("ActionBar is null");
        }
    }

    protected void setTitle(String title) {
        mActionBar.setTitle(title);
    }

    protected void setSubTitle(String subTitle) {
        mActionBar.setSubtitle(subTitle);
    }
}
