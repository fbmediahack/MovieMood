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

    public int getDominantColor(Bitmap bitmap) {
        Log.d(TAG, "start");
        final long startTime = System.currentTimeMillis();
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();

        Log.d(TAG, "finish =  " + (System.currentTimeMillis() - startTime));
        return color;
    }
}
