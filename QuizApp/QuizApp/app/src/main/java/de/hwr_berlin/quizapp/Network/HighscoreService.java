package de.hwr_berlin.quizapp.network;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import de.hwr_berlin.quizapp.network.models.Error;
import de.hwr_berlin.quizapp.network.models.Score;

/**
 * Created by oruckdeschel on 02.03.2016.
 */
public class HighscoreService {

    private Context context;

    private static final String TAG = HighscoreService.class.getSimpleName();

    private static final String GET_HIGHSCORE_SERVICE = "get_all_highscore.php";
    private static final String SET_HIGHSCORE_SERVICE = "set_highscore.php";
    private static final String NAME = "name";
    private static final String SCORE = "score";

    public HighscoreService(Context context) {
        this.context = context;
    }

    public void uploadHighscore(String name, int highscore) {
        Log.d(TAG, "Upload highscore to online database.");

        if (! NetworkUtil.hasInternetConnection(context)) {
            return;
        }

        try {
            NetworkUtil.makeRequest(context, buildPostUrl(name, highscore));
        } catch (IOException e) {
            Log.e(TAG, "IOException while uploading the highscore", e);
        }
    }

    public List<Score> getHighscore(int page) {
        // This method should return a list of 10 scores.

        if (! NetworkUtil.hasInternetConnection(context)) {
            return null;
        }

        StringBuilder responseStringBuilder = null;
        try {
            responseStringBuilder = new StringBuilder();

            InputStream stream = NetworkUtil.makeRequest(context, buildGetUrl(page));

            if (stream == null) {
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String line;
            while ((line = reader.readLine()) != null) {
                responseStringBuilder.append(line);
            }

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<Score[]> categoryTypeRef = new TypeReference<Score[]>(){};

            Score[] scoreArray = mapper.readValue(responseStringBuilder.toString().replace("\uFEFF", ""), categoryTypeRef);
            return Arrays.asList(scoreArray);

        } catch (IOException e) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                TypeReference<Error> errorTypeReference = new TypeReference<Error>(){};

                if (responseStringBuilder != null) {
                    Error error = mapper.readValue(responseStringBuilder.toString().replace("\uFEFF", ""), errorTypeReference);
                    Log.e(TAG, error.getError());
                }

            } catch (IOException ex) {
                Log.e(TAG, "IOException while fetching the highscores and parsing to error.", ex);
            }
            return null;
        }
    }

    private static String buildGetUrl(int page) {
        StringBuilder sb = new StringBuilder();
        if (! NetworkUtil.BASE_URL.startsWith("http://")) {
            sb.append("http://");
        }
        sb.append(NetworkUtil.BASE_URL);
        sb.append((NetworkUtil.BASE_URL.endsWith("/") ? "" : "/"));

        sb.append(GET_HIGHSCORE_SERVICE);
        sb.append("?page=");
        sb.append(page);

        String result = sb.toString();
        Log.i(TAG, "Builded Get URL: " + result);
        return result;
    }

    private static String buildPostUrl(String name, int score) {
        StringBuilder sb = new StringBuilder();
        if (! NetworkUtil.BASE_URL.startsWith("http://")) {
            sb.append("http://");
        }
        sb.append(NetworkUtil.BASE_URL);
        sb.append((NetworkUtil.BASE_URL.endsWith("/") ? "" : "/"));
        sb.append(SET_HIGHSCORE_SERVICE);
        sb.append("?");
        sb.append(NAME);
        sb.append("=");
        sb.append(name);
        sb.append("&");
        sb.append(SCORE);
        sb.append("=");
        sb.append(score);

        String result = sb.toString();
        Log.i(TAG, "Builded Post URL: " + result);
        return result;
    }
}
