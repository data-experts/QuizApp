package de.hwr_berlin.quizapp.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by oruckdeschel on 14.04.2016.
 */
public class CategorieColors {

    private static List<String> colors = new ArrayList<>();

    private static int currentIndex = 0;

    static {
        // fill with colors
        colors.add("#ffd940"); // gelb
        colors.add("#ff4040"); // rot
        colors.add("#40c4ff"); // blau
        colors.add("#54ff60"); // grün
        colors.add("#4079ff"); // dunkelblau
        colors.add("#ff40f2"); // pink
        colors.add("#ff8340"); // orange
        colors.add("#b340ff"); // violet
    }

    public static String getNextColor() {
        if (currentIndex >= colors.size()) {
            currentIndex = 0;
        }
        return colors.get(currentIndex++); // increment index for the next question to get the next question
    }

}
