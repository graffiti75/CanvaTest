package br.android.rodrigo.canvatest.model;

import android.graphics.Bitmap;

/**
 * Utils.java.
 *
 * @author Rodrigo Cericatto
 * @since Jul 25, 2016
 */
public class Tile {

    //----------------------------------------------
    // Attributes
    //----------------------------------------------

    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private Bitmap bitmap;

    //----------------------------------------------
    // Constructor
    //----------------------------------------------

    public Tile(Integer x, Integer y, Integer width, Integer height, Bitmap bitmap) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bitmap = bitmap;
    }

    //----------------------------------------------
    // To String
    //----------------------------------------------

    @Override
    public String toString() {
        return "Tile{" +
            "x=" + x +
            ", y=" + y +
            ", width=" + width +
            ", height=" + height +
            ", bitmap=" + bitmap +
            '}';
    }

    //----------------------------------------------
    // Getters and Setters
    //----------------------------------------------

    public Integer getX() {
        return x;
    }
    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }
    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getWidth() {
        return width;
    }
    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }
    public void setHeight(Integer height) {
        this.height = height;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}