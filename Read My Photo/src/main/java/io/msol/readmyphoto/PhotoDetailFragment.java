package io.msol.readmyphoto;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
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
    private static final int MIN_TIME_BETWEEN_TOASTS = 1000;

    @Inject Tesseract tesseract;
    @Inject InputMethodManager inputMethodManager;
    @Inject ClipboardManager clipboardManager;
    @Inject Context context;

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
        text.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                clipAndNotifyWithDelay(s);
            }

            @Override public void afterTextChanged(final Editable s) {
            }
        });
    }

    private void clipAndNotifyWithDelay(final CharSequence text) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("Text read from a photo", text));

        final Date now = new Date();
        final long millisecondsElapsed = now.getTime() - lastToasted.getTime();
        if (millisecondsElapsed > MIN_TIME_BETWEEN_TOASTS) {
            lastToasted = now;
            Toast toast = Toast.makeText(context, "Text copied!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 16);
            toast.show();
        }
    };

    private class KeyboardHidingFocusChangeListener implements View.OnFocusChangeListener {
        @Override public void onFocusChange(View v, boolean hasFocus){
            if (v.getId() == R.id.text && !hasFocus) {
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }
}
