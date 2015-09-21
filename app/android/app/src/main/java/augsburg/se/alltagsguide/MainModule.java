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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.persistence.resources.PageResource;
import augsburg.se.alltagsguide.persistence.resources.LanguageResource;
import augsburg.se.alltagsguide.utilities.PageSerializer;
import retrofit.converter.GsonConverter;

/**
 * Created by Daniel-L on 01.09.2015.
 */
public class MainModule extends AbstractModule {
    public MainModule() {
    }

    @Override
    protected void configure() {
        install(new ServicesModule());
        install(new FactoryModuleBuilder()
                .build(LanguageResource.Factory.class));
        install(new FactoryModuleBuilder()
                .build(PageResource.Factory.class));
    }

    @Provides
    GsonConverter gsonConverter() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .registerTypeAdapter(new TypeToken<List<Page>>() {
                }.getType(), new PageSerializer())
                .create();
        //add custom deserializers here
        return new GsonConverter(gson);
    }


    @Provides
    @Named("cacheDir")
    File cacheDir(Context context) {
        return new File(context.getFilesDir(), "cache");
    }
}
