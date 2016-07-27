package br.android.rodrigo.canvatest.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import br.android.rodrigo.canvatest.R;
import br.android.rodrigo.canvatest.model.Tile;
import br.android.rodrigo.canvatest.tasks.DownloadImageAsyncTask;

/**
 * MainActivity.java.
 *
 * @author Rodrigo Cericatto
 * @since Jul 24, 2016
 */
public class MainActivity extends AppCompatActivity {

    //--------------------------------------------------
    // Constants
    //--------------------------------------------------

    private static final String TAG = "Canva";

    private static final String BASE_URL = "http://10.0.2.2:8765/color/";
    private static final Integer TRANSPARENCY = 70;
    private static final Integer SIZE = 64;

    //--------------------------------------------------
    // Activity Life Cycle
    //--------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Tile> tiles = getTiles();
        Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.baby);
        mergeImages(tiles, source);
    }

    //--------------------------------------------------
    // Methods
    //--------------------------------------------------

    // http://localhost:8765/color/32/32/abcabc
    private void downloadImage(final List<Tile> tiles, final Integer current, String url,
        final Tile item, final Bitmap result, final Canvas comboImage) {

        Log.i(TAG, "MainActivity.downloadImage() -> Current is " + current + ", url is '" + url + "'.");
        Glide.with(this).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                // Gets the bitmap.
                if (resource != null) {
                    Log.i(TAG, "MainActivity.downloadImage().onPostExecute() -> Current is " + current
                            + ", and tiles size is '" + tiles.size() + "'.");

                    // Draws the image.
                    Bitmap transparent = makeTransparent(resource, TRANSPARENCY);
                    Bitmap bitmap = overlay(item.getBitmap(), transparent);
                    comboImage.drawBitmap(bitmap, item.getX(), item.getY(), null);

                    // Display for the user.
                    if (current == tiles.size()) {
                        ImageView first = (ImageView) findViewById(R.id.id_activity_main__first_image);
                        first.setImageBitmap(result);
                    }
                }
            }
        });
    }

    //--------------------------------------------------
    // Bitmap Methods
    //--------------------------------------------------

    private Bitmap makeTransparent(Bitmap source, int value) {
        int width = source.getWidth();
        int height = source.getHeight();
        Bitmap transBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(transBitmap);
        canvas.drawARGB(0, 0, 0, 0);

        // Configuring paint.
        Paint paint = new Paint();
        paint.setAlpha(value);
        canvas.drawBitmap(source, 0, 0, paint);
        return transBitmap;
    }

    private void mergeImages(List<Tile> tiles, Bitmap source) {
        int width = source.getWidth(), height = source.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas comboImage = new Canvas(result);
        String url = BASE_URL + SIZE + "/" + SIZE + "/";

        int current = 1;
        for (Tile item : tiles) {
            String color = dominantColor(item.getBitmap());
            downloadImage(tiles, current++, url + color, item, result, comboImage);
        }
    }

    private String dominantColor(Bitmap bitmap) {
        return "ff0000";
    }

    private Bitmap overlay(Bitmap bitmap1, Bitmap bitmap2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), bitmap1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(bitmap2, 0, 0, null);
        return bmOverlay;
    }

    private List<Tile> getTiles() {
        List<Tile> tiles = new ArrayList<>();
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.baby);
        Integer screenWidth = image.getWidth();
        Integer screenHeight = image.getHeight();

//        Log.i(TAG, "----- Screen width is " + screenWidth + ".");
//        Log.i(TAG, "----- Screen height is " + screenHeight + ".");
        for (int i = 0; i < screenHeight; i += SIZE) {
            for (int j = 0; j < screenWidth; j += SIZE) {
                // Current row.
                Integer currentY = i;
                Integer height = SIZE;
//                Log.i(TAG, "----- Current height is " + currentY + ".");
                if (i + SIZE > screenHeight) {
                    height = screenHeight - i;
//                    Log.i(TAG, "----- Current height is " + currentY + ".");
                }

                // Current column.
                Integer currentX = j;
                Integer width = SIZE;
//                Log.i(TAG, "----- Current width is " + currentX + ".");
                if (j + SIZE > screenWidth) {
                    width = screenWidth - j;
//                    Log.i(TAG, "----- Current width is " + currentX + ".");
                }
                Bitmap bitmap = Bitmap.createBitmap(image, currentX, currentY, width, height);
                Tile tile = new Tile(currentX, currentY, width, height, bitmap);
                Log.i(TAG, "----->>>>> Current Tile is " + tile.toString() + ".");
                tiles.add(tile);
            }
        }
        Log.i(TAG, "----->>>>> Tiles size is " + tiles.size() + ".");
        return tiles;
    }
}