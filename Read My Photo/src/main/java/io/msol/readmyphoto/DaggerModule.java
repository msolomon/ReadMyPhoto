package io.msol.readmyphoto;

/**
 * Created by mike on 4/3/14.
 */

import com.google.common.collect.ImmutableList;

import java.io.File;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                Application.class,
                Gallery.class,
                PhotoListFragment.class
        }
)
public class DaggerModule {
    private final Application application;

    public DaggerModule(final Application application) {
        this.application = application;
    }

//    @Singleton @Provides Context provideContext() {
//        return application;
//    }

    @Provides ImmutableList<File> providePhotos(final Gallery gallery) {
        return gallery.getPhotos();
    }
}
