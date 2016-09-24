package com.movie.mood;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class CameraActivity extends AppCompatActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        if (savedInstanceState == null) {
            Camera2BasicFragment frag = Camera2BasicFragment.newInstance();

            getFragmentManager().beginTransaction()
                    .add(R.id.container, frag)
                    .commit();
        }
    }

}
