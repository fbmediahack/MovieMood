package com.movie.mood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

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
            @Override public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
            }
        });
    }

    @Override protected void onResume() {
        super.onResume();
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();

        final int color = colourAverager.averagePixelsInImage(bitmap);
        left.setBackgroundColor(color);
        right.setBackgroundColor(color);
    }
}
