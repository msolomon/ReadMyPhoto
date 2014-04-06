package io.msol.readmyphoto;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import butterknife.ButterKnife;

/**
 * Created by mike on 4/3/14.
 */
@Singleton
public class Gallery {
    @Inject public Gallery() {
    }

    public ImmutableList<File> getPhotos() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        FluentIterable<File> externalStorageFiles = Files.fileTreeTraverser().preOrderTraversal(externalStorageDirectory);

        return externalStorageFiles
                .filter(new IsPhotoPredicate())
                .toSortedList(new LastModifiedFileComparator());
    }

    public PhotoAdapter getPhotoAdapter(final Context context, final ImmutableList<File> photos) {
        return new PhotoAdapter(context, photos);
    }


    public class PhotoAdapter extends BaseAdapter {
        private final Context context;
        private final ImmutableList<File> photos;
        private final Picasso picasso;

        private PhotoAdapter(final Context context, final ImmutableList<File> photos) {
            this.context = context;
            this.photos = photos;
            picasso = Picasso.with(context);
        }

        @Override public int getCount() {
            return photos.size();
        }

        @Override public Object getItem(final int position) {
            return photos.get(position);
        }

        @Override public long getItemId(final int position) {
            return position;
        }

        @Override public View getView(final int position, final View convertView, final ViewGroup parent) {
            View view = (convertView == null) ? LayoutInflater.from(context).inflate(R.layout.list_item_image, parent, false) : convertView;

            ImageView imageView = ButterKnife.findById(view, R.id.image);
            picasso.load((File)getItem(position)).centerInside().resize(parent.getWidth(), parent.getHeight()).into(imageView);

            return view;
        }
    }


    private static class IsPhotoPredicate implements Predicate<File> {
        private static final Set<String> extensions = new TreeSet<String>(Arrays.asList(new String[]{"jpg", "jpeg", "png"}));
        private static final Predicate<CharSequence> inDirectory = Predicates.contains(
                Pattern.compile(Joiner.on('|').join("Picture", "Photo", "Camera"), Pattern.CASE_INSENSITIVE));

        @Override public boolean apply(final File input) {
            return input.isFile() && isPhoto(input) && inDirectory.apply(input.getAbsolutePath());
        }

        private boolean isPhoto(final File input) {
            return extensions.contains(Files.getFileExtension(input.getPath().toLowerCase()));
        }
    }

    private static class LastModifiedFileComparator implements Comparator<File> {
        @Override public int compare(final File lhs, final File rhs) {
            if (lhs.equals(rhs)) {
                return 0;
            }
            return (lhs.lastModified() < rhs.lastModified()) ? 1 : -1;
        }
    }

}
