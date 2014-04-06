package io.msol.readmyphoto;

import javax.inject.Inject;

import dagger.ObjectGraph;
import timber.log.Timber;

/**
 * Created by mike on 4/3/14.
 */
public class Application extends android.app.Application {
    private ObjectGraph objectGraph;

    @Inject Gallery gallery;

    @Override public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        createObjectGraph();

        inject(this);
    }

    private void createObjectGraph() {
        objectGraph = ObjectGraph.create(new DaggerModule(this));
    }

    public void inject(Object injectionTarget) {
        objectGraph.inject(injectionTarget);
    }
}
