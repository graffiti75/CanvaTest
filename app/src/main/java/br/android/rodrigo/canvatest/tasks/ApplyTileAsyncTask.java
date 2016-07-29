package br.android.rodrigo.canvatest.tasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import br.android.rodrigo.canvatest.Globals;
import br.android.rodrigo.canvatest.model.Tile;
import br.android.rodrigo.canvatest.utils.Utils;

/**
 * AverageColorAsyncTask.java.
 *
 * @author Rodrigo Cericatto
 * @since Jul 29, 2016
 */
public class ApplyTileAsyncTask extends AsyncTask<Object, Void, Integer> {

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    private Activity mActivity;
    private Integer mTileSize;
    private Bitmap mSourceBitmap;

    private LinearLayout mLoadingLinearLayout;
    private ImageView mPhotoImageView;

    //--------------------------------------------------
    // Async Task
    //--------------------------------------------------

    @Override
    protected Integer doInBackground(Object... params) {
        // Params.
        mActivity = (Activity)params[0];
        mSourceBitmap = (Bitmap)params[1];
        mTileSize = (Integer)params[2];
        mLoadingLinearLayout = (LinearLayout)params[3];
        mPhotoImageView = (ImageView)params[4];

        // Apply tiles.
        if (!Utils.hasConnection(mActivity)) {
            Utils.callLostConnectionDialog(mActivity);
        } else {
            List<Tile> tiles = getTiles();
            mergeImages(tiles, mSourceBitmap);
        }

        return 0;
    }

    //--------------------------------------------------
    // Bitmap Methods
    //--------------------------------------------------

    private void mergeImages(final List<Tile> tiles, Bitmap source) {
        int width = source.getWidth(), height = source.getHeight();
        final Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas comboImage = new Canvas(result);
        final String url = Globals.BASE_URL + mTileSize + "/" + mTileSize + "/";

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
        Integer imageWidth = mSourceBitmap.getWidth();
        Integer imageHeight = mSourceBitmap.getHeight();

        for (int i = 0; i < imageHeight; i += mTileSize) {
            for (int j = 0; j < imageWidth; j += mTileSize) {
                // Current row.
                Integer currentY = i;
                Integer height = mTileSize;
                if (i + mTileSize > imageHeight) {
                    height = imageHeight - i;
                }

                // Current column.
                Integer currentX = j;
                Integer width = mTileSize;
                if (j + mTileSize > imageWidth) {
                    width = imageWidth - j;
                }
                Bitmap bitmap = Bitmap.createBitmap(mSourceBitmap, currentX, currentY, width, height);
                Tile tile = new Tile(currentX, currentY, width, height, bitmap);
                tiles.add(tile);
            }
        }
        return tiles;
    }

    private void downloadImage(final List<Tile> tiles, final Integer current, String url,
        final Tile item, final Bitmap result, final Canvas comboImage) {
        Glide.with(mActivity).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
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
                        mLoadingLinearLayout.setVisibility(View.GONE);
                        mPhotoImageView.setVisibility(View.VISIBLE);
                        mPhotoImageView.setImageBitmap(result);
                    }
                }
            }
        });
    }
}