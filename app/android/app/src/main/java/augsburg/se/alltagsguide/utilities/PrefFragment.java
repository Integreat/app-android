package augsburg.se.alltagsguide.utilities;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import augsburg.se.alltagsguide.R;

/**
 * Created by Daniel-L on 20.09.2015.
 */
public class PrefFragment extends PreferenceFragment {
    public static Fragment getInstance() {
        return new PrefFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}