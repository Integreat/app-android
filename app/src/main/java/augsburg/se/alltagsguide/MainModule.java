/*
 * This file is part of Integreat.
 *
 * Integreat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Integreat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Integreat.  If not, see <http://www.gnu.org/licenses/>.
 */

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

import augsburg.se.alltagsguide.common.EventPage;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;
import augsburg.se.alltagsguide.persistence.DatabaseInfo;
import augsburg.se.alltagsguide.persistence.resources.AvailableLanguageResource;
import augsburg.se.alltagsguide.persistence.resources.DisclaimerResource;
import augsburg.se.alltagsguide.persistence.resources.EventCategoryResource;
import augsburg.se.alltagsguide.persistence.resources.EventPageResource;
import augsburg.se.alltagsguide.persistence.resources.EventTagResource;
import augsburg.se.alltagsguide.persistence.resources.PageResource;
import augsburg.se.alltagsguide.persistence.resources.LanguageResource;
import augsburg.se.alltagsguide.serialization.EventPageSerializer;
import augsburg.se.alltagsguide.serialization.LanguageSerializer;
import augsburg.se.alltagsguide.serialization.LocationSerializer;
import augsburg.se.alltagsguide.serialization.PageSerializer;
import de.greenrobot.event.EventBus;
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
    public static final int VERSION = 44;

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
        install(new FactoryModuleBuilder()
                .build(DisclaimerResource.Factory.class));
        bind(EventBus.class).toInstance(EventBus.getDefault());
    }

    @Provides
    DatabaseInfo getDatabaseInfo() {
        return new DatabaseInfo(NAME, VERSION);
    }

    @Provides
    Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Page>>() {
                }.getType(), new PageSerializer())
                .registerTypeAdapter(new TypeToken<List<Language>>() {
                }.getType(), new LanguageSerializer())
                .registerTypeAdapter(new TypeToken<List<Location>>() {
                }.getType(), new LocationSerializer())
                .registerTypeAdapter(new TypeToken<List<EventPage>>() {
                }.getType(), new EventPageSerializer())
                .create();
    }

    @Provides
    GsonConverter gsonConverter(Gson gson) {
        return new GsonConverter(gson);
    }


    @Provides
    @Named("cacheDir")
    File cacheDir(Context context) {
        return new File(context.getFilesDir(), "cache");
    }


}
