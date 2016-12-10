package de.hwr_berlin.quizapp.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.hwr_berlin.quizapp.activities.MainActivity;
import de.hwr_berlin.quizapp.activities.QuizActivity;
import de.hwr_berlin.quizapp.events.QuestionPreloadedEvent;
import de.hwr_berlin.quizapp.network.models.Question;
import de.hwr_berlin.quizapp.tasks.QuestionLoaderTask;

/**
 * Created by oruckdeschel on 30.03.2016.
 */
public class Game implements Parcelable {

    private static final String TAG = Game.class.getSimpleName();

    private static GameScoreStorage GAME_SCORE_STORAGE = new GameScoreStorage();

    private static Question currentQuestion;
    private static Queue<Question> preloadedQuestions;
    private static List<Question> askedQuestions;

    private static long start_time;
    private static long time_before_pause = 0;

    static {
        preloadedQuestions = new LinkedList<>();
        askedQuestions = new ArrayList<>();
    }

    private Context context;

    public Game() {

    }

    public Game(Context context) {
        this.context = context;
        MainActivity.eventBus.register(this);
    }

    public Question getQuestion(boolean newQuestion) {
        if (newQuestion || currentQuestion == null) {
            // We currently have no question. -> Load new from preloaded.

            if (preloadedQuestions.size() <= 0) {
                return null;
            }

            currentQuestion = preloadedQuestions.poll();
            askedQuestions.add(currentQuestion);
        }
        return currentQuestion;
    }

    public void startTimeCounter() {
        start_time = System.currentTimeMillis();
    }

    public long stopTimeCounter() {
        return System.currentTimeMillis() - start_time;
    }

    public boolean checkAnswer(Question question, String answer) {
        if (question == null) {
            question = currentQuestion;
        }

        boolean result = answer.equals(question.getAnswer());

        if (!result) {
            // Given answer was wrong! Preload a new question from this category.
            new QuestionLoaderTask(context).execute(question.getCategory());
        } else {
            // Only change the state of the category to answered if the given answer was correct.
            MainActivity.CATEGORY_STORAGE.setCategoryState(question.getCategory(), result);
        }

        return result;
    }

    public boolean checkAnswer(String answer) {
        return checkAnswer(null, answer);
    }

    public boolean isOver() {
        return MainActivity.CATEGORY_STORAGE.isDefined() && ! MainActivity.CATEGORY_STORAGE.hasUnansweredCategories();
    }

    @Subscribe
    public void onEvent(QuestionPreloadedEvent event) {
        addNewQuestion(event.question);
    }

    public static void addNewQuestion(Question question) {
        if (!Game.preloadedQuestions.contains(question)) {
            Game.preloadedQuestions.add(question);
        }
    }

    public int getAskedQuestionCount() {
        return askedQuestions.size();
    }

    public Question[] getAskedQuestions() {
        Question[] result = new Question[askedQuestions.size()];
        askedQuestions.toArray(result);
        return result;
    }

    public int getFinalGameScore() {
        return GAME_SCORE_STORAGE.getFinalGameScore();
    }

    public int getCurrentGameScore() {
        return GAME_SCORE_STORAGE.getCurrentGameScore();
    }

    public static void reset() {
        time_before_pause = 0; // save is save
        start_time = 0;
        currentQuestion = null;
        preloadedQuestions.clear(); // TODO: do we really want to clear this too? We could leave the questions preloaded. Game should end if all categories were answered not if all questions are answered.
        askedQuestions.clear(); // TODO: do we really want to clear this? If we don't clear we will be able to ask every quiz started new questions.
        GAME_SCORE_STORAGE.resetGameScore();
        if (MainActivity.eventBus.isRegistered(QuizActivity.quiz)) {
            MainActivity.eventBus.unregister(QuizActivity.quiz);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        try {
            parcel.writeValue(GAME_SCORE_STORAGE);
        } catch (RuntimeException ex) {
            // We catch because the app crashes on "go back to Main" Button clicked
            Log.e(TAG, "Parcel causes RuntimeException: " + ex, ex);
        }
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Game createFromParcel(Parcel in) {
            Game game = new Game();
            game.GAME_SCORE_STORAGE = (GameScoreStorage) in.readValue(GameScoreStorage.class.getClassLoader());
            return game;
        }

        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    public void pauseGame() {
        time_before_pause = System.currentTimeMillis() - start_time;
        Log.d(TAG, "Game paused. Time: " + time_before_pause);
    }

    public void resumeGame() {
        start_time = System.currentTimeMillis() - time_before_pause;
        Log.d(TAG, "Game resumed. new Starttime: " + start_time);
    }
}
