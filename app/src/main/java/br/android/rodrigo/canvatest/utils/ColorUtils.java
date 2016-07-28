package br.android.rodrigo.canvatest.utils;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

/**
 * ColorUtils.java.
 *
 * @author Rodrigo Cericatto
 * @since Jul 27, 2016
 */
public class ColorUtils {

    //--------------------------------------------------
    // Color Methods
    //--------------------------------------------------

    public static String convertIntToColorString(int color) {
        String hex = Integer.toHexString(color);
        hex = "#" + hex;
        if (hex.length() % 2 == 0) {
            hex = hex.replace("#", "");
            hex = "#0" + hex;
        }
        return hex;
    }

    public static int getDominantColor(Bitmap bitmap) {
        if (bitmap == null)
            throw new NullPointerException();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int pixels[] = new int[size];

        Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false);
        bitmap2.getPixels(pixels, 0, width, 0, 0, width, height);

        HashMap<Integer, Integer> colorMap = new HashMap<>();
        int color;
        Integer count;
        for (int i = 0; i < pixels.length; i++) {
            color = pixels[i];
            count = colorMap.get(color);
            if (count == null)
                count = 0;
            colorMap.put(color, ++count);
        }

        int dominantColor = 0;
        int max = 0;
        for (Map.Entry<Integer, Integer> entry : colorMap.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                dominantColor = entry.getKey();
            }
        }
        return dominantColor;
    }
}