package pt.isec.gps2526_g42.surprise_me.model.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Enjoyer implements Serializable {
    @Serial
    private static final long serialVersionUID = 300L;
    private static int nextId = 1;

    private final int idEnjoyer;
    private EnjoyerDetails details;
    private final ArrayList<Gift> gifts;
    private final ArrayList<Event> events;

    public Enjoyer(EnjoyerDetails details) {
        this.idEnjoyer = nextId++;
        this.details = new EnjoyerDetails(details);
        this.gifts = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    public static void setNextId(int newId) {
        nextId = newId;
    }

    int getIdEnjoyer() {
        return idEnjoyer;
    }

    void editEnjoyer(EnjoyerDetails details) {
        this.details = new EnjoyerDetails(details);
    }

    public EnjoyerDetails getDetails() {
        return new EnjoyerDetails(details);
    }

    void addGift(Gift newGift) {
        gifts.add(newGift);
    }

    ArrayList<Gift> getGifts() {
        return new ArrayList<>(gifts);
    }

    void addEvent(Event event) {
        events.add(event);
    }

    ArrayList<Event> getEvents() {
        return new ArrayList<>(events);
    }

    void removeEvent(Event event) {
        events.remove(event);
    }
}
