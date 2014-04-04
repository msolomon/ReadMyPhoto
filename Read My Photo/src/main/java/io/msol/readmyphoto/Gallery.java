package io.msol.readmyphoto;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

/**
 * Created by mike on 4/3/14.
 */
public class Gallery {

    public void getPhotos(Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        String[] columns = new String[] {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.TITLE,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.MIME_TYPE,
                MediaStore.Images.ImageColumns.SIZE };

        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns, null, null, null);

        cursor.getColumnCount();

    }

}
