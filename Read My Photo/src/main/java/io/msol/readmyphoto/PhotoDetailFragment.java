package io.msol.readmyphoto;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class PhotoDetailFragment extends Fragment {
    public static final String FILE_PATH = "FILE_PATH";
    private static final long int MIN_TIME_BETWEEN_TOASTS = 1000;

    @Inject Tesseract tesseract;
    @Inject InputMethodManager inputMethodManager;
    @Inject ClipboardManager clipboardManager;

    @InjectView(R.id.text) TextView text;
    @InjectView(R.id.progress) ProgressBar progress;

    private String filePath;
    private Tesseract.OCRTask executingTask;

    private Date lastToasted = new Date();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new SimpleInjector(getActivity()).inject(this);

        if (!getArguments().containsKey(FILE_PATH)) {
            throw new RuntimeException("Tried to get detail view without attaching filePath");
        }

        filePath = getArguments().getString(FILE_PATH);

        beginImageOCR();
    }

    private void beginImageOCR() {
        showSpinner();
        cancelAnyRunningTask();

        final String originalFilePath = filePath;
        executingTask = tesseract.readImage(filePath, new Tesseract.Callback() {
            @Override public void onOCRComplete(final String readText) {
                if (filePath != originalFilePath) {
                    Timber.i("This fragment doesn't match the one requesting OCR. Result:" + readText);
                } else {
                    progress.setVisibility(View.INVISIBLE);
                    text.setVisibility(View.VISIBLE);
                    text.setText(readText);
                }
            }
        });
    }

    private void cancelAnyRunningTask() {
        if (executingTask != null) {
            executingTask.cancel(true);
        }
    }

    private void showSpinner() {
        if (text != null && progress != null) {
            text.setVisibility(View.INVISIBLE);
            progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_detail, container, false);
        ImageView imageView = ButterKnife.findById(rootView, R.id.image);

        Picasso.with(getActivity()).load(new File(filePath)).centerInside().resize(container.getMeasuredWidth(), container.getMeasuredHeight()).into(imageView);

        showSpinner();

        return rootView;
    }

    @Override public void onStart() {
        super.onStart();

        ButterKnife.inject(this, getView());
        text.setOnFocusChangeListener(new KeyboardHidingFocusChangeListener());
    }

    private void clipAndNotifyWithDelay(final String text) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("Text read from a photo", text));

        final Date now = new Date();
        if (now.getTime() - lastToasted.getTime() < MIN_TIME_BETWEEN_TOASTS) {
            lastToasted = now;
            Toast.makeText(getActivity(), "Text copied!", Toast.LENGTH_SHORT);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    clipAndNotifyWithDelay(text);
                }
            }, MIN_TIME_BETWEEN_TOASTS);
        }
    },

    private class KeyboardHidingFocusChangeListener implements View.OnFocusChangeListener {
        @Override public void onFocusChange(View v, boolean hasFocus){
            if (v.getId() == R.id.text && !hasFocus) {
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }
}
