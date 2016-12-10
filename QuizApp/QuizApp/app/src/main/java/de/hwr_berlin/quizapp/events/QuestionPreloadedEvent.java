package de.hwr_berlin.quizapp.events;

import android.util.Log;

import de.hwr_berlin.quizapp.network.models.Question;

/**
 * Created by oruckdeschel on 16.03.2016.
 */
public class QuestionPreloadedEvent {

    private static final String TAG = QuestionPreloadedEvent.class.getSimpleName();

    public Question question;

    public QuestionPreloadedEvent(Question question) {
        Log.d(TAG, "Question preloaded. QID:" + (question != null ? question.getId() : "null"));
        this.question = question;
    }

}
