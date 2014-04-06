package io.msol.readmyphoto;

import android.app.Activity;

/**
* Created by mike on 4/5/14.
*/
public class SimpleInjector {
    private Application application;

    public SimpleInjector(final Application application) {
        this.application = application;
    }

    public SimpleInjector(final android.app.Application application) {
        this((Application)application);
    }

    public SimpleInjector(final Activity activity) {
        this(activity.getApplication());
    }

    public void inject(final Object object) {
        application.inject(object);
    }

}
