package pt.isec.gps2526_g42.surprise_me.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public enum Feedback implements Serializable {
    UNKNOWN("-"),
    GOOD("Good"),
    NEUTRAL("Neutral"),
    BAD("Bad");

    @Serial
    private static final long serialVersionUID = 300L;
    private final String value;

    // Constructor
    Feedback(String value) {
        this.value = value;
    }

    // Getter for value
    public String getFeedback() {
        return value;
    }

    // Getter for all feedbacks
    public static ArrayList<String> getAllFeedbacks() {
        ArrayList<String> feedbacks = new ArrayList<>();
        for (Feedback feedback : Feedback.values()) {
            feedbacks.add(feedback.getFeedback());
        }
        return feedbacks;
    }

    // Returns a Feedback corresponding to the string
    public static Feedback convertFromString(String text) {
        for (Feedback f : Feedback.values()) {
            if (f.getFeedback().equalsIgnoreCase(text.trim())) {
                return f;
            }
        }
        return Feedback.UNKNOWN;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase() + " (" + getFeedback() + ")";
    }
}
