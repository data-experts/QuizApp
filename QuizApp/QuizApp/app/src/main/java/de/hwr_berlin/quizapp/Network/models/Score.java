package de.hwr_berlin.quizapp.network.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by oruckdeschel on 29.02.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Score {

    @JsonProperty("ID")
    private int id;

    @JsonProperty("USERNAME")
    private String name;

    @JsonProperty("SCORE")
    private int score;

    public Score() {
        // no operation just for jackson
    }

    public Score(int id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    public Score(String name, int finalGameScore) {
        this.name = name;
        score = finalGameScore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
