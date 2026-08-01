package pt.isec.gps2526_g42.surprise_me.model.data;

import pt.isec.gps2526_g42.surprise_me.model.Feedback;
import pt.isec.gps2526_g42.surprise_me.model.Occasion;
import pt.isec.gps2526_g42.surprise_me.model.Status;
import pt.isec.gps2526_g42.surprise_me.model.Type;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 300L;

    private static int nextId = 1;

    private final int idUser;
    private final UserDetails userDetails;
    private final HashSet<Enjoyer> enjoyers;
    private final ArrayList<Gift> gifts;
    private final ArrayList<Event> events;

    public User(String name, String email, String password) {
        this.idUser = nextId++;
        this.enjoyers = new HashSet<>();
        this.gifts = new ArrayList<>();
        this.events = new ArrayList<>();
        this.userDetails = new UserDetails(name, email, password);
    }

    static void setNextId(int newId) {
        nextId = newId;
    }

    int getLastEnjoyersId() {
        int lastEnjoyersId = 0;
        for (Enjoyer enjoyer : enjoyers) {
            if (enjoyer.getIdEnjoyer() > lastEnjoyersId) {
                lastEnjoyersId = enjoyer.getIdEnjoyer();
            }
        }
        return lastEnjoyersId;
    }

    int getLastGiftsId() {
        int lastGiftsId = 0;
        for (Gift gift : gifts) {
            if (gift.getIdGift() > lastGiftsId) {
                lastGiftsId = gift.getIdGift();
            }
        }
        return lastGiftsId;
    }

    int getLastEventsId() {
        int lastEventsId = 0;
        for (Event event : events) {
            if (event.getIdEvent() > lastEventsId) {
                lastEventsId = event.getIdEvent();
            }
        }
        return lastEventsId;
    }

    int getIdUser() {
        return idUser;
    }

    String getEmail() {
        return userDetails.getEmail();
    }

    String getPassword() {
        return userDetails.getPassword();
    }

    UserDetails getUserDetails() {
        return userDetails.getUserDetails();
    }

    boolean setUserDetails(UserDetails userDetails) {
        if (userDetails == null) {
            return false;
        }
        this.userDetails.setUserDetails(userDetails);
        return true;
    }

    /* --- METHODS FOR ENJOYERS --- */

    boolean addEnjoyer(EnjoyerDetails details) {
        if (details == null) {
            return false;
        }
        Enjoyer newEnjoyer = new Enjoyer(details);
        return enjoyers.add(newEnjoyer);
    }

    boolean editEnjoyer(int idEnjoyer, EnjoyerDetails details) {
        if (details == null) {
            return false;
        }
        for (Enjoyer enjoyer : enjoyers) {
            if (enjoyer.getIdEnjoyer() == idEnjoyer) {
                enjoyer.editEnjoyer(details);
                return true;
            }
        }
        return false;
    }

    HashMap<Integer, EnjoyerDetails> getEnjoyers() {
        HashMap<Integer, EnjoyerDetails> map = new HashMap<>();
        for (Enjoyer enjoyer : enjoyers) {
            map.put(enjoyer.getIdEnjoyer(), enjoyer.getDetails());
        }
        return map;
    }

    int getSurprisedEnjoyers() {
        int count = 0;
        for (Enjoyer enjoyer : enjoyers) {
            if (!enjoyer.getGifts().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    boolean removeEnjoyer(int idEnjoyer) {
        Enjoyer toRemove = null;
        for (Enjoyer enjoyer : enjoyers) {
            if (enjoyer.getIdEnjoyer() == idEnjoyer) {
                toRemove = enjoyer;
                break;
            }
        }
        if (toRemove != null) {
            // Remove all events associated with this enjoyer
            for (Event event : new ArrayList<>(events)) {
                if (!event.isDeleted() && event.getEnjoyer() != null && event.getEnjoyer().getIdEnjoyer() == idEnjoyer) {
                    event.removeEnjoyer();
                    events.remove(event);
                }
            }
            // Remove all gifts associated with this enjoyer
            for (Gift gift : new ArrayList<>(gifts)) {
                if (gift.getEnjoyer() != null && gift.getEnjoyer().getIdEnjoyer() == idEnjoyer) {
                    gifts.remove(gift);
                }
            }
            enjoyers.remove(toRemove);
            return true;
        }
        return false;
    }

    Enjoyer getEnjoyerById(int idEnjoyer) {
        for (Enjoyer enjoyer : enjoyers) {
            if (enjoyer.getIdEnjoyer() == idEnjoyer) {
                return enjoyer;
            }
        }
        return null;
    }

    /* --- METHODS FOR GIFTS --- */

    boolean addGift(int enjoyerId, String name, Type type, Occasion occasion) {
        if (enjoyerId <= 0) {
            return gifts.add(new Gift(name, type, occasion, null));
        }
        for (Enjoyer enjoyer : enjoyers) {
            if (enjoyer.getIdEnjoyer() == enjoyerId) {
                return gifts.add(new Gift(name, type, occasion, enjoyer));
            }
        }
        return false;
    }

    boolean editGift(int giftId, Feedback feedback, Status status) {
        for (Gift gift : gifts) {
            if (gift.getIdGift() == giftId) {
                gift.editGift(feedback, status);
                return true;
            }
        }
        return false;
    }

    boolean setGiftMessage(int giftId, String giftMessage) {
        for (Gift gift : gifts) {
            if (gift.getIdGift() == giftId) {
                gift.setGiftMessage(giftMessage);
                return true;
            }
        }
        return false;
    }

    ArrayList<Gift> getGifts() {
        return new ArrayList<>(gifts);
    }

    ArrayList<Gift> getGiftsForEnjoyer(int enjoyerId) {
        for (Enjoyer enjoyer : enjoyers) {
            if (enjoyer.getIdEnjoyer() == enjoyerId) {
                return enjoyer.getGifts();
            }
        }
        return new ArrayList<>();
    }

    /* --- METHODS FOR EVENTS --- */

    boolean addEvent(String name, LocalDate date, Occasion occasion, int enjoyerId) {
        Enjoyer enjoyer = null;
        if (enjoyerId > 0) {
            enjoyer = getEnjoyerById(enjoyerId);
            if (enjoyer == null) {
                return false;
            }
        }
        Event event = new Event(name, date, occasion, enjoyer);
        return events.add(event);
    }

    boolean editEvent(int eventId, String name, LocalDate date, Occasion occasion, int enjoyerId) {
        Enjoyer enjoyer = null;
        if (enjoyerId > 0) {
            enjoyer = getEnjoyerById(enjoyerId);
            if (enjoyer == null) {
                return false;
            }
        }
        for (Event event : events) {
            if (!event.isDeleted() && event.getIdEvent() == eventId) {
                event.editEvent(name, date, occasion, enjoyer);
                return true;
            }
        }
        return false;
    }

    boolean removeEvent(int eventId) {
        for (Event event : events) {
            if (!event.isDeleted() && event.getIdEvent() == eventId) {
                event.removeEnjoyer();
                event.markDeleted();
                return true;
            }
        }
        return false;
    }

    List<Integer> getMonthlyEventsIds(LocalDate monthFirstDay) {
        ArrayList<Integer> ids = new ArrayList<>();
        int year = monthFirstDay.getYear();
        int month = monthFirstDay.getMonthValue();

        for (Event event : events) {
            if (event.isDeleted()) {
                continue;
            }
            LocalDate eventDate = event.getDate();
            if (eventDate != null && eventDate.getYear() == year && eventDate.getMonthValue() == month) {
                ids.add(event.getIdEvent());
            }
        }
        return ids;
    }

    // Returns monthly events sorted by Date (most recent first)
    List<Event> getMonthlyEvents(LocalDate monthFirstDay) {
        ArrayList<Event> monthlyEvents = new ArrayList<>();
        int year = monthFirstDay.getYear();
        int month = monthFirstDay.getMonthValue();
        for (Event event : events) {
            if (event.isDeleted()) {
                continue;
            }
            LocalDate eventDate = event.getDate();
            if (eventDate != null && eventDate.getYear() == year && eventDate.getMonthValue() == month) {
                monthlyEvents.add(event);
            }
        }
        monthlyEvents.sort(Comparator.comparing(Event::getDate));
        return monthlyEvents;
    }

    String getEventNameById(int idEvent) {
        for (Event event : events) {
            if (!event.isDeleted() && event.getIdEvent() == idEvent) {
                return event.getName();
            }
        }
        return null;
    }

    LocalDate getEventDateById(int idEvent) {
        for (Event event : events) {
            if (!event.isDeleted() && event.getIdEvent() == idEvent) {
                return event.getDate();
            }
        }
        return null;
    }

    Occasion getEventOccasionById(int idEvent) {
        for (Event event : events) {
            if (!event.isDeleted() && event.getIdEvent() == idEvent) {
                return event.getOccasion();
            }
        }
        return null;
    }

    String getEventEnjoyerNameById(int idEvent) {
        for (Event event : events) {
            if (!event.isDeleted() && event.getIdEvent() == idEvent) {
                return event.getEnjoyerName();
            }
        }
        return null;
    }

    /* --- METHODS FOR CRON --- */

    boolean hasEvent(Occasion occasion, LocalDate date, Integer enjoyerId) {
        for (Event event : events) {
            if (event.isDeleted() || event.getOccasion() != occasion) {
                continue;
            }
            if (date != null && !date.equals(event.getDate())) {
                continue;
            }
            if (enjoyerId == null) {
                return true;
            }
            if (enjoyerId == -1) {
                if (event.getEnjoyer() == null) {
                    return true;
                }
            } else {
                if (event.getEnjoyer() != null && event.getEnjoyer().getIdEnjoyer() == enjoyerId) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean hasDeletedEvent(Occasion occasion, LocalDate date, Integer enjoyerId) {
        for (Event event : events) {
            if (!event.isDeleted() || event.getOccasion() != occasion) {
                continue;
            }
            if (date != null && !date.equals(event.getDate())) {
                continue;
            }
            if (enjoyerId == null) {
                return true;
            }
            if (enjoyerId == -1) {
                if (event.getEnjoyer() == null) {
                    return true;
                }
            } else {
                if (event.getEnjoyer() != null && event.getEnjoyer().getIdEnjoyer() == enjoyerId) {
                    return true;
                }
            }
        }
        return false;
    }

    List<Integer> getEventIdsByOccasion(Occasion occasion) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (Event event : events) {
            if (!event.isDeleted() && event.getOccasion() == occasion) {
                ids.add(event.getIdEvent());
            }
        }
        return ids;
    }
}
