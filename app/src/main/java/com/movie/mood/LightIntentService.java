package com.movie.mood;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.util.Log;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.UnknownHostException;

import de.toman.milight.WiFiBox;

public class LightIntentService extends IntentService {

    public static final String LEFT_COLOR = "LEFT_COLOR";
    public static final String RIGHT_COLOR = "RIGHT_COLOR";
    public static final String EXTRA_MILLIS = "de.toman.milight.WiFiBox.MILLIS";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ COMMAND_COLOUR, COMMAND_BRIGHTNESS, COMMAND_WHITE })
    public @interface Command {
    }

    public static final int COMMAND_COLOUR = 1;
    public static final int COMMAND_BRIGHTNESS = 2;
    public static final int COMMAND_WHITE = 3;

    private static final String NAME = "LightIntentService";

    private WiFiBox wiFiBox;
    private boolean hostOk = false;

    public LightIntentService() {
        super(NAME);
        try {
            wiFiBox = new WiFiBox("192.168.0.122");
            hostOk = true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!hostOk) {
            return;
        }
        long millis = intent.getLongExtra(EXTRA_MILLIS, 0);
        if (SystemClock.elapsedRealtime() - millis > 50) { // Prevent queue build-up.
            Log.d("error", "discarded");
            return;
        }

        int leftRgb = intent.getIntExtra(LEFT_COLOR, 0);
        int rightRgb = intent.getIntExtra(RIGHT_COLOR, 0);

        float[] lefthsv = new float[3];
        Color.colorToHSV(leftRgb, lefthsv);

        float[] righthsv = new float[3];
        Color.colorToHSV(rightRgb, righthsv);

        final int leftColor = correctColor(lefthsv[0]);
        final int rightColor = correctColor(righthsv[0]);

        try {
            if (lefthsv[1] > 0.01f) {
                wiFiBox.color(1, leftColor);
            } else {
                wiFiBox.white(1);
            }
            wiFiBox.brightness(1, (int) (WiFiBox.MAX_BRIGHTNESS * lefthsv[2]));

            if (righthsv[1] > 0.01f) {
                wiFiBox.color(2, rightColor);
            } else {
                wiFiBox.white(2);
            }
            wiFiBox.brightness(2, (int) (WiFiBox.MAX_BRIGHTNESS * righthsv[2]));
            //wiFiBox.white(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int correctColor(float hue) {
        return shift(normalise(hue));
    }

    int normalise(float h) {
        return (int) ((h / 360) * 255);
    }

    int shift(int c) {
        return ((255 - c) - 80 + 255) % 256;
    }
}
