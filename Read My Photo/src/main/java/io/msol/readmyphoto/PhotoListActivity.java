package io.msol.readmyphoto;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.io.File;

public class PhotoListActivity extends FragmentActivity implements PhotoListFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_twopane);

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
    }
}
