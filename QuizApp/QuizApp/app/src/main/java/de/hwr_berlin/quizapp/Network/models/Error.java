package de.hwr_berlin.quizapp.network.models;

/**
 * Created by oruckdeschel on 14.03.2016.
 */
public class Error {

    private String error;

    public Error() {
        // no action - just for jackson
    }

    public Error(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
