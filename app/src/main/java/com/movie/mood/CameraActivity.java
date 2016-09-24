package com.movie.mood;

import com.movie.mood.Camera2BasicFragment.OnImageCapturedListener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import de.toman.milight.WiFiBox;

public class CameraActivity extends AppCompatActivity {

    ColourAverager colourAverager = new ColourAverager();
    private Camera2BasicFragment mFrag;


    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_camera);

        if (savedInstanceState == null) {
            mFrag = Camera2BasicFragment.newInstance();

            getFragmentManager().beginTransaction()
                    .add(R.id.container, mFrag, "camerafrag")
                    .commit();
        } else {
            mFrag = (Camera2BasicFragment) getFragmentManager().findFragmentByTag("camerafrag");
        }


        mFrag.setOnImageCapturedListener(new OnImageCapturedListener() {

            @Override public void onImageCaptured(Bitmap bitmap) {
                if(bitmap != null) {

                    Log.d("BITMAP", "got bitmap");
                    final int[] dominantColors = colourAverager.getDomnantColor(bitmap, 2);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFrag.setDebugColors(dominantColors[0], dominantColors[1]);
                        }
                    });

                    float[] lefthsv = new float[3];
                    Color.colorToHSV(dominantColors[0], lefthsv);

                    float[] rightHsv = new float[3];
                    Color.colorToHSV(dominantColors[1], rightHsv);

                    setLightsColor(lefthsv, rightHsv);
                } else {
                    Log.d("BITMAP", "no bitmap");
                }

            }
        });
    }

    private void setLightsColor(float[] lefthsv, float[] righthsv) {
        Intent intent = new Intent(this, LightIntentService.class);
        setBrightness(intent, true, lefthsv[2]);

        if (lefthsv[1] > 0.25f ) {
            setLightColor(true, lefthsv[0], intent);
        } else {
            setWhite(true, intent);
        }

        setBrightness(intent, false, righthsv[2]);

        if (lefthsv[1] > 0.01f ) {
            setLightColor(false, righthsv[0], intent);
        } else {
            setWhite(false, intent);
        }

        startService(intent);
    }

    private void setWhite(boolean left, Intent intent) {
        intent.putExtra(LightIntentService.EXTRA_MILLIS, SystemClock.elapsedRealtime());
        intent.putExtra(LightIntentService.EXTRA_GROUP, left ? 1 : 2);
        intent.putExtra(LightIntentService.EXTRA_COMMAND, LightIntentService.COMMAND_WHITE);
    }

    private void setLightColor(boolean left, float hue, Intent intent) {
        intent.putExtra(LightIntentService.EXTRA_MILLIS, SystemClock.elapsedRealtime());
        intent.putExtra(LightIntentService.EXTRA_GROUP, left ? 1 : 2);
        intent.putExtra(LightIntentService.EXTRA_COMMAND, LightIntentService.COMMAND_COLOUR);
        intent.putExtra(LightIntentService.EXTRA_VALUE, (int) correctColor(hue));
    }

    @NonNull private Intent setBrightness(Intent intent, boolean left, float brightness) {
        intent.putExtra(LightIntentService.EXTRA_MILLIS, SystemClock.elapsedRealtime());
        intent.putExtra(LightIntentService.EXTRA_GROUP, left ? 1 : 2);
        intent.putExtra(LightIntentService.EXTRA_COMMAND, LightIntentService.COMMAND_BRIGHTNESS);
        intent.putExtra(LightIntentService.EXTRA_VALUE, (int)(WiFiBox.MAX_BRIGHTNESS * brightness));
        startService(intent);
        return intent;
    }

    int correctColor(float hue) {
        return shift(normalise(hue)) ;
    }

    int normalise(float h) {
        return (int) ((h /360) * 255);
    }

    int shift(int c) {
        return ((255 - c) - 80 + 255) % 256;
    }

}
