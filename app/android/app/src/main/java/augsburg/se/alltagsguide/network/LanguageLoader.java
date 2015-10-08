package augsburg.se.alltagsguide.network;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.persistence.DatabaseCache;
import augsburg.se.alltagsguide.persistence.resources.LanguageResource;
import augsburg.se.alltagsguide.utilities.BasicLoader;
import roboguice.util.Ln;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class LanguageLoader extends BasicLoader<List<Language>> {

    @Inject
    private LanguageResource.Factory languageFactory;
    @Inject
    private DatabaseCache db;

    private Location mLocation;

    /**
     * Create loader for context
     *
     * @param activity
     */
    @Inject
    public LanguageLoader(Activity activity, @NonNull Location location) {
        super(activity);
        mLocation = location;
    }


    @Override
    public List<Language> load() {
        try {
            List<Language> items = db.loadOrRequest(languageFactory.under(mLocation));
            if (items == null) {
                items = new ArrayList<>();
            }
            return items;
        } catch (IOException e) {
            Ln.e(e);
            return Collections.emptyList();
        }
    }
}
