package de.hwr_berlin.quizapp.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import de.hwr_berlin.quizapp.activities.MainActivity;
import de.hwr_berlin.quizapp.activities.QuizActivity;
import de.hwr_berlin.quizapp.events.QuestionPreloadedEvent;
import de.hwr_berlin.quizapp.network.QuestionsService;
import de.hwr_berlin.quizapp.network.models.Category;
import de.hwr_berlin.quizapp.network.models.Question;

/**
 * Created by oruckdeschel on 03.03.2016.
 */

public class QuestionLoaderTask extends AsyncTask<Category, Void, Question> {

    private Context context;
    private static final String TAG = QuestionLoaderTask.class.getSimpleName();

    public QuestionLoaderTask(Context context) {
        this.context = context;
    }

    @Override
    protected Question doInBackground(Category... categories) {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            Log.v(TAG, element.toString());
        }
        if (!MainActivity.CATEGORY_STORAGE.isDefined()) {
            // Ohoh... The storage was never filled. Please do this now!
            Log.w(TAG, "The category storage is not filled. We should do this now.");
            Handler handler = new Handler(Looper.getMainLooper());
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new CategoryLoaderTask(context).execute();
                }
            });
            return null;
        }

        // Actual load a question
        Log.d(TAG, "Started to preload new random question.");
        QuestionsService service = new QuestionsService(context);

        Question[] askedQuestions = null;
        if (QuizActivity.quiz != null) {
            askedQuestions = QuizActivity.quiz.getAskedQuestions();
        }

        return service.getRandomQuestion(categories[0], askedQuestions);
    }

    @Override
    protected void onPostExecute(Question result) {
        Log.i(TAG, "New question preloaded.");
        // Just post the preload event!
        MainActivity.eventBus.post(new QuestionPreloadedEvent(result));
    }

}
