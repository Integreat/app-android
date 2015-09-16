package augsburg.se.alltagsguide;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;

import java.io.File;

import augsburg.se.alltagsguide.persistence.resources.ArticleResource;
import augsburg.se.alltagsguide.persistence.resources.CategoryResource;
import augsburg.se.alltagsguide.persistence.resources.LanguageResource;
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
                .build(CategoryResource.Factory.class));
        install(new FactoryModuleBuilder()
                .build(ArticleResource.Factory.class));
    }

    @Provides
    GsonConverter gsonConverter() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
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
