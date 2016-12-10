package de.hwr_berlin.quizapp.network;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import de.hwr_berlin.quizapp.activities.MainActivity;
import de.hwr_berlin.quizapp.network.models.Category;
import de.hwr_berlin.quizapp.network.models.Question;

/**
 * Created by oruckdeschel on 29.02.2016.
 */
public class QuestionsService {

    private Context context;

    private static final String TAG = QuestionsService.class.getSimpleName();

    private static final String QUESTION_SERVICE = "get_question.php";
    private static final String QUESTION_SERVICE_IGNORED = "get_question_ignore.php";
    private static final String PATH_CATEGORY = "category";
    private static final String PATH_IGNORED = "ignored";

    public QuestionsService(Context context) {
        this.context = context;
    }

    public Question getRandomQuestion() {
        return getRandomQuestion(null, null);
    }

    public Question getRandomQuestion(Category category, Question[] askedQuestions) {
        if (category == null) {
            // Random category
            category = MainActivity.CATEGORY_STORAGE.getNextUnansweredCategory();
            Log.d(TAG, "Category was null. New category: " + category);
        }

        if (! NetworkUtil.hasInternetConnection(context)) {
            return null;
        }

        StringBuilder responseStringBuilder = null;
        ObjectMapper mapper = null;
        try {
            responseStringBuilder = new StringBuilder();

            String url = (askedQuestions == null || askedQuestions.length <= 0) ? buildUrl(category) : buildUrl(category, askedQuestions);

            InputStream stream = NetworkUtil.makeRequest(context, url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String line;
            while ((line = reader.readLine()) != null) {
                responseStringBuilder.append(line);
            }

            mapper = new ObjectMapper();
            return mapper.readValue(responseStringBuilder.toString().replace("\uFEFF", ""), Question.class);

        } catch (JsonMappingException e) {
            Log.e(TAG, "Jackson could not map the object.", e);
            if (mapper != null && responseStringBuilder != null) {
                try {
                    de.hwr_berlin.quizapp.network.models.Error error = mapper.readValue(responseStringBuilder.toString().replace("\uFEFF", ""), de.hwr_berlin.quizapp.network.models.Error.class);
                    Log.e(TAG, "Serverside error: " + error.getError());
                } catch (IOException e1) {
                    Log.wtf(TAG, "Now we got a problem. The result from the server is neither a Question nor an Error.", e1);
                }
            } else {
                Log.e(TAG, "Mapper or responseString is null. mapper: " + mapper + " ||| responseStringBuilder: " + responseStringBuilder);
            }

        } catch (IOException e) {
            Log.e(TAG, "IOException while fetching a new Question.", e);
            // if we want dummy questions ...
            // return tempServiceLoad();
        }

        return null;
    }

    public List<Question> getAllQuestions() {
        return getAllQuestions(null);
    }

    public List<Question> getAllQuestions(Category category) {
        return null;
    }

    private static String buildUrl(Category category) {
        if (category == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (! NetworkUtil.BASE_URL.startsWith("http://")) {
            sb.append("http://");
        }
        sb.append(NetworkUtil.BASE_URL)
            .append((NetworkUtil.BASE_URL.endsWith("/") ? "" : "/"))
            .append(QUESTION_SERVICE)
            .append("?")
            .append(PATH_CATEGORY)
            .append("=")
            .append(category.getId());

        String result = sb.toString();
        Log.i(TAG, "Builded URL: " + result);
        return result;
    }

    private static String buildUrl(Category category, Question[] askedQuestions) {
        if (category == null) {
            return null;
        }
        if (askedQuestions == null || askedQuestions.length <= 0) {
            return buildUrl(category);
        }

        StringBuilder sb = new StringBuilder();
        if (! NetworkUtil.BASE_URL.startsWith("http://")) {
            sb.append("http://");
        }
        sb.append(NetworkUtil.BASE_URL)
            .append((NetworkUtil.BASE_URL.endsWith("/") ? "" : "/"))
            .append(QUESTION_SERVICE_IGNORED)
            .append("?")
            .append(PATH_CATEGORY)
            .append("=")
            .append(category.getId())
            .append("&")
            .append(PATH_IGNORED)
            .append("=");

        for (int i = 0; i < askedQuestions.length; i++) {
            sb.append(askedQuestions[i].getId());
            if (i != askedQuestions.length - 1) {
                sb.append(",");
            }
        }

        String result = sb.toString();
        Log.i(TAG, "Builded URL: " + result);
        return result;

    }

    private static Question tempServiceLoad() {
        Log.d(TAG, "Fallback Question loaded.");
        return new Question(new Category(1, "Sport"), "Frage?", "Antwort 1", "Jo2", "Antwort 3", "Antwort 4");
    }

}
