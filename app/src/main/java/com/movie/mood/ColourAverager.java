package com.movie.mood;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.ColorInt;

public class ColourAverager {

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
        return results;
    }
}
