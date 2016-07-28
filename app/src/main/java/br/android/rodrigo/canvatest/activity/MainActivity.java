package br.android.rodrigo.canvatest.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import br.android.rodrigo.canvatest.R;
import br.android.rodrigo.canvatest.model.Tile;
import br.android.rodrigo.canvatest.tasks.AverageColorAsyncTask;

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

    private static final String BASE_URL = "http://10.0.2.2:8765/color/";
    private static final Integer SIZE = 128;

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

    public void downloadImage(final List<Tile> tiles, final Integer current, String url,
        final Tile item, final Bitmap result, final Canvas comboImage) {
        Glide.with(this).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                // Gets the bitmap.
                if (resource != null) {
                    // Draws the image.
                    Bitmap transparent = resource;
                    Bitmap bitmap = overlay(item.getBitmap(), transparent);
                    comboImage.drawBitmap(bitmap, item.getX(), item.getY(), null);

                    // Display for the user.
                    if (current == (tiles.size() - 1)) {
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

    private void mergeImages(final List<Tile> tiles, Bitmap source) {
        int width = source.getWidth(), height = source.getHeight();
        final Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas comboImage = new Canvas(result);
        final String url = BASE_URL + SIZE + "/" + SIZE + "/";

        for (int i = 0; i < tiles.size(); i++) {
            final Tile item = tiles.get(i);
            final int current = i;
            AverageColorAsyncTask task = new AverageColorAsyncTask() {
                @Override
                protected void onPostExecute(String color) {
                    downloadImage(tiles, current, url + color, item, result, comboImage);
                }
            };
            task.execute(item.getBitmap());
        }
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
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.lili);
        Integer screenWidth = image.getWidth();
        Integer screenHeight = image.getHeight();

        for (int i = 0; i < screenHeight; i += SIZE) {
            for (int j = 0; j < screenWidth; j += SIZE) {
                // Current row.
                Integer currentY = i;
                Integer height = SIZE;
                if (i + SIZE > screenHeight) {
                    height = screenHeight - i;
                }

                // Current column.
                Integer currentX = j;
                Integer width = SIZE;
                if (j + SIZE > screenWidth) {
                    width = screenWidth - j;
                }
                Bitmap bitmap = Bitmap.createBitmap(image, currentX, currentY, width, height);
                Tile tile = new Tile(currentX, currentY, width, height, bitmap);
                tiles.add(tile);
            }
        }
        return tiles;
    }
}