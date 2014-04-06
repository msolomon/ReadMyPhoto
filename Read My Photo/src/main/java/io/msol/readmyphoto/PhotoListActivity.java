package io.msol.readmyphoto;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PhotoListActivity extends FragmentActivity implements PhotoListFragment.Callbacks {
    @InjectView(R.id.photo_detail_container) ViewGroup photoDetailContainer;

    private PhotoRequestor photoRequestor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_twopane);

        photoRequestor = new PhotoRequestor(this);

        ButterKnife.inject(this);

        if (photoDetailContainer.getChildCount() == 0) {
            ImageView instructions = (ImageView)LayoutInflater.from(this).inflate(R.layout.instructions, photoDetailContainer, false);
            photoDetailContainer.addView(instructions);
            Picasso.with(this).load(R.drawable.instructions).into(instructions);
        }

        ((PhotoListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.photo_list))
                .setActivateOnItemClick(true);
    }

    @Override
    public void onItemSelected(File file) {
        Bundle arguments = new Bundle();
        arguments.putString(PhotoDetailFragment.FILE_PATH, file.getAbsolutePath());

        PhotoDetailFragment fragment = new PhotoDetailFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.photo_detail_container, fragment)
                .commit();

        photoDetailContainer.removeAllViews();
    }

    @Override protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PhotoRequestor.REQUEST_TAKE_PHOTO) {
            photoRequestor.galleryAddPic();

            ((PhotoListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.photo_list)).fetchPhotos();
        }
    }

    @OnClick(R.id.camera_button)
    public void buttonCameraPressed() {
        photoRequestor.dispatchTakePictureIntent();
    }
}
