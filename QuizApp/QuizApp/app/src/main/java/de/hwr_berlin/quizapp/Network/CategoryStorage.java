package de.hwr_berlin.quizapp.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hwr_berlin.quizapp.activities.MainActivity;
import de.hwr_berlin.quizapp.network.models.Category;

/**
 * Created by oruckdeschel on 03.03.2016.
 */
public class CategoryStorage {

    private static Map<Category, Boolean> categories = new HashMap<>();

    public static boolean isDefined() {
        return categories != null;
    }

    public static List<Category> getCategoryList() {
        return new ArrayList<>(categories.keySet());
    }

    public static void setCategoryList(List<Category> categoryList) {
        Collections.shuffle(categoryList);

        for (Category category : categoryList) {
            CategoryStorage.categories.put(category, false);
        }
    }

    public static void setCategoryState(Category category, boolean answered) {
        for (Map.Entry<Category, Boolean> categoryEntry : categories.entrySet()) {
            if (categoryEntry.getKey().getId() == category.getId()) {
                categoryEntry.setValue(answered);
                return;
            }
        }
    }

    public static Category getCategory(int id) {
        for (Category category : MainActivity.CATEGORY_STORAGE.categories.keySet()) {
            if (category.getId() == id) {
                return category;
            }
        }
        return null;
    }

    public static List<Category> getUnansweredCategories() {
        if (! MainActivity.CATEGORY_STORAGE.isDefined()) {
            return null;
        }
        List<Category> unansweredCategories = new ArrayList<>();

        for (Map.Entry<Category, Boolean> categoryEntry : categories.entrySet()) {
            if ( ! categoryEntry.getValue()) {
                // false -> category was not answered
                unansweredCategories.add(categoryEntry.getKey());
            }
        }
        return unansweredCategories;
    }

    public static Category getNextUnansweredCategory() {
        if (! MainActivity.CATEGORY_STORAGE.isDefined()) {
            return null;
        }

        for (Map.Entry<Category, Boolean> categoryEntry : categories.entrySet()) {
            if ( ! categoryEntry.getValue()) {
                // false -> category was not answered
                return categoryEntry.getKey();
            }
        }

        return null;
    }

    /**
     * Returns the total amount of categories. All Categories are counted (answered or unanswered)
     * @return
     */
    public static int getCategoryCount() {
        return MainActivity.CATEGORY_STORAGE.categories.size();
    }

    public static boolean isAlreadyAnswered(Category category) {
        return MainActivity.CATEGORY_STORAGE.getCategory(category.getId()).isAnswered();
    }

    public static boolean hasUnansweredCategories() {
        // Check if the map contains an unanswered question (some value has to be false...)
        return categories.containsValue(false);
    }

    public static void clear() {
        categories.clear();
    }

}
