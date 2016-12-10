package de.hwr_berlin.quizapp.models;

import android.util.Log;

import org.greenrobot.eventbus.Subscribe;

import de.hwr_berlin.quizapp.activities.MainActivity;
import de.hwr_berlin.quizapp.events.AnswerEvent;

/**
 * Created by oruckdeschel on 14.03.2016.
 */
public class GameScoreStorage {

    private static final String TAG = GameScoreStorage.class.getSimpleName();
    private static final int AVERAGE_TIME = 7500;
    private static final int NORMAL_QUESTION_POINTS = 100;

    private static int currentGameScore = 0;
    private static int timeBonus = 0;
    private static int currentCombo = 1;

    public GameScoreStorage() {
        MainActivity.eventBus.register(this);
    }

    private void resetCombo() {
        GameScoreStorage.currentCombo = 1;
    }

    public int getFinalGameScore() {
        //int askedQuestionCount = QuizActivity.quiz.getAskedQuestionCount();
        //askedQuestionCount = askedQuestionCount == 0 ? 1 : askedQuestionCount;
        //return Math.round(currentGameScore / askedQuestionCount);
        return currentGameScore;
    }

    public void resetGameScore() {
        currentGameScore = 0;
        resetCombo();
    }

    // TODO: what about not answered categories? otherwise combine getFinalGameScore() and this method!
    public int getCurrentGameScore() {
        //int askedQuestionCount = QuizActivity.quiz.getAskedQuestionCount();
        //askedQuestionCount = askedQuestionCount == 0 ? 1 : askedQuestionCount;
        //return Math.round(currentGameScore / askedQuestionCount);
        return currentGameScore;
    }

    @Subscribe
    public void onAnswerEvent(AnswerEvent event) {
        Log.d(TAG, "Current gamescore: " + currentGameScore);
        if (event.answerCorrect) {
            //currentGameScore += Math.round(((float)Math.sqrt(currentCombo)) * (AVERAGE_TIME / ((float)event.timeNeeded)*5) * 100);
            timeBonus = Math.round((NORMAL_QUESTION_POINTS / 2) * (1 - ((float)event.timeNeeded / AVERAGE_TIME)));
            if (timeBonus > 0) {
                currentGameScore += Math.round(currentCombo * (NORMAL_QUESTION_POINTS + (timeBonus)));
            }
            else if (timeBonus <= 0) {
                currentGameScore += Math.round(currentCombo * NORMAL_QUESTION_POINTS);
            }
            currentCombo++;
        } else {
            // wrong answer was given
            if (currentGameScore >= 0 && currentGameScore - 50 >= 0) {
                currentGameScore -= 50;
            } else if (currentGameScore < 50) {
                currentGameScore = 0;
            }
            resetCombo();
        }
        Log.d(TAG, "New gamescore: " + currentGameScore);
    }

}
