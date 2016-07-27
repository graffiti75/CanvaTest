package br.android.rodrigo.canvatest.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * BitmapUtils.java.
 *
 * @author Rodrigo Cericatto
 * @since Jul 24, 2016
 */
public class BitmapUtils {

    //--------------------------------------------------
    // Statics
    //--------------------------------------------------

    private static final float BITMAP_SCALE = 0.4f;
//    private static final float BLUR_RADIUS = 7.5f;
    private static final float BLUR_RADIUS = 15f;

    //--------------------------------------------------
    // Bitmap Methods
    //--------------------------------------------------

    public static Bitmap getRoundedShape(Bitmap source) {
        // Process image.
        int targetWidth = source.getWidth();
        int targetHeight = source.getHeight();
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2, ((float) targetHeight - 1) / 2,
            (Math.min(((float) targetWidth), ((float) targetHeight)) / 2), Path.Direction.CCW);

        canvas.clipPath(path);
        canvas.drawBitmap(source, new Rect(0, 0, source.getWidth(), source.getHeight()),
            new Rect(0, 0, targetWidth, targetHeight), null);

        return targetBitmap;
    }

    public static Bitmap convertDrawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Integer width = drawable.getIntrinsicWidth();
        Integer height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    //--------------------------------------------------
    // Paint Methods
    //--------------------------------------------------

    public static Bitmap darkenBitmap(Bitmap input) {
        Integer width = input.getWidth();
        Integer height = input.getHeight();
        Bitmap darkenBitmap = input.copy(Bitmap.Config.ARGB_8888, true);
        Integer factor = 2;
        int pixelAlpha, pixel, R, G, B;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixel = input.getPixel(i, j);
                R = (pixel >> 16) & 0xff;
                G = (pixel >> 8) & 0xff;
                B = pixel & 0xff;
                pixelAlpha = Color.argb(0, R / factor, G / factor, B / factor);
                darkenBitmap.setPixel(i, j, pixelAlpha);
            }
        }
        return darkenBitmap;
    }

    public static Bitmap paintBitmap(Bitmap input, String color) {
        Integer width = input.getWidth();
        Integer height = input.getHeight();
        Bitmap darkenBitmap = input.copy(Bitmap.Config.ARGB_8888, true);
        int pixelAlpha = 0, userA = 0, userR = 0, userG = 0, userB = 0;
        Boolean hasAlpha = false;

        if (color.length() == 9) {
            // Color with alpha.
            userA = Integer.parseInt(color.substring(1, 3), 16);
            userR = Integer.parseInt(color.substring(3, 5), 16);
            userG = Integer.parseInt(color.substring(5, 7), 16);
            userB = Integer.parseInt(color.substring(7, 9), 16);
            hasAlpha = true;
        } else if (color.length() == 7) {
            // Color without alpha.
            userR = Integer.parseInt(color.substring(1, 3), 16);
            userG = Integer.parseInt(color.substring(3, 5), 16);
            userB = Integer.parseInt(color.substring(5, 7), 16);
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (hasAlpha) {
                    pixelAlpha = Color.argb(userA, userR, userG, userB);
                } else {
                    pixelAlpha = Color.rgb(userR, userG, userB);
                }
                darkenBitmap.setPixel(i, j, pixelAlpha);
            }
        }
        return darkenBitmap;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blur(Context context, Bitmap image) {
//        int width = Math.round(image.getWidth() * BITMAP_SCALE);
//        int height = Math.round(image.getHeight() * BITMAP_SCALE);
        int width = Math.round(image.getWidth() * 1);
        int height = Math.round(image.getHeight() * 1);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    //--------------------------------------------------
    // Resize Methods
    //--------------------------------------------------

    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // Creates a matrix for the manipulation and resize the bitmap.
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        // Recreate the new bitmap.
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
}