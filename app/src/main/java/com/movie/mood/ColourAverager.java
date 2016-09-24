package com.movie.mood;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.graphics.Palette;
import android.util.Log;

public class ColourAverager {

    private static final String TAG = ColourAverager.class.getSimpleName();

    public ColourAverager() {
    }

    @ColorInt
    public int averagePixelsInImage(Bitmap bitmap) {
        long redBucket = 0;
        long greenBucket = 0;
        long blueBucket = 0;
        long pixelCount = 0;

        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                final int c = bitmap.getPixel(x, y);

                pixelCount++;
                redBucket += Color.red(c);
                greenBucket += Color.green(c);
                blueBucket += Color.blue(c);
            }
        }

        final int r = (int) (redBucket / pixelCount);
        final int g = (int) (greenBucket / pixelCount);
        final int b = (int) (blueBucket / pixelCount);
        return Color.rgb(r,g, b);
    }

    public int[] averagePixelsInImage(Bitmap bitmap, int bands) {

        Log.d(TAG, "start");
        final long startTime = System.currentTimeMillis();

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        final int bandWidth = width / bands;

        int[] results = new int[bands];

        for (int bandIndex = 0; bandIndex < bands; bandIndex++) {
            int bandStart = bandIndex * bandWidth;
            int bandEnd = bandStart + bandWidth;

            long redBucket = 0;
            long greenBucket = 0;
            long blueBucket = 0;
            long pixelCount = 0;

            for (int x = bandStart; x < bandEnd; x++) {
                for (int y = 0; y < height; y++) {
                    pixelCount++;
                    final int c = bitmap.getPixel(x, y);
                    redBucket += Color.red(c);
                    greenBucket += Color.green(c);
                    blueBucket += Color.blue(c);
                }
            }
            final int r = (int) (redBucket / pixelCount);
            final int g = (int) (greenBucket / pixelCount);
            final int b = (int) (blueBucket / pixelCount);
            results[bandIndex] = Color.rgb(r, g, b);
        }

        Log.d(TAG, "finish =  " + (System.currentTimeMillis() - startTime) );
        return results;
    }


    public Palette pallete(Bitmap bitmap) {
        Log.d(TAG, "start");
        final long startTime = System.currentTimeMillis();
        final Palette generate = Palette.from(bitmap).generate();
        Log.d(TAG, "finish =  " + (System.currentTimeMillis() - startTime) );
        return generate;
    }


    public int[] getDomnantColor(Bitmap bitmap, int bands) {
        Log.d(TAG, "start");
        final long startTime = System.currentTimeMillis();

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        final int bandWidth = width / bands;

        int[] results = new int[bands];

        for (int bandIndex = 0; bandIndex < bands; bandIndex++) {
            int bandStart = bandIndex * bandWidth;
            Bitmap band = Bitmap.createBitmap(bitmap, bandStart, 0, bandWidth, height);
            results[bandIndex] = getDominantColor(band);
            band.recycle();
        }
        Log.d(TAG, "finish =  " + (System.currentTimeMillis() - startTime) );
        return results;
    }


    public int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
    }
}

//var_R = ( R / 255 )                     //RGB from 0 to 255
//        var_G = ( G / 255 )
//        var_B = ( B / 255 )
//
//        var_Min = min( var_R, var_G, var_B )    //Min. value of RGB
//        var_Max = max( var_R, var_G, var_B )    //Max. value of RGB
//        del_Max = var_Max - var_Min             //Delta RGB value
//
//        V = var_Max
//
//        if ( del_Max == 0 )                     //This is a gray, no chroma...
//        {
//        H = 0                                //HSV results from 0 to 1
//        S = 0
//        }
//        else                                    //Chromatic data...
//        {
//        S = del_Max / var_Max
//
//        del_R = ( ( ( var_Max - var_R ) / 6 ) + ( del_Max / 2 ) ) / del_Max
//        del_G = ( ( ( var_Max - var_G ) / 6 ) + ( del_Max / 2 ) ) / del_Max
//        del_B = ( ( ( var_Max - var_B ) / 6 ) + ( del_Max / 2 ) ) / del_Max
//
//        if      ( var_R == var_Max ) H = del_B - del_G
//        else if ( var_G == var_Max ) H = ( 1 / 3 ) + del_R - del_B
//        else if ( var_B == var_Max ) H = ( 2 / 3 ) + del_G - del_R
//
//        if ( H < 0 ) H += 1
//        if ( H > 1 ) H -= 1
//        }