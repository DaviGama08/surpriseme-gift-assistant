package pt.isec.gps2526_g42.surprise_me.model.data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class UserDetails implements Serializable {
    @Serial
    private static final long serialVersionUID = 300L;

    private String name;
    private String email;
    private String password;
    private String country;
    private String city;
    private LocalDate birthDate;
    private String avatarPath; // relative path inside data dir (e.g., avatars/abc.jpg)

    public UserDetails(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public UserDetails(UserDetails other) {
        this.name = other.name;
        this.email = other.email;
        this.country = other.country;
        this.city = other.city;
        this.birthDate = other.birthDate;
        this.avatarPath = other.avatarPath;
    }

    public UserDetails getUserDetails() {
        return new UserDetails(this);
    }

    public void setUserDetails(UserDetails other) {
        this.name = other.name;
        this.country = other.country;
        this.city = other.city;
        this.email = other.email;
        this.birthDate = other.birthDate;
        this.avatarPath = other.avatarPath;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return password;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    void setPasswordHash(String password) {
        this.password = password;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
}
