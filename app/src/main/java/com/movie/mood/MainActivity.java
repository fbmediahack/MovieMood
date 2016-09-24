package com.movie.mood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import de.toman.milight.WiFiBox;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private final ColourAverager colourAverager = new ColourAverager();
    private View left;
    private View right;
    private ImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.image);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);

        findViewById(R.id.button_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();

        final int[] color = colourAverager.getDomnantColor(bitmap, 2);
        left.setBackgroundColor(color[0]);
        right.setBackgroundColor(color[1]);


        float[] lefthsv = new float[3];
        Color.colorToHSV(Color.BLUE, lefthsv);

        float[] righthsv = new float[3];
        Color.colorToHSV(Color.RED, righthsv);

        setLightsColor(lefthsv, righthsv);
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

        if (lefthsv[1] > 0.25f ) {
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


// 00 Blue
// 10
// 20 Light blue
// 30
// 40 Cyan
// 50
// 60 Green
// 70
// 80 Yellow
// 90
// a0 Red
// b0
// c0 Magenta
// d0
// e0 Magenta
// f0 Blue



