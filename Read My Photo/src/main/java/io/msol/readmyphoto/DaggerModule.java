package io.msol.readmyphoto;

/**
 * Created by mike on 4/3/14.
 */

import android.content.ClipboardManager;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

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

    @Provides @Singleton Tesseract provideTesseract(final Context context) {
        return new Tesseract(context.getAssets(), context.getFilesDir());
    }

    @Provides @Singleton InputMethodManager provideInputMethodManager(final Context context) {
        return (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Provides @Singleton ClipboardManager provideClipboardManager(final Context context) {
        return (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

}
