package de.hwr_berlin.quizapp.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import de.hwr_berlin.quizapp.activities.MainActivity;
import de.hwr_berlin.quizapp.events.CategoriesLoadedEvent;
import de.hwr_berlin.quizapp.network.models.Category;
import de.hwr_berlin.quizapp.network.CategoryService;

/**
 * Created by oruckdeschel on 03.03.2016.
 */

public class CategoryLoaderTask extends AsyncTask<Void, Void, List<Category>> {

    private Context context;

    public CategoryLoaderTask(Context context) {
        this.context = context;
    }

    @Override
    protected List<Category> doInBackground(Void... voids) {
        CategoryService service = new CategoryService(context);
        return service.getAllCategories();
    }

    @Override
    protected void onPostExecute(List<Category> result) {
        if (result == null) {
            return;
        }
        MainActivity.CATEGORY_STORAGE.setCategoryList(result);
        MainActivity.eventBus.post(new CategoriesLoadedEvent());
    }
}
