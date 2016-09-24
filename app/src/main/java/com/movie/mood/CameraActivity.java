package com.movie.mood;

import com.movie.mood.Camera2BasicFragment.OnImageCapturedListener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class CameraActivity extends AppCompatActivity {

    ColourAverager colourAverager = new ColourAverager();
    private Camera2BasicFragment mFrag;

    private int[] color = { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW };
    private long lastTime;
    private int colorIndex;

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
                if (bitmap != null) {

                    if (lastTime == 0) {
                        lastTime = System.currentTimeMillis();
                    }

                    if ((System.currentTimeMillis() - lastTime) > 1000) {
                        colorIndex++;
                        lastTime = 0;
                    }

                    final int[] dominantColors = colourAverager.getDomnantColor(bitmap, 2);

                    //sendColors(color[colorIndex % color.length], color[colorIndex % color.length]);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFrag.setDebugColors(dominantColors[0], dominantColors[1]);
                        }
                    });

                    sendColors(dominantColors[0], dominantColors[1]);

                    // setLightsColor(lefthsv, lefthsv);
                }
            }
        });
    }

    private void sendColors(int leftColor, int rightColor) {
        Intent intent = new Intent(this, LightIntentService.class);
        intent.putExtra(LightIntentService.LEFT_COLOR, leftColor);
        intent.putExtra(LightIntentService.RIGHT_COLOR, rightColor);
        intent.putExtra(LightIntentService.EXTRA_MILLIS, SystemClock.elapsedRealtime());
        startService(intent);
    }
}
