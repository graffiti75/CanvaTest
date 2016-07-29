package br.android.rodrigo.canvatest.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import br.android.rodrigo.canvatest.ContentManager;
import br.android.rodrigo.canvatest.Globals;
import br.android.rodrigo.canvatest.R;
import br.android.rodrigo.canvatest.tasks.ApplyTileAsyncTask;
import br.android.rodrigo.canvatest.utils.ColorUtils;
import br.android.rodrigo.canvatest.view.animation.Navigation;

/**
 * MainActivity.java.
 *
 * @author Rodrigo Cericatto
 * @since Jul 29, 2016
 */
public class TileActivity extends AppCompatActivity {

    //--------------------------------------------------
    // Attributes
    //--------------------------------------------------

    /**
     * Contexts.
     */

    private TileActivity mActivity = TileActivity.this;

    /**
     * Layout.
     */

    private LinearLayout mLoadingLinearLayout;
    private ImageView mPhotoImageView;

    /**
     * Others.
     */

    private Integer mTileSize = 32;
    private Bitmap mSourceBitmap;

    //--------------------------------------------------
    // Activity Life Cycle
    //--------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tile);

        getExtras();
        changeStatusBar();
        setLayout();
        setTileTransformation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Navigation.animate(this, Navigation.Animation.BACK);
    }

    //--------------------------------------------------
    // Menu
    //--------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Integer id = menuItem.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    //--------------------------------------------------
    // Methods
    //--------------------------------------------------

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mTileSize = extras.getInt(Globals.TILE_SIZE_EXTRA);
            mSourceBitmap = ContentManager.getInstance().getSourceBitmap();
        }
    }

    private void changeStatusBar() {
        ColorUtils.changeStatusBar(this, getWindow(), R.color.teal_500);
        initToolbar(R.color.teal_500, true);
        getSupportActionBar().setTitle(R.string.activity_tile__title);
    }

    private void initToolbar(int colorId, Boolean homeEnabled) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(homeEnabled);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, colorId)));
        }
    }

    private void setLayout() {
        mLoadingLinearLayout = (LinearLayout) findViewById(R.id.id_activity_tile__loading_linear_layout);
        mPhotoImageView = (ImageView) findViewById(R.id.id_activity_tile__photo_image_view);
    }

    private void setTileTransformation() {
        ApplyTileAsyncTask task = new ApplyTileAsyncTask() {
            @Override
            protected void onPostExecute(Integer result) {
            }
        };
        task.execute(mActivity, mSourceBitmap, mTileSize, mLoadingLinearLayout, mPhotoImageView);
    }
}