package pt.isec.gps2526_g42.surprise_me.model.data;

import pt.isec.gps2526_g42.surprise_me.model.Occasion;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class Event implements Serializable {
    @Serial
    private static final long serialVersionUID = 300L;
    private static int nextId = 1;

    // Attributes
    private final int idEvent;
    private String name;
    private LocalDate date;

    // Enums
    private Occasion occasion;
    Enjoyer enjoyer;

    // soft delete flag
    private boolean deleted;

    public Event(String name, LocalDate date, Occasion occasion, Enjoyer enjoyer) {
        this.idEvent = nextId++;
        this.name = name;
        this.date = date;
        this.occasion = occasion;
        this.enjoyer = enjoyer;
        // Adds this event to the enjoyer list
        if (this.enjoyer != null) {
            this.enjoyer.addEvent(this);
        }
    }

    public static void setNextId(int newId) {
        nextId = newId;
    }

    int getIdEvent() {
        return idEvent;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    Occasion getOccasion() {
        return occasion;
    }

    void editEvent(String name, LocalDate date, Occasion occasion, Enjoyer enjoyer) {
        // Se o enjoyer atual for diferente do novo, remove a ligação no enjoyer antigo
        if (this.enjoyer != null && this.enjoyer != enjoyer) {
            this.enjoyer.removeEvent(this);
        }

        this.name = name;
        this.date = date;
        this.occasion = occasion;
        this.enjoyer = enjoyer;

        // Garante que o evento está presente na lista do enjoyer atual
        if (this.enjoyer != null) {
            this.enjoyer.addEvent(this);
        }
    }

    void removeEnjoyer() {
        if (enjoyer != null) {
            enjoyer.removeEvent(this);
        }
    }

    public String getEnjoyerName() {
        if (enjoyer != null) {
            return enjoyer.getDetails().getName();
        }
        return "Unknown";
    }

    Enjoyer getEnjoyer() {
        return enjoyer;
    }

    boolean isDeleted() {
        return deleted;
    }

    void markDeleted() {
        this.deleted = true;
    }
}
