package augsburg.se.alltagsguide;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;

import java.io.File;
import java.util.List;

import augsburg.se.alltagsguide.common.EventCategory;
import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.persistence.DatabaseInfo;
import augsburg.se.alltagsguide.persistence.resources.AvailableLanguageResource;
import augsburg.se.alltagsguide.persistence.resources.EventCategoryResource;
import augsburg.se.alltagsguide.persistence.resources.EventPageResource;
import augsburg.se.alltagsguide.persistence.resources.EventTagResource;
import augsburg.se.alltagsguide.persistence.resources.PageResource;
import augsburg.se.alltagsguide.persistence.resources.LanguageResource;
import augsburg.se.alltagsguide.serialization.EventPageSerializer;
import augsburg.se.alltagsguide.serialization.LanguageSerializer;
import augsburg.se.alltagsguide.serialization.LocationSerializer;
import augsburg.se.alltagsguide.serialization.PageSerializer;
import retrofit.converter.GsonConverter;

/**
 * Created by Daniel-L on 01.09.2015.
 */
public class MainModule extends AbstractModule {

    /**
     * Name of database file
     */
    public static final String NAME = "cache.db";
    /**
     * Version of database
     */
    public static final int VERSION = 27;

    public MainModule() {
    }

    @Override
    protected void configure() {
        install(new ServicesModule());
        install(new FactoryModuleBuilder()
                .build(LanguageResource.Factory.class));
        install(new FactoryModuleBuilder()
                .build(PageResource.Factory.class));
        install(new FactoryModuleBuilder()
                .build(EventPageResource.Factory.class));
        install(new FactoryModuleBuilder()
                .build(EventTagResource.Factory.class));
        install(new FactoryModuleBuilder()
                .build(EventCategoryResource.Factory.class));
        install(new FactoryModuleBuilder()
                .build(AvailableLanguageResource.Factory.class));
    }

    @Provides
    DatabaseInfo getDatabaseInfo() {
        return new DatabaseInfo(NAME, VERSION);
    }

    @Provides
    GsonConverter gsonConverter() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Page>>() {
                }.getType(), new PageSerializer())
                .registerTypeAdapter(new TypeToken<List<Language>>() {
                }.getType(), new LanguageSerializer())
                .registerTypeAdapter(new TypeToken<List<Location>>() {
                }.getType(), new LocationSerializer())
                .registerTypeAdapter(new TypeToken<List<EventPage>>() {
                }.getType(), new EventPageSerializer())
                .create();
        return new GsonConverter(gson);
    }


    @Provides
    @Named("cacheDir")
    File cacheDir(Context context) {
        return new File(context.getFilesDir(), "cache");
    }


}
