package br.android.rodrigo.canvatest.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import br.android.rodrigo.canvatest.utils.ColorUtils;

/**
 * AverageColorAsyncTask.java.
 *
 * @author Rodrigo Cericatto
 * @since Jul 27, 2016
 */
public class AverageColorAsyncTask extends AsyncTask<Object, Void, String> {

    //--------------------------------------------------
    // Async Task
    //--------------------------------------------------

    @Override
    protected String doInBackground(Object... params) {
        // Params.
        Bitmap bitmap = (Bitmap)params[0];

        // Gets the average color.
        int colorId = ColorUtils.getDominantColor(bitmap);
        String color = ColorUtils.convertIntToColorString(colorId).replace("#", "").substring(2, 8);
        return color;
    }
}