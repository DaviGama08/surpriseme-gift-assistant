package pt.isec.gps2526_g42.surprise_me.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public enum Status implements Serializable {
    PENDING("Pending"),
    GIFTED("Gifted");

    @Serial
    private static final long serialVersionUID = 300L;

    private final String value;

    // Constructor
    Status(String value) {
        this.value = value;
    }

    // Getter for value
    public String getStatus() {
        return value;
    }

    // Getter for all statuses
    public static ArrayList<String> getAllStatuses() {
        ArrayList<String> statuses = new ArrayList<>();
        for (Status status : Status.values()) {
            statuses.add(status.getStatus());
        }
        return statuses;
    }

    // Returns a Status corresponding to the string
    public static Status convertFromString(String text) {
        for (Status s : Status.values()) {
            if (s.getStatus().equalsIgnoreCase(text.trim())) {
                return s;
            }
        }
        return Status.PENDING;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase() + " (" + getStatus() + ")";
    }

}
