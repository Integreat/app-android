package augsburg.se.alltagsguide;

import android.content.Context;

import java.io.File;

/**
 * Created by Daniel-L on 01.09.2015.
 */
public class MainModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new ServicesModule());
        install(new FactoryModuleBuilder().build(SyncCampaign.Factory.class));
        install(new FactoryModuleBuilder()
                .build(OrganizationRepositories.Factory.class));
        install(AccountScope.module());
    }

    @Provides
    @Named("cacheDir")
    File cacheDir(Context context) {
        return new File(context.getFilesDir(), "cache");
    }
}
