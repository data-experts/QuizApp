package de.hwr_berlin.quizapp.events;

import android.util.Log;

/**
 * Created by oruckdeschel on 04.04.2016.
 */
public class HighscoreUploadedEvent {

    private static final String TAG = HighscoreUploadedEvent.class.getSimpleName();

    public boolean success;

    public HighscoreUploadedEvent(boolean success) {
        Log.d(TAG, "Highscore was uploaded. Success: " + success);
        this.success = success;
    }

}
