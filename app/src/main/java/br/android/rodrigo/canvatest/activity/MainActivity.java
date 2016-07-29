package br.android.rodrigo.canvatest.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import br.android.rodrigo.canvatest.ContentManager;
import br.android.rodrigo.canvatest.Globals;
import br.android.rodrigo.canvatest.R;
import br.android.rodrigo.canvatest.utils.ActivityUtils;
import br.android.rodrigo.canvatest.utils.ColorUtils;
import br.android.rodrigo.canvatest.utils.FileUtils;
import br.android.rodrigo.canvatest.utils.Utils;

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
     * Saved Instance State.
     */

    public static final String TILE_SIZE_SAVED_INSTANCE = "tile_size_saved_instance";
    public static final String FILE_PATH_SAVED_INSTANCE = "file_path_saved_instance";

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    /**
     * Contexts.
     */

    private Activity mActivity = MainActivity.this;

    /**
     * Layout.
     */

    private TextView mFileLabelTextView;
    private ImageView mFileImageView;
    private TextView mPixelSizeLabelTextView;
    private TextView mPixelSizeTextView;
    private ImageView mPhotoImageView;

    /**
     * Others.
     */

    private Integer mTileSize = 32;
    private String mFilePath = "";
    private Intent mIntentData = null;
    private Bitmap mSourceBitmap;

    //--------------------------------------------------
    // Activity Life Cycle
    //--------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSavedInstanceState(savedInstanceState);
        changeStatusBar();
        setLayout();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == FILE_SELECT_CODE) {
                mIntentData = data;
                checkPermissions();
            } else {
                Toast.makeText(mActivity, getString(R.string.activity_main__error_reading_file),
                    Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mActivity, getString(R.string.activity_main__error_reading_file),
                Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TILE_SIZE_SAVED_INSTANCE, mTileSize);
        outState.putString(FILE_PATH_SAVED_INSTANCE, mFilePath);
    }

    //--------------------------------------------------
    // Menu
    //--------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Integer id = menuItem.getItemId();
        switch (id) {
            case R.id.id_menu_tile:
                // Checks connection.
                if (!Utils.hasConnection(mActivity)) {
                    Utils.callLostConnectionDialog(mActivity);
                } else {
                    if (!Utils.isEmpty(mFilePath)) {
                        ContentManager.getInstance().setSourceBitmap(mSourceBitmap);
                        ActivityUtils.startActivityExtras(mActivity, TileActivity.class,
                            Globals.TILE_SIZE_EXTRA, mTileSize);
                    } else {
                        Toast.makeText(mActivity, getString(R.string.activity_main__error_reading_file),
                            Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        return false;
    }

    //--------------------------------------------------
    // Methods
    //--------------------------------------------------

    private void getSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mTileSize = savedInstanceState.getInt(TILE_SIZE_SAVED_INSTANCE);
            mFilePath = savedInstanceState.getString(FILE_PATH_SAVED_INSTANCE);
            if (!Utils.isEmpty(mFilePath)) {
                mPhotoImageView = (ImageView) findViewById(R.id.id_activity_main__photo_image_view);
                mPhotoImageView.setVisibility(View.VISIBLE);
                Bitmap bitmap = BitmapFactory.decodeFile(mFilePath);
                mSourceBitmap = bitmap;
                mPhotoImageView.setImageBitmap(bitmap);
            }
        }
    }

    private void changeStatusBar() {
        ColorUtils.changeStatusBar(mActivity, getWindow(), R.color.teal_500);
        initToolbar(R.color.teal_500, false);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    private void initToolbar(int colorId, Boolean homeEnabled) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(homeEnabled);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mActivity, colorId)));
        }
    }

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

        mPhotoImageView = (ImageView) findViewById(R.id.id_activity_main__photo_image_view);
    }

    private void callTextFontListDialog() {
        new MaterialDialog.Builder(mActivity).title(R.string.activity_main__choose_tile_size)
                .items(R.array.array_list__pixel_size).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                mTileSize = Integer.valueOf(text.toString());
                mPixelSizeTextView.setText(text);
            }
        }).show();
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
            Toast.makeText(mActivity, getString(R.string.activity_main__install_file_manager), Toast.LENGTH_SHORT).show();
        }
    }

    private void getFileContentFromIntent(Intent data) {
        if (data != null) {
            Uri uri = data.getData();
            mFilePath = FileUtils.getPath(mActivity, uri);
        }

        mPhotoImageView.setVisibility(View.VISIBLE);
        Bitmap bitmap = BitmapFactory.decodeFile(mFilePath);
        mSourceBitmap = bitmap;
        mPhotoImageView.setImageBitmap(bitmap);
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