package pt.isec.gps2526_g42.surprise_me.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public enum Type implements Serializable {
    ANY("Any type"),
    PHYSICAL("Physical gift"),
    EXPERIENCE("Experience"),
    DIGITAL("Digital gift"),
    DO_IT_YOURSELF("Do it yourself");

    @Serial
    private static final long serialVersionUID = 300L;

    private final String value;

    // Constructor
    Type(String value) {
        this.value = value;
    }

    // Getter for value
    public String getType() {
        return value;
    }

    // Getter for all types
    public static ArrayList<String> getAllTypes() {
        ArrayList<String> types = new ArrayList<>();
        for (Type type : Type.values()) {
            types.add(type.getType());
        }
        return types;
    }

    // Returns a Type corresponding to the string
    public static Type convertFromString(String text) {
        for (Type t : Type.values()) {
            if (t.getType().equalsIgnoreCase(text.trim())) {
                return t;
            }
        }
        return Type.ANY;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase() + " (" + getType() + ")";
    }

}
