package br.android.rodrigo.canvatest.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import br.android.rodrigo.canvatest.R;
import br.android.rodrigo.canvatest.model.Tile;
import br.android.rodrigo.canvatest.tasks.AverageColorAsyncTask;
import br.android.rodrigo.canvatest.utils.FileUtils;

/**
 * MainActivity.java.
 *
 * @author Rodrigo Cericatto
 * @since Jul 24, 2016
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //--------------------------------------------------
    // Constants
    //--------------------------------------------------

    /**
     * OnActivityResult.
     */

    private static final Integer FILE_SELECT_CODE = 1011;

    /**
     * Permissions.
     */

    public static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    /**
     * Webservice.
     */

    private static final String BASE_URL = "http://10.0.2.2:8765/color/";

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    /**
     * Contexts.
     */

    private MainActivity mActivity = MainActivity.this;

    /**
     * Layout.
     */

    private TextView mFileLabelTextView;
    private ImageView mFileImageView;
    private TextView mPixelSizeLabelTextView;
    private TextView mPixelSizeTextView;
    private CardView mImageCardView;
    private ImageView mPhotoImageView;

    /**
     * Others.
     */

    private Integer mTileSize = 32;
    private String mFilePath = "";
    private Intent mIntentData = null;

    //--------------------------------------------------
    // Activity Life Cycle
    //--------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLayout();
        List<Tile> tiles = getTiles();
        Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.baby);
        mergeImages(tiles, source);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == FILE_SELECT_CODE) {
                mIntentData = data;
                checkPermissions();
            } else {
                Toast.makeText(this, getString(R.string.activity_main__error_reading_file),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.activity_main__error_reading_file),
                    Toast.LENGTH_SHORT).show();
        }
    }

    //--------------------------------------------------
    // Methods
    //--------------------------------------------------

    private void setLayout() {
        mFileLabelTextView = (TextView) findViewById(R.id.id_activity_main__file_label_text_view);
        mFileLabelTextView.setOnClickListener(this);

        mFileImageView = (ImageView) findViewById(R.id.id_activity_main__file_image_view);
        mFileImageView.setOnClickListener(this);

        mPixelSizeLabelTextView = (TextView) findViewById(R.id.id_activity_main__pixel_size_label_text_view);
        mPixelSizeLabelTextView.setOnClickListener(this);

        mPixelSizeTextView = (TextView) findViewById(R.id.id_activity_main__pixel_size_text_view);
        mPixelSizeTextView.setOnClickListener(this);
        mPixelSizeTextView.setText(mTileSize.toString());

        mImageCardView = (CardView) findViewById(R.id.id_activity_main__image_card_view);
        mPhotoImageView = (ImageView) findViewById(R.id.id_activity_main__photo_image_view);
    }

    private void downloadImage(final List<Tile> tiles, final Integer current, String url,
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
                        ImageView photoImageView = (ImageView) findViewById(R.id.id_activity_main__photo_image_view);
                        photoImageView.setImageBitmap(result);
                    }
                }
            }
        });
    }

    private void callTextFontListDialog() {
        new MaterialDialog.Builder(this).title(R.string.activity_main__choose_tile_size)
                .items(R.array.array_list__pixel_size).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                mTileSize = Integer.valueOf(text.toString());
                mPixelSizeTextView.setText(text);
            }
        }).show();
    }

    //--------------------------------------------------
    // Bitmap Methods
    //--------------------------------------------------

    private void mergeImages(final List<Tile> tiles, Bitmap source) {
        int width = source.getWidth(), height = source.getHeight();
        final Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas comboImage = new Canvas(result);
        final String url = BASE_URL + mTileSize + "/" + mTileSize + "/";

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

        for (int i = 0; i < screenHeight; i += mTileSize) {
            for (int j = 0; j < screenWidth; j += mTileSize) {
                // Current row.
                Integer currentY = i;
                Integer height = mTileSize;
                if (i + mTileSize > screenHeight) {
                    height = screenHeight - i;
                }

                // Current column.
                Integer currentX = j;
                Integer width = mTileSize;
                if (j + mTileSize > screenWidth) {
                    width = screenWidth - j;
                }
                Bitmap bitmap = Bitmap.createBitmap(image, currentX, currentY, width, height);
                Tile tile = new Tile(currentX, currentY, width, height, bitmap);
                tiles.add(tile);
            }
        }
        return tiles;
    }

    //--------------------------------------------------
    // File Methods
    //--------------------------------------------------

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.activity_main__select_image)),
                FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog.
            Toast.makeText(this, getString(R.string.activity_main__install_file_manager), Toast.LENGTH_SHORT).show();
        }
    }

    public static String readFromFile(String fileName) {
        String buffer = "";
        try {
            File file = new File(fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String data;
            while ((data = bufferedReader.readLine()) != null) {
                buffer += data;
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    private void getFileContentFromIntent(Intent data) {
        if (data != null) {
            Uri uri = data.getData();
            mFilePath = FileUtils.getPath(mActivity, uri);
        }
        Glide.with(mActivity).load(mFilePath).into(mPhotoImageView);
        mImageCardView.setVisibility(View.VISIBLE);
        mPhotoImageView.setVisibility(View.VISIBLE);
    }

    private void openFileActivity() {
//        ActivityUtils.startActivityTransition(mActivity, FileActivity.class,
//            R.id.id_activity_input_options__file_image_view, R.string.input_file__transition);
    }

    //--------------------------------------------------
    // Permissions
    //--------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getFileContentFromIntent(mIntentData);
            } else {
                finish();
            }
        }
    }

    public void checkPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(mActivity, new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE }, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        getFileContentFromIntent(mIntentData);
    }

    //--------------------------------------------------
    // View.OnClickListener
    //--------------------------------------------------

    @Override
    public void onClick(View view) {
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.zoom_in);
        Integer id = view.getId();
        switch (id) {
            case R.id.id_activity_main__file_label_text_view:
                mFileLabelTextView.startAnimation(animation);
                showFileChooser();
                break;
            case R.id.id_activity_main__file_image_view:
                mFileImageView.startAnimation(animation);
                showFileChooser();
                break;
            case R.id.id_activity_main__pixel_size_label_text_view:
                mPixelSizeLabelTextView.startAnimation(animation);
                callTextFontListDialog();
                break;
            case R.id.id_activity_main__pixel_size_text_view:
                mPixelSizeTextView.startAnimation(animation);
                callTextFontListDialog();
                break;
        }
    }
}