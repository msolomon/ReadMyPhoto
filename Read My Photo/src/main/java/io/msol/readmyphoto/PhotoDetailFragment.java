package io.msol.readmyphoto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.ButterKnife;

public class PhotoDetailFragment extends Fragment {
    public static final String FILE_PATH = "FILE_PATH";

    private String filePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getArguments().containsKey(FILE_PATH)) {
            throw new RuntimeException("Tried to get detail view without attaching filePath");
        }

        filePath = getArguments().getString(FILE_PATH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_detail, container, false);
        ImageView imageView = ButterKnife.findById(rootView, R.id.image);

        Picasso.with(getActivity()).load(new File(filePath)).centerInside().resize(container.getMeasuredWidth(), container.getMeasuredHeight()).into(imageView);

        return rootView;
    }
}
