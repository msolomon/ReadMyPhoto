package io.msol.readmyphoto;

import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.google.common.io.ByteStreams;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mike on 4/5/14.
 */
public class Tesseract {
    private static final String trainedDataPath = "tesseract-ocr/en/tessdata/eng.traineddata";
    private final File trainedData;

    public Tesseract(final AssetManager assetManager, File filesDir) {

        trainedData = new File(filesDir, trainedDataPath);

        if (!trainedData.exists()) {
            unzipTrainedData(assetManager, trainedData);
        }
    }

    private void unzipTrainedData(final AssetManager assetManager, final File trainedData) {
        try {
            trainedData.getParentFile().mkdirs();
            trainedData.createNewFile();

            InputStream inputStream = new BufferedInputStream(assetManager.open(trainedDataPath));
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(trainedData));
            ByteStreams.copy(inputStream, outputStream);

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Error copying out from assets", e);
        }
    }

    public OCRTask readImage(final String path, final Callback tesseractCallback) {
        OCRTask task = new OCRTask(trainedData, tesseractCallback);
        task.execute(new Options(path));
        return task;
    }

    public static interface Callback {
        void onOCRComplete(String readText);
        void onFailure();
    }

    public static class Options {
        private final String imagePath;

        public Options(final String imagePath) {
            this.imagePath = imagePath;
        }

        public String getImagePath() {
            return imagePath;
        }
    }

    public static class OCRTask extends AsyncTask<Options, Void, Void> {
        private final File trainedData;
        private final Callback callback;
        private String result = "";
        private TessBaseAPI tesseract;

        public OCRTask(final File trainedData, final Callback callback) {
            this.trainedData = trainedData;
            this.callback = callback;
        }

        @Override protected Void doInBackground(final Options... params) {
            if (params.length != 1) {
                throw new RuntimeException("Expected one Options object");
            }

            Options options = params[0];

            tesseract = new TessBaseAPI();
            tesseract.init(trainedData.getParentFile().getParent(), "eng");
            if (isCancelled()) { return null; };

            tesseract.setImage(new File(options.getImagePath()));
            if (isCancelled()) { return null; };

            tesseract.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
            Pixa regions = tesseract.getRegions();
            tesseract.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK);
            if (isCancelled()) { return null; };

            for (final Pix pix : regions) {
                tesseract.clear();
                if (isCancelled()) { return null; };

                tesseract.setImage(pix);
                if (isCancelled()) { return null; };

                result += tesseract.getUTF8Text() + " ";
                if (isCancelled()) { return null; };
            }

            tesseract.end();

            return null;
        }

        @Override protected void onPostExecute(final Void aVoid) {
            callback.onOCRComplete(result.trim());
        }

        @Override protected void onCancelled(final Void aVoid) {
            callback.onFailure();
        }
    }
}
