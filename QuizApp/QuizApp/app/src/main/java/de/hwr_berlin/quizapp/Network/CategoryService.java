package de.hwr_berlin.quizapp.network;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hwr_berlin.quizapp.network.models.Category;

/**
 * Created by oruckdeschel on 03.03.2016.
 */
public class CategoryService {

    private Context context;

    private static final String TAG = CategoryService.class.getSimpleName();
    private static final String CATEGORY_SERVICE = "get_all_categories.php";

    public CategoryService(Context context) {
        this.context = context;
    }

    public List<Category> getAllCategories() {

        List<Category> result = new ArrayList<>();

        try {
            StringBuilder responseStringBuilder = new StringBuilder();

            InputStream stream = NetworkUtil.makeRequest(context, buildUrl());
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String line;
            while ((line = reader.readLine()) != null) {
                responseStringBuilder.append(line);
            }

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<Category>> categoryTypeRef = new TypeReference<List<Category>>(){};

            result = mapper.readValue(responseStringBuilder.toString().replace("\uFEFF", ""), categoryTypeRef);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException while fetching the categories.", e);
            // if we want dummy categories...
            // result = Arrays.asList(tempServiceLoad());
        } finally {
            return result;
        }
    }

    private String buildUrl() {
        String prefix = NetworkUtil.BASE_URL.startsWith("http://") ? "" : "http://";
        String suffix = NetworkUtil.BASE_URL.endsWith("/") ? CATEGORY_SERVICE : "/" + CATEGORY_SERVICE;
        Log.i(TAG, "Builded URL: " + prefix + NetworkUtil.BASE_URL + suffix);
        return prefix + NetworkUtil.BASE_URL + suffix;
    }

    private Category[] tempServiceLoad() {
        Log.d(TAG, "Fallback categories loaded.");
        return new Category[]{new Category(1, "Sport"), new Category(2, "Computer")};
    }

}
