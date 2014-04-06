package io.msol.readmyphoto;

import android.content.res.AssetManager;

import com.google.common.io.ByteStreams;
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
    private static final String traineddataFilename = "eng.traineddata";
    private static final String trainingPath = "tesseract-ocr/en/tessdata/" + traineddataFilename;
    private static final String tessdataPath = "tessdata";

    private final TessBaseAPI tesseract = new TessBaseAPI();

    public Tesseract(final AssetManager assetManager, File filesDir) {
        File trainedData = new File(filesDir, tessdataPath + "/" + traineddataFilename);

        if (!trainedData.exists()) {
            unzipTrainedData(assetManager, trainedData);
        }
    }

    private void unzipTrainedData(final AssetManager assetManager, final File trainedData) {
        try {
            trainedData.getParentFile().mkdirs();
            trainedData.createNewFile();

            InputStream inputStream = new BufferedInputStream(assetManager.open(trainingPath));
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(trainedData));
            ByteStreams.copy(inputStream, outputStream);

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Error copying out from assets", e);
        }
    }
}
