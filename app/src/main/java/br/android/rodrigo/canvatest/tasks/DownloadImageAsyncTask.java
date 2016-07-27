package br.android.rodrigo.canvatest.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * DownloadImageAsyncTask.java.
 *
 * @author Rodrigo Cericatto
 * @since Jul 27, 2016
 */
public class DownloadImageAsyncTask extends AsyncTask<Object, Void, Bitmap> {

    //--------------------------------------------------
    // Async Task
    //--------------------------------------------------

    @Override
    protected Bitmap doInBackground(Object... params) {
        String url = (String) params[0];
        Bitmap bitmap = downloadBitmap(url);
        return bitmap;
    }

    //--------------------------------------------------
    // Methods
    //--------------------------------------------------

    /**
     * Download an image.
     *
     * @param url The URL of the image to be download.
     * @return The download image.
     */
    public static Bitmap downloadBitmap(String url) {
        Bitmap bitmapImage = null;
        URL fileUrl = null;

        try {
            fileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            // Gets the connection with the URL.
            HttpURLConnection conn = (HttpURLConnection)fileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            // If data couldn't be downloaded.
            if (conn.getResponseCode() == -1) {
                int[] colors = new int[1];
                colors[0] = Color.BLACK;
                Bitmap defaultBitmap = Bitmap.createBitmap(colors, 60, 60, Bitmap.Config.ARGB_8888);
                return defaultBitmap;
            }

            // Gets the image from URL.
            InputStream is = conn.getInputStream();
            bitmapImage = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmapImage;
    }
}