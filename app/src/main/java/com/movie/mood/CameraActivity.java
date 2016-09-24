package com.movie.mood;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.movie.mood.Camera2BasicFragment.OnImageCapturedListener;

public class CameraActivity extends AppCompatActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_camera);

        Camera2BasicFragment frag;
        if (savedInstanceState == null) {
            frag = Camera2BasicFragment.newInstance();

            getFragmentManager().beginTransaction()
                    .add(R.id.container, frag, "camerafrag")
                    .commit();
        } else {
            frag = (Camera2BasicFragment) getFragmentManager().findFragmentByTag("camerafrag");
        }


        frag.setOnImageCapturedListener(new OnImageCapturedListener() {

            @Override public void onImageCaptured(Bitmap bitmap) {


                // TODO

            }
        });
    }

}
