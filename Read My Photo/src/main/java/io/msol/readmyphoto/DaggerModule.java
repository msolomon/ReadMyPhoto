package io.msol.readmyphoto;

/**
 * Created by mike on 4/3/14.
 */

import android.content.Context;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                Application.class,
                Gallery.class,
                PhotoDetailFragment.class,
                PhotoListFragment.class
        }
)
public class DaggerModule {
    private final Application application;

    public DaggerModule(final Application application) {
        this.application = application;
    }

    @Provides @Singleton Context provideContext() {
        return application;
    }

    @Provides ImmutableList<File> providePhotos(final Gallery gallery) {
        return gallery.getPhotos();
    }

    @Provides @Singleton ExecutorService provideExecutorService() {
        return Executors.newCachedThreadPool();
    }

    @Provides @Singleton Tesseract provideTesseract(final Context context) {
        return new Tesseract(context.getAssets(), context.getFilesDir());
    }
}
