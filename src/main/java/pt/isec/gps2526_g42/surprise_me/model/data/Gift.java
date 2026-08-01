package pt.isec.gps2526_g42.surprise_me.model.data;

import pt.isec.gps2526_g42.surprise_me.model.Feedback;
import pt.isec.gps2526_g42.surprise_me.model.Occasion;
import pt.isec.gps2526_g42.surprise_me.model.Status;
import pt.isec.gps2526_g42.surprise_me.model.Type;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class Gift implements Serializable {
    @Serial
    private static final long serialVersionUID = 300L;
    private static int nextId = 1;

    // Attributes
    private final int idGift;
    private final String name;
    private final LocalDate date;
    private String giftMessage;

    // Enums
    private Feedback feedback;
    private Status status;
    private final Type type;
    private final Occasion occasion;

    // Reference to the Enjoyer
    private final Enjoyer enjoyer;

    public Gift(Gift other) {
        this.idGift = other.idGift;
        this.name = other.name;
        this.date = other.date;
        this.giftMessage = other.giftMessage;
        this.feedback = other.feedback;
        this.status = other.status;
        this.type = other.type;
        this.occasion = other.occasion;
        this.enjoyer = other.enjoyer;
    }

    public Gift(String name, Type type, Occasion occasion, Enjoyer enjoyer) {
        this.idGift = nextId++;
        this.status = Status.PENDING;
        this.feedback = Feedback.UNKNOWN;
        this.name = name;
        this.date = LocalDate.now();
        this.type = type;
        this.occasion = occasion;
        this.enjoyer = enjoyer;
        // Adds this gift to the enjoyer list
        if (this.enjoyer != null) {
            this.enjoyer.addGift(this);
        }
    }

    public static void setNextId(int newId) {
        nextId = newId;
    }

    public int getIdGift() {
        return idGift;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getGiftMessage() {
        return giftMessage;
    }

    public String getFeedback() {
        return feedback.getFeedback();
    }

    public String getStatus() {
        return status.getStatus();
    }

    public String getType() {
        return type.getType();
    }

    public String getOccasion() {
        return occasion.getOccasion();
    }

    public Enjoyer getEnjoyer() {
        return enjoyer;
    }

    void editGift(Feedback feedback, Status status) {
        this.feedback = feedback;
        this.status = status;
    }

    void setGiftMessage(String giftMessage) {
        this.giftMessage = giftMessage;
    }
}
