package pt.isec.gps2526_g42.surprise_me.model.data;

import pt.isec.gps2526_g42.surprise_me.model.Feedback;
import pt.isec.gps2526_g42.surprise_me.model.Occasion;
import pt.isec.gps2526_g42.surprise_me.model.Status;
import pt.isec.gps2526_g42.surprise_me.model.Type;
import pt.isec.gps2526_g42.surprise_me.model.llm.GiftCriteria;
import pt.isec.gps2526_g42.surprise_me.model.llm.LlmApi;
import pt.isec.gps2526_g42.surprise_me.model.llm.LlmClient;
import pt.isec.gps2526_g42.surprise_me.model.llm.PromptBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SurpriseMeManager {
    private SurpriseMe surpriseMe;
    private final LlmClient llmClient;
    private final PromptBuilder promptBuilder;

    public SurpriseMeManager() {
        this(new LlmApi());
    }

    public SurpriseMeManager(LlmClient llmClient) {
        load();
        this.llmClient = java.util.Objects.requireNonNull(llmClient);
        promptBuilder = new PromptBuilder();
    }

    /* --- METHODS FOR DATA PERSISTENCE --- */

    public void load() {
        surpriseMe = SurpriseMeSerialization.load();
        surpriseMe.updateNextId();
    }

    public void save() {
        SurpriseMeSerialization.save(surpriseMe);
    }

    /* --- METHODS FOR USER --- */

    public boolean login(String email, String password) {
        boolean success = surpriseMe.login(email, password);
        if (success && surpriseMe.consumePasswordHashMigration()) {
            save();
        }
        return success;
    }

    public boolean register(String name, String email, String password) {
        return persistIfChanged(surpriseMe.register(name, email, password));
    }

    public UserDetails getUserDetails() {
        return surpriseMe.getUserDetails();
    }

    public boolean setUserDetails(UserDetails userDetails) {
        return persistIfChanged(surpriseMe.setUserDetails(userDetails));
    }

    public void logout() {
        surpriseMe.logout();
        save();
    }

    /* --- METHODS FOR USER ENJOYERS --- */

    public boolean addEnjoyer(EnjoyerDetails details) {
        return persistIfChanged(surpriseMe.addEnjoyer(details));
    }

    public HashMap<Integer, EnjoyerDetails> getEnjoyers() {
        return surpriseMe.getEnjoyers();
    }

    public int getSurprisedEnjoyers() {
        return surpriseMe.getSurprisedEnjoyers();
    }

    public boolean editEnjoyer(int idEnjoyer, EnjoyerDetails details) {
        return persistIfChanged(surpriseMe.editEnjoyer(idEnjoyer, details));
    }

    public boolean removeEnjoyer(int idEnjoyer) {
        return persistIfChanged(surpriseMe.removeEnjoyer(idEnjoyer));
    }

    /* --- METHODS FOR USER GIFTS --- */

    public boolean addGift(String name, Type type, Occasion occasion, int enjoyerId) {
        // Only adds the gift now; no automatic event binding based on occasion.
        return persistIfChanged(surpriseMe.addGift(enjoyerId, name, type, occasion));
    }

    public boolean addGift(String name, Type type, Occasion occasion) {
        return addGift(name, type, occasion, -1);
    }

    public boolean editGift(int idGift, Feedback feedback, Status status) {
        return persistIfChanged(surpriseMe.editGift(idGift, feedback, status));
    }

    public boolean setGiftMessage(int giftId, String giftMessage) {
        return persistIfChanged(surpriseMe.setGiftMessage(giftId, giftMessage));
    }

    public ArrayList<Gift> getGifts() {
        return surpriseMe.getGifts();
    }

    public ArrayList<Gift> getGiftsForEnjoyer(int enjoyerId) {
        return surpriseMe.getGiftsForEnjoyer(enjoyerId);
    }

    public String generateGiftSuggestions(EnjoyerDetails enjoyerDetails, GiftCriteria giftCriteria, boolean consentGranted) {
        if (!consentGranted) {
            return "Consent is required before recipient data is sent to the external LLM provider.";
        }
        try {
            String prompt = promptBuilder.buildGiftSuggestionPrompt(enjoyerDetails, giftCriteria);
            return llmClient.generateGiftSuggestions(prompt);
        } catch (Exception e) {
            return "The suggestion service is currently unavailable.";
        }
    }

    public String generateSpontaneousGifts(String enjoyerDescription, GiftCriteria giftCriteria, boolean consentGranted) {
        return generateSpontaneousGifts(enjoyerDescription, giftCriteria, null, null, consentGranted);
    }

    public String generateSpontaneousGifts(String enjoyerDescription, GiftCriteria giftCriteria, String userCity,
                                           String userCountry, boolean consentGranted) {
        if (!consentGranted) {
            return "Consent is required before recipient data is sent to the external LLM provider.";
        }
        try {
            String prompt = promptBuilder.buildSpontaneousPrompt(enjoyerDescription, giftCriteria, userCity, userCountry);
            return llmClient.generateGiftSuggestions(prompt);
        } catch (Exception e) {
            return "The suggestion service is currently unavailable.";
        }
    }

    public String generateGiftMessage(String giftTitle, String giftDescription, String recipientName,
                                      String relationship, String occasion, boolean consentGranted) {
        if (!consentGranted) {
            return "Consent is required before recipient data is sent to the external LLM provider.";
        }
        try {
            String prompt = promptBuilder.buildGiftMessagePrompt(
                    giftTitle, giftDescription, recipientName, relationship, occasion);
            return llmClient.generateGiftSuggestions(prompt);
        } catch (Exception e) {
            return "The suggestion service is currently unavailable.";
        }
    }

    public String generateSpontaneousGiftMessage(String giftTitle, String giftDescription, String occasion,
                                                 boolean consentGranted) {
        if (!consentGranted) {
            return "Consent is required before recipient data is sent to the external LLM provider.";
        }
        try {
            String prompt = promptBuilder.buildSpontaneousGiftMessagePrompt(
                    giftTitle, giftDescription, occasion);
            return llmClient.generateGiftSuggestions(prompt);
        } catch (Exception e) {
            return "The suggestion service is currently unavailable.";
        }
    }

    /* --- METHODS FOR USER EVENTS --- */

    public boolean addEvent(String name, LocalDate date, Occasion occasion, int enjoyerId) {
        return persistIfChanged(surpriseMe.addEvent(name, date, occasion, enjoyerId));
    }

    public boolean editEvent(int eventId, String name, LocalDate date, Occasion occasion, int enjoyerId) {
        return persistIfChanged(surpriseMe.editEvent(eventId, name, date, occasion, enjoyerId));
    }

    public boolean removeEvent(int eventId) {
        return persistIfChanged(surpriseMe.removeEvent(eventId));
    }

    public List<Integer> getMonthlyEventsIds(LocalDate monthFirstDay) {
        EventCronManager.ensureRecurringEventsForMonth(this, monthFirstDay);
        return surpriseMe.getMonthlyEventsIds(monthFirstDay);
    }

    public List<Event> getMonthlyEvents(LocalDate monthFirstDay) {
        EventCronManager.ensureRecurringEventsForMonth(this, monthFirstDay);
        return surpriseMe.getMonthlyEvents(monthFirstDay);
    }

    public String getEventNameById(int idEvent) {
        return surpriseMe.getEventNameById(idEvent);
    }

    public LocalDate getEventDateById(int idEvent) {
        return surpriseMe.getEventDateById(idEvent);
    }

    public Occasion getEventOccasionById(int idEvent) {
        return surpriseMe.getEventOccasionById(idEvent);
    }

    public String getEventEnjoyerNameById(int idEvent) {
        return surpriseMe.getEventEnjoyerNameById(idEvent);
    }

    public boolean hasEvent(Occasion occasion, LocalDate date, Integer enjoyerId) {
        return surpriseMe.hasEvent(occasion, date, enjoyerId);
    }

    public boolean hasDeletedEvent(Occasion occasion, LocalDate date, Integer enjoyerId) {
        return surpriseMe.hasDeletedEvent(occasion, date, enjoyerId);
    }

    public List<Integer> getEventIdsByOccasion(Occasion occasion) {
        return surpriseMe.getEventIdsByOccasion(occasion);
    }

    private boolean persistIfChanged(boolean changed) {
        if (changed) {
            save();
        }
        return changed;
    }
}
