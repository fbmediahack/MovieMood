package com.movie.mood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import de.toman.milight.WiFiBox;

public class MainActivity extends AppCompatActivity {

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

        final int[] color = colourAverager.averagePixelsInImage(bitmap, 2);
        left.setBackgroundColor(color[0]);
        right.setBackgroundColor(color[1]);
        Intent intent = new Intent(this, LightIntentService.class);
        intent.putExtra(LightIntentService.EXTRA_MILLIS, SystemClock.elapsedRealtime());
        intent.putExtra(LightIntentService.EXTRA_GROUP, 1);
        intent.putExtra(LightIntentService.EXTRA_COMMAND, LightIntentService.COMMAND_BRIGHTNESS);
        intent.putExtra(LightIntentService.EXTRA_VALUE, WiFiBox.MAX_BRIGHTNESS/5);
        startService(intent);

        intent.putExtra(LightIntentService.EXTRA_MILLIS, SystemClock.elapsedRealtime());
        intent.putExtra(LightIntentService.EXTRA_GROUP, 1);
        intent.putExtra(LightIntentService.EXTRA_COMMAND, LightIntentService.COMMAND_COLOUR);
        intent.putExtra(LightIntentService.EXTRA_VALUE, 0xa0);
        startService(intent);
    }
}
