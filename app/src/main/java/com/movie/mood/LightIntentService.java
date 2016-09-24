package com.movie.mood;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.IntDef;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.UnknownHostException;

import de.toman.milight.WiFiBox;

public class LightIntentService extends IntentService {

    public static final String EXTRA_GROUP = "de.toman.milight.WiFiBox.GROUP";
    public static final String EXTRA_COMMAND = "de.toman.milight.WiFiBox.COMMAND";
    public static final String EXTRA_VALUE = "de.toman.milight.WiFiBox.VALUE";
    public static final String EXTRA_MILLIS = "de.toman.milight.WiFiBox.MILLIS";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({COMMAND_COLOUR, COMMAND_BRIGHTNESS, COMMAND_WHITE})
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
        if (SystemClock.elapsedRealtime() - millis > 10L) { // Prevent queue build-up.
            return;
        }
        int group = intent.getIntExtra(EXTRA_GROUP, 0);
        int command = intent.getIntExtra(EXTRA_COMMAND, 0);
        int value = intent.getIntExtra(EXTRA_VALUE, 0);
        try {
            switch (command) {
                case COMMAND_BRIGHTNESS:
                    wiFiBox.brightness(group, value);
                    break;
                case COMMAND_COLOUR:
                    wiFiBox.color(group, value);
                    break;
                case COMMAND_WHITE:
                    wiFiBox.white(group);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Unlucky!
        }
    }
}
