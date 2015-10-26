package augsburg.se.alltagsguide.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.persistence.DatabaseCache;
import augsburg.se.alltagsguide.persistence.resources.LanguageResource;
import augsburg.se.alltagsguide.utilities.BasicLoader;
import augsburg.se.alltagsguide.utilities.LoadingType;
import roboguice.util.Ln;

/**
 * Created by Daniel-L on 07.09.2015.
 */
public class LanguageLoader extends BasicLoader<List<Language>> {

    @Inject
    private LanguageResource.Factory languageFactory;
    @Inject
    private DatabaseCache db;

    @NonNull
    private Location mLocation;

    /**
     * Create loader for context
     *
     * @param context
     */
    public LanguageLoader(Context context, @NonNull Location location, LoadingType loadingType) {
        super(context, loadingType);
        mLocation = location;
    }


    @Override
    public List<Language> load() {
        try {
            return get(languageFactory.under(mLocation));
        } catch (IOException e) {
            Ln.e(e);
            return Collections.emptyList();
        }
    }
}
