package br.android.rodrigo.canvatest;

import android.graphics.Bitmap;

/**
 * ContentManager.java class.
 *
 * @author Rodrigo Cericatto
 * @since Jul 29, 2016
 */
public class ContentManager {

    //----------------------------------------------
    // Statics
    //----------------------------------------------

    private static ContentManager sInstance = null;

    //----------------------------------------------
    // Attributes
    //----------------------------------------------

    private Bitmap mSourceBitmap;

    //----------------------------------------------
    // Constructor
    //----------------------------------------------

    /**
     * Private constructor.
     */
    public ContentManager() {
    }

    /**
     * @return The singleton instance of ContentManager.
     */
    public static ContentManager getInstance() {
        if (sInstance == null) {
            sInstance = new ContentManager();
        }
        return sInstance;
    }

    //----------------------------------------------
    // Methods
    //----------------------------------------------

    public Bitmap getSourceBitmap() {
        return mSourceBitmap;
    }

    public void setSourceBitmap(Bitmap bitmap) {
        mSourceBitmap = bitmap;
    }
}