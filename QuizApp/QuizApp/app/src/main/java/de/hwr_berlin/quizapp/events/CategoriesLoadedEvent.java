package de.hwr_berlin.quizapp.events;

import android.util.Log;

/**
 * Created by oruckdeschel on 03.03.2016.
 */
public class CategoriesLoadedEvent {

    private static final String TAG = CategoriesLoadedEvent.class.getSimpleName();

    public CategoriesLoadedEvent() {
        Log.d(TAG, "New CategoriesLoadedEvent -> New categories were loaded.");
    }

}
