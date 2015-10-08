package augsburg.se.alltagsguide.settings;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import augsburg.se.alltagsguide.R;

/**
 * Created by Daniel-L on 20.09.2015.
 */
public class PrefFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }

}