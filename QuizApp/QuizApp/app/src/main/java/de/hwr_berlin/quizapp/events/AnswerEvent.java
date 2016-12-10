package de.hwr_berlin.quizapp.events;

import android.util.Log;

/**
 * Created by oruckdeschel on 14.03.2016.
 */
public class AnswerEvent {

    private static final String TAG = AnswerEvent.class.getSimpleName();

    public boolean answerCorrect;
    public long timeNeeded;

    public AnswerEvent(boolean answerCorrect, long timeNeeded) {
        Log.d(TAG, "Answer was given. Answer was " + (answerCorrect ? "correct" : "false"));
        this.answerCorrect = answerCorrect;
        this.timeNeeded = timeNeeded;
    }

}
