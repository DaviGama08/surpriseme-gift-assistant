package pt.isec.gps2526_g42.surprise_me.model.data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

// Class that encapsulates all the enjoyer details
public class EnjoyerDetails implements Serializable {
    @Serial
    private static final long serialVersionUID = 300L;

    private String name;
    private String relationship;
    private LocalDate birthDate;
    private String notes;
    private ArrayList<String> likes;
    private ArrayList<String> dislikes;
    private String city;
    private String country;

    public EnjoyerDetails() {
        likes = new ArrayList<>();
        dislikes = new ArrayList<>();
    }

    public EnjoyerDetails(String name, String relationship, LocalDate birthDate, String notes, ArrayList<String> likes, ArrayList<String> dislikes, String city, String country) {
        this.name = name;
        this.relationship = relationship;
        this.birthDate = birthDate;
        this.notes = notes;
        if (likes == null) {
            this.likes = new ArrayList<>();
        } else {
            this.likes = new ArrayList<>(likes);
        }
        if (dislikes == null) {
            this.dislikes = new ArrayList<>();
        } else {
            this.dislikes = new ArrayList<>(dislikes);
        }
        this.city = city;
        this.country = country;
    }

    public EnjoyerDetails(EnjoyerDetails other) {
        this.name = other.getName();
        this.relationship = other.getRelationship();
        this.birthDate = other.getBirthDate();
        this.notes = other.getNotes();
        this.likes = other.getLikes();
        this.dislikes = other.getDislikes();
        this.city = other.getCity();
        this.country = other.getCountry();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            return;
        }

        String n = name.trim();

        if (n.isBlank() || n.length() < 3) {
            return;
        }

        n = n.replaceAll("\\s+", " ");

        this.name = n;
    }


    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ArrayList<String> getLikes() {
        if (likes == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(likes);
    }

    public void setLikes(ArrayList<String> likes) {
        if (likes == null) {
            return;
        }
        this.likes = new ArrayList<>(likes);
    }

    public ArrayList<String> getDislikes() {
        if (dislikes == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(dislikes);
    }

    public void setDislikes(ArrayList<String> dislikes) {
        if (dislikes == null) {
            return;
        }
        this.dislikes = new ArrayList<>(dislikes);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
