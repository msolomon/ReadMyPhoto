package io.msol.readmyphoto;

import dagger.ObjectGraph;
import timber.log.Timber;

/**
 * Created by mike on 4/3/14.
 */
public class Application extends android.app.Application {
    private ObjectGraph objectGraph;

    @Override public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
        Timber.tag("Application");

        createObjectAndInject();
    }

    private void createObjectAndInject() {
        objectGraph = ObjectGraph.create(new DaggerModule(this));
        objectGraph.inject(this);
    }
}
