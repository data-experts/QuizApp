package de.hwr_berlin.quizapp.network.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.hwr_berlin.quizapp.models.CategorieColors;

/**
 * Created by oruckdeschel on 29.02.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {

    @JsonProperty("ID")
    private int id;

    @JsonProperty("NAME")
    private String name;

    @JsonIgnore
    private boolean answered = false; // This field is needed to indicate whether a category already got a correct answer or not.

    @JsonIgnore
    private String color;

    public Category() {
        // no code. just for jackson
    }

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
        color = CategorieColors.getNextColor();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public String getColor() {
        if (color == null) {
            color = CategorieColors.getNextColor();
        }
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
