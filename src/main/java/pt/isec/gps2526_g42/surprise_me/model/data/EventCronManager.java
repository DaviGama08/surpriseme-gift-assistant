package pt.isec.gps2526_g42.surprise_me.model.data;

import pt.isec.gps2526_g42.surprise_me.model.Occasion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventCronManager {

    private EventCronManager() {
    }

    /**
     * Ensures that automatic birthday events exist for the requested month,
     * but only for enjoyers with a known birth date.
     * <p>
     * - Does not touch manual birthday events.
     * - Does not touch events for enjoyers without a birthDate.
     */
    public static void ensureRecurringEventsForMonth(SurpriseMeManager manager,
                                                     LocalDate monthFirstDay) {
        if (manager == null || monthFirstDay == null) {
            return;
        }

        LocalDate firstDay = monthFirstDay.withDayOfMonth(1);
        int year = firstDay.getYear();
        int month = firstDay.getMonthValue();

        ensureBirthdayEvents(manager, firstDay, year, month);
    }

    public static void onGiftAdded(SurpriseMeManager manager, int enjoyerId, Occasion occasion) {
        if (manager == null || occasion == null || enjoyerId <= 0) {
            return;
        }

        List<Integer> ids = manager.getEventIdsByOccasion(occasion);
        if (ids == null || ids.isEmpty()) {
            return;
        }

        for (Integer id : ids) {
            if (id == null) {
                continue;
            }

            String enjoyerName = manager.getEventEnjoyerNameById(id);

            boolean hasEnjoyer = enjoyerName != null && !enjoyerName.isBlank() && !"Unknown".equalsIgnoreCase(enjoyerName);

            if (!hasEnjoyer) {
                String name = manager.getEventNameById(id);
                LocalDate date = manager.getEventDateById(id);
                manager.editEvent(id, name, date, occasion, enjoyerId);
            }
        }
    }

    private static void ensureBirthdayEvents(SurpriseMeManager manager, LocalDate firstDay, int year, int month) {
        HashMap<Integer, EnjoyerDetails> enjoyers = manager.getEnjoyers();
        if (enjoyers == null || enjoyers.isEmpty()) {
            return;
        }

        // Starting point to calculate the "next" birthday
        LocalDate from = firstDay.minusDays(1);

        // Snapshot of ALL events with occasion BIRTHDAY
        List<Integer> birthdayIds = manager.getEventIdsByOccasion(Occasion.BIRTHDAY);

        for (Map.Entry<Integer, EnjoyerDetails> entry : enjoyers.entrySet()) {
            Integer enjoyerId = entry.getKey();
            EnjoyerDetails details = entry.getValue();

            if (details == null || details.getBirthDate() == null) {
                continue;
            }

            String enjoyerName = details.getName();
            LocalDate birth = details.getBirthDate();

            // *** NEW BEHAVIOR ***
            // If there is no birth date, we do not manage this automatically:
            // - We do not create events
            // - We do not delete existing events
            // This allows manual birthday events for these cases.
            if (birth == null) {
                continue;
            }

            // Calculate the next birthday (day/month of birthDate in this year)
            String cronExpr = buildYearlyDateCron(birth);
            LocalDate next = CronDateUtils.nextDate(cronExpr, from);

            // If the next birthday is not in this month/year, do nothing.
            if (next == null || next.getYear() != year || next.getMonthValue() != month) {
                continue;
            }

            // Check if there's a deleted event for this enjoyer on this date
            // If user manually deleted it, respect that decision and don't recreate
            if (manager.hasDeletedEvent(Occasion.BIRTHDAY, next, enjoyerId)) {
                // Event was manually deleted by user - respect that and don't recreate
                continue;
            }

            if (manager.hasEvent(Occasion.BIRTHDAY, next, enjoyerId)) {
                // Event already exists (active) - skip creation
                continue;
            }

            // Birthday events for this enjoyer in this month/year
            ArrayList<Integer> existingForEnjoyerInMonth = new ArrayList<>();
            for (Integer evId : birthdayIds) {
                if (evId == null) {
                    continue;
                }

                LocalDate evDate = manager.getEventDateById(evId);
                if (evDate == null || evDate.getYear() != year || evDate.getMonthValue() != month) {
                    continue;
                }

                String evEnjoyerName = manager.getEventEnjoyerNameById(evId);
                if (enjoyerName != null && !enjoyerName.isBlank()
                        && enjoyerName.equals(evEnjoyerName)) {
                    existingForEnjoyerInMonth.add(evId);
                }
            }

            // From here on we want to ensure that there is AT LEAST ONE
            // birthday event in this month for this enjoyer.
            String evName;
            if (enjoyerName != null && !enjoyerName.isBlank()) {
                evName = "Birthday - " + enjoyerName;
            } else {
                evName = "Birthday";
            }

            if (existingForEnjoyerInMonth.isEmpty()) {
                // There was no event -> create a new one
                manager.addEvent(evName, next, Occasion.BIRTHDAY, enjoyerId);
            } else {
                // At least one exists -> update the first one to match the correct date
                Integer keepId = existingForEnjoyerInMonth.getFirst();
                if (keepId != null) {
                    manager.editEvent(keepId, evName, next, Occasion.BIRTHDAY, enjoyerId);
                }
                // NOTE: we do not delete any other events; we leave "extra" events alone
            }
        }
    }

    /**
     * Builds a yearly cron expression (Quartz) for the birthday day/month:
     * sec min hour day-of-month month day-of-week year
     * <p>
     * Example: 0 0 0 15 9 ? *  -> every year on 15/09 at midnight
     */
    private static String buildYearlyDateCron(LocalDate date) {
        int day = date.getDayOfMonth();
        int month = date.getMonthValue();
        return "0 0 0 " + day + " " + month + " ? *";
    }
}
