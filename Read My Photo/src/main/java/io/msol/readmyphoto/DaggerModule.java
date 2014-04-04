package io.msol.readmyphoto;

/**
 * Created by mike on 4/3/14.
 */

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                Application.class,
                PhotoDetailActivity.class
        }
)
public class DaggerModule {
    private final Application application;

    public DaggerModule(final Application application) {
        this.application = application;
    }

    @Singleton @Provides Application provideApplication() {
        return application;
    }

    @Singleton @Provides Context provideContext() {
        return application;
    }
}
