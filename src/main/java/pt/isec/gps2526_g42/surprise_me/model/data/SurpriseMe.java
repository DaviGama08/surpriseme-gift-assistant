package pt.isec.gps2526_g42.surprise_me.model.data;

import pt.isec.gps2526_g42.surprise_me.model.Feedback;
import pt.isec.gps2526_g42.surprise_me.model.Occasion;
import pt.isec.gps2526_g42.surprise_me.model.Status;
import pt.isec.gps2526_g42.surprise_me.model.Type;
import pt.isec.gps2526_g42.surprise_me.security.PasswordHasher;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SurpriseMe implements Serializable {
    @Serial
    private static final long serialVersionUID = 300L;
    private static final int MIN_PASSWORD_LENGTH = 6;

    private final HashMap<Integer, User> users;
    private transient int loggedUser = -1;
    private transient boolean passwordHashMigrated;

    public SurpriseMe() {
        this.users = new HashMap<>();
    }

    public void updateNextId() {
        int lastUserId = 0;
        for (User user : users.values()) {
            if(user.getIdUser() > lastUserId) {
                lastUserId = user.getIdUser();
            }
        }
        User.setNextId(lastUserId + 1);

        int lastEnjoyersId = 0;
        int lastGiftsId = 0;
        int lastEventsId = 0;

        for(User user : users.values()) {
            if(user.getLastEnjoyersId() > lastEnjoyersId)
                lastEnjoyersId = user.getLastEnjoyersId();
            if(user.getLastGiftsId() > lastGiftsId)
                lastGiftsId = user.getLastGiftsId();
            if(user.getLastEventsId() > lastEventsId)
                lastEventsId = user.getLastEventsId();
        }

        Enjoyer.setNextId(lastEnjoyersId + 1);
        Gift.setNextId(lastGiftsId + 1);
        Event.setNextId(lastEventsId + 1);
    }

    /* --- METHODS FOR USER --- */

    public boolean login(String email, String password) {
        passwordHashMigrated = false;
        if (loggedUser == -1 && email != null && password != null) {
            for (User user : users.values()) {
                String storedHash = user.getPasswordHash();
                if (user.getEmail() != null
                        && user.getEmail().equalsIgnoreCase(email.trim())
                        && PasswordHasher.verify(password, storedHash)) {
                    if (PasswordHasher.needsRehash(storedHash)) {
                        user.setPasswordHash(PasswordHasher.hash(password));
                        passwordHashMigrated = true;
                    }
                    loggedUser = user.getIdUser();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean consumePasswordHashMigration() {
        boolean migrated = passwordHashMigrated;
        passwordHashMigrated = false;
        return migrated;
    }

    public boolean register(String name, String email, String password) {
        if (loggedUser == -1 && name != null && !name.isBlank() && email != null && !email.isBlank()
                && isAcceptablePassword(password)) {
            if (emailTakenByAnotherUser(email, -1)) {
                return false;
            }
            User newUser = new User(name.trim(), email.trim(), PasswordHasher.hash(password));
            users.put(newUser.getIdUser(), newUser);
            loggedUser = newUser.getIdUser();
            return true;
        }
        return false;
    }

    public void logout() {
        loggedUser = -1;
    }

    public UserDetails getUserDetails() {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getUserDetails();
            }
        }
        return null;
    }

    public boolean setUserDetails(UserDetails userDetails) {
        if (loggedUser > 0 && userDetails != null) {
            User user = users.get(loggedUser);
            if (user == null) {
                return false;
            }
            String email = userDetails.getEmail();
            if (email == null || email.isBlank() || emailTakenByAnotherUser(email, loggedUser)) {
                return false;
            }
            userDetails.setEmail(email.trim());
            return user.setUserDetails(userDetails);
        }
        return false;
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        loggedUser = -1;
        passwordHashMigrated = false;
    }

    private static boolean isAcceptablePassword(String password) {
        return password != null && !password.isBlank() && password.length() >= MIN_PASSWORD_LENGTH;
    }

    private boolean emailTakenByAnotherUser(String email, int currentUserId) {
        if (email == null) {
            return true;
        }
        String normalized = email.trim();
        if (normalized.isEmpty()) {
            return true;
        }
        for (User other : users.values()) {
            if (other.getIdUser() != currentUserId
                    && other.getEmail() != null
                    && other.getEmail().equalsIgnoreCase(normalized)) {
                return true;
            }
        }
        return false;
    }

    /* --- METHODS FOR USER ENJOYERS --- */

    public boolean addEnjoyer(EnjoyerDetails details) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.addEnjoyer(details);
            }
        }
        return false;
    }

    public HashMap<Integer, EnjoyerDetails> getEnjoyers() {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getEnjoyers();
            }
        }
        return null;
    }

    public int getSurprisedEnjoyers() {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getSurprisedEnjoyers();
            }
        }
        return 0;
    }

    public boolean editEnjoyer(int idEnjoyer, EnjoyerDetails details) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.editEnjoyer(idEnjoyer, details);
            }
        }
        return false;
    }

    public boolean removeEnjoyer(int idEnjoyer) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.removeEnjoyer(idEnjoyer);
            }
        }
        return false;
    }

    /* --- METHODS FOR USER GIFTS --- */

    public boolean addGift(int enjoyerId, String name, Type type, Occasion occasion) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.addGift(enjoyerId, name, type, occasion);
            }
        }
        return false;
    }

    public boolean editGift(int idGift, Feedback feedback, Status status) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.editGift(idGift, feedback, status);
            }
        }
        return false;
    }

    public boolean setGiftMessage(int giftId, String giftMessage) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.setGiftMessage(giftId, giftMessage);
            }
        }
        return false;
    }

    public ArrayList<Gift> getGifts() {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getGifts();
            }
        }
        return null;
    }

    public ArrayList<Gift> getGiftsForEnjoyer(int enjoyerId) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getGiftsForEnjoyer(enjoyerId);
            }
        }
        return null;
    }

    /* --- METHODS FOR USER EVENTS --- */

    public boolean addEvent(String name, LocalDate date, Occasion occasion, int enjoyerId) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.addEvent(name, date, occasion, enjoyerId);
            }
        }
        return false;
    }

    public boolean editEvent(int eventId, String name, LocalDate date, Occasion occasion, int enjoyerId) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.editEvent(eventId, name, date, occasion, enjoyerId);
            }
        }
        return false;
    }

    public boolean removeEvent(int eventId) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.removeEvent(eventId);
            }
        }
        return false;
    }

    public List<Integer> getMonthlyEventsIds(LocalDate monthFirstDay) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getMonthlyEventsIds(monthFirstDay);
            }
        }
        return new ArrayList<>();
    }

    public List<Event> getMonthlyEvents(LocalDate monthFirstDay) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getMonthlyEvents(monthFirstDay);
            }
        }
        return new ArrayList<>();
    }

    public String getEventNameById(int idEvent) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getEventNameById(idEvent);
            }
        }
        return null;
    }

    public LocalDate getEventDateById(int idEvent) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getEventDateById(idEvent);
            }
        }
        return null;
    }

    public Occasion getEventOccasionById(int idEvent) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getEventOccasionById(idEvent);
            }
        }
        return null;
    }

    public String getEventEnjoyerNameById(int idEvent) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getEventEnjoyerNameById(idEvent);
            }
        }
        return null;
    }

    public boolean hasEvent(Occasion occasion, LocalDate date, Integer enjoyerId) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.hasEvent(occasion, date, enjoyerId);
            }
        }
        return false;
    }

    public boolean hasDeletedEvent(Occasion occasion, LocalDate date, Integer enjoyerId) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.hasDeletedEvent(occasion, date, enjoyerId);
            }
        }
        return false;
    }

    public List<Integer> getEventIdsByOccasion(Occasion occasion) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getEventIdsByOccasion(occasion);
            }
        }
        return new ArrayList<>();
    }

}
