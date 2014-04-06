package io.msol.readmyphoto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class PhotoDetailFragment extends Fragment {
    public static final String FILE_PATH = "FILE_PATH";

    @Inject Tesseract tesseract;

    @InjectView(R.id.text) TextView text;
    @InjectView(R.id.progress) ProgressBar progress;

    private String filePath;

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

        final String originalFilePath = filePath;
        tesseract.readImage(filePath, new Tesseract.Callback() {
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
    }
}
