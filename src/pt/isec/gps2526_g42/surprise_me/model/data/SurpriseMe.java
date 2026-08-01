package pt.isec.gps2526_g42.surprise_me.model.data;

import pt.isec.gps2526_g42.surprise_me.model.Feedback;
import pt.isec.gps2526_g42.surprise_me.model.Occasion;
import pt.isec.gps2526_g42.surprise_me.model.Status;
import pt.isec.gps2526_g42.surprise_me.model.Type;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SurpriseMe implements Serializable {
    @Serial
    private static final long serialVersionUID = 300L;

    private final HashMap<Integer, User> users;
    private int loggedUser = -1;

    public SurpriseMe() {
        this.users = new HashMap<>();
    }

    void updateNextId() {
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

    boolean login(String email, String password) {
        if (loggedUser == -1) {
            for (User user : users.values()) {
                if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                    loggedUser = user.getIdUser();
                    return true;
                }
            }
        }
        return false;
    }

    boolean register(String name, String email, String password) {
        if (loggedUser == -1) {
            for (User user : users.values()) {
                if (user.getEmail().equals(email)) {
                    return false;
                }
            }
            User newUser = new User(name, email, password);
            users.put(newUser.getIdUser(), newUser);
            loggedUser = newUser.getIdUser();
            return true;
        }
        return false;
    }

    void logout() {
        loggedUser = -1;
    }

    UserDetails getUserDetails() {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getUserDetails();
            }
        }
        return null;
    }

    boolean setUserDetails(UserDetails userDetails) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.setUserDetails(userDetails);
            }
        }
        return false;
    }

    /* --- METHODS FOR USER ENJOYERS --- */

    boolean addEnjoyer(EnjoyerDetails details) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.addEnjoyer(details);
            }
        }
        return false;
    }

    HashMap<Integer, EnjoyerDetails> getEnjoyers() {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getEnjoyers();
            }
        }
        return null;
    }

    int getSurprisedEnjoyers() {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getSurprisedEnjoyers();
            }
        }
        return 0;
    }

    boolean editEnjoyer(int idEnjoyer, EnjoyerDetails details) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.editEnjoyer(idEnjoyer, details);
            }
        }
        return false;
    }

    boolean removeEnjoyer(int idEnjoyer) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.removeEnjoyer(idEnjoyer);
            }
        }
        return false;
    }

    /* --- METHODS FOR USER GIFTS --- */

    boolean addGift(int enjoyerId, String name, Type type, Occasion occasion) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.addGift(enjoyerId, name, type, occasion);
            }
        }
        return false;
    }

    boolean editGift(int idGift, Feedback feedback, Status status) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.editGift(idGift, feedback, status);
            }
        }
        return false;
    }

    boolean setGiftMessage(int giftId, String giftMessage) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.setGiftMessage(giftId, giftMessage);
            }
        }
        return false;
    }

    ArrayList<Gift> getGifts() {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getGifts();
            }
        }
        return null;
    }

    ArrayList<Gift> getGiftsForEnjoyer(int enjoyerId) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getGiftsForEnjoyer(enjoyerId);
            }
        }
        return null;
    }

    /* --- METHODS FOR USER EVENTS --- */

    boolean addEvent(String name, LocalDate date, Occasion occasion, int enjoyerId) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.addEvent(name, date, occasion, enjoyerId);
            }
        }
        return false;
    }

    boolean editEvent(int eventId, String name, LocalDate date, Occasion occasion, int enjoyerId) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.editEvent(eventId, name, date, occasion, enjoyerId);
            }
        }
        return false;
    }

    boolean removeEvent(int eventId) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.removeEvent(eventId);
            }
        }
        return false;
    }

    List<Integer> getMonthlyEventsIds(LocalDate monthFirstDay) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getMonthlyEventsIds(monthFirstDay);
            }
        }
        return new ArrayList<>();
    }

    List<Event> getMonthlyEvents(LocalDate monthFirstDay) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getMonthlyEvents(monthFirstDay);
            }
        }
        return new ArrayList<>();
    }

    String getEventNameById(int idEvent) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getEventNameById(idEvent);
            }
        }
        return null;
    }

    LocalDate getEventDateById(int idEvent) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getEventDateById(idEvent);
            }
        }
        return null;
    }

    Occasion getEventOccasionById(int idEvent) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getEventOccasionById(idEvent);
            }
        }
        return null;
    }

    String getEventEnjoyerNameById(int idEvent) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getEventEnjoyerNameById(idEvent);
            }
        }
        return null;
    }

    boolean hasEvent(Occasion occasion, LocalDate date, Integer enjoyerId) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.hasEvent(occasion, date, enjoyerId);
            }
        }
        return false;
    }

    boolean hasDeletedEvent(Occasion occasion, LocalDate date, Integer enjoyerId) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.hasDeletedEvent(occasion, date, enjoyerId);
            }
        }
        return false;
    }

    List<Integer> getEventIdsByOccasion(Occasion occasion) {
        if (loggedUser > 0) {
            User user = users.get(loggedUser);
            if (user != null) {
                return user.getEventIdsByOccasion(occasion);
            }
        }
        return new ArrayList<>();
    }

}
