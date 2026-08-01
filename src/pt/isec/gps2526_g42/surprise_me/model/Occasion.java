package pt.isec.gps2526_g42.surprise_me.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public enum Occasion implements Serializable {
    OTHER("Other"),
    ANNIVERSARY("Anniversary"),
    BABY_SHOWER("Baby Shower"),
    BIRTH("Birth"),
    BIRTHDAY("Birthday"),
    CHRISTMAS("Christmas"),
    EASTER("Easter"),
    FAREWELL("Farewell"),
    FATHERS_DAY("Father's Day"),
    GET_WELL_SOON("Get well soon"),
    GRADUATION("Graduation"),
    HOUSEWARMING("Housewarming"),
    MOTHERS_DAY("Mother's Day"),
    NEW_YEAR("New Year"),
    PROMOTION("Promotion"),
    RETIREMENT("Retirement"),
    THANK_YOU("Thank you"),
    VALENTINES_DAY("Valentine's Day"),
    WEDDING("Wedding");

    @Serial
    private static final long serialVersionUID = 300L;

    private final String value;

    // Constructor
    Occasion(String value) {
        this.value = value;
    }

    // Getter for value
    public String getOccasion() {
        return value;
    }

    // Getter for all occasions
    public static ArrayList<String> getAllOccasions() {
        ArrayList<String> occasions = new ArrayList<>();
        for (Occasion occasion : Occasion.values()) {
            occasions.add(occasion.getOccasion());
        }
        return occasions;
    }

    // Returns an Occasion corresponding to the string
    public static Occasion convertFromString(String text) {
        for (Occasion o : Occasion.values()) {
            if (o.getOccasion().equalsIgnoreCase(text.trim())) {
                return o;
            }
        }
        return Occasion.OTHER;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase() + " (" + getOccasion() + ")";
    }
}
