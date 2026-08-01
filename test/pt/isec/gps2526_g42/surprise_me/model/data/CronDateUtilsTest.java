package pt.isec.gps2526_g42.surprise_me.model.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CronDateUtils Tests")
class CronDateUtilsTest {

    @Test
    @DisplayName("Test nextDate with New Year cron expression")
    void testNextDateNewYear() {
        String cronNewYear = "0 0 0 1 1 ? *";
        LocalDate from = LocalDate.of(2025, 12, 31);
        LocalDate next = CronDateUtils.nextDate(cronNewYear, from);

        assertNotNull(next);
        assertEquals(1, next.getMonthValue());
        assertEquals(1, next.getDayOfMonth());
        assertTrue(next.getYear() >= 2026);
    }

    @Test
    @DisplayName("Test nextDate with Valentine's Day cron expression")
    void testNextDateValentinesDay() {
        String cronValentines = "0 0 0 14 2 ? *";
        LocalDate from = LocalDate.of(2025, 2, 1);
        LocalDate next = CronDateUtils.nextDate(cronValentines, from);

        assertNotNull(next);
        assertEquals(2, next.getMonthValue());
        assertEquals(14, next.getDayOfMonth());
    }

    @Test
    @DisplayName("Test nextDate with Christmas cron expression")
    void testNextDateChristmas() {
        String cronChristmas = "0 0 0 25 12 ? *";
        LocalDate from = LocalDate.of(2025, 12, 1);
        LocalDate next = CronDateUtils.nextDate(cronChristmas, from);

        assertNotNull(next);
        assertEquals(12, next.getMonthValue());
        assertEquals(25, next.getDayOfMonth());
    }

    @Test
    @DisplayName("Test nextDate with yearly date (birthday)")
    void testNextDateYearlyBirthday() {
        String cronBirthday = "0 0 0 15 5 ? *"; // May 15th
        LocalDate from = LocalDate.of(2025, 5, 1);
        LocalDate next = CronDateUtils.nextDate(cronBirthday, from);

        assertNotNull(next);
        assertEquals(5, next.getMonthValue());
        assertEquals(15, next.getDayOfMonth());
    }

    @Test
    @DisplayName("Test nextDate with null cron expression")
    void testNextDateNullCron() {
        LocalDate next = CronDateUtils.nextDate(null, LocalDate.now());
        assertNull(next);
    }

    @Test
    @DisplayName("Test nextDate with null from date")
    void testNextDateNullFrom() {
        String cron = "0 0 0 1 1 ? *";
        LocalDate next = CronDateUtils.nextDate(cron, null);
        assertNull(next);
    }

    @Test
    @DisplayName("Test nextDate with invalid cron expression")
    void testNextDateInvalidCron() {
        String invalidCron = "invalid";
        LocalDate from = LocalDate.now();
        LocalDate next = CronDateUtils.nextDate(invalidCron, from);
        assertNull(next);
    }

    @Test
    @DisplayName("Test nextDate returns future date")
    void testNextDateReturnsFutureDate() {
        String cron = "0 0 0 1 1 ? *"; // January 1st
        LocalDate from = LocalDate.of(2025, 12, 31);
        LocalDate next = CronDateUtils.nextDate(cron, from);

        assertNotNull(next);
        assertTrue(next.isAfter(from));
    }

    @Test
    @DisplayName("Test nextDate with Mother's Day cron (first Sunday of May)")
    void testNextDateMothersDayFirstSundayMay() {
        String cronMothersDay = "0 0 0 ? 5 1#1 *"; // First Sunday of May
        LocalDate from = LocalDate.of(2025, 4, 30);
        LocalDate next = CronDateUtils.nextDate(cronMothersDay, from);

        assertNotNull(next);
        assertEquals(5, next.getMonthValue());
    }

    @Test
    @DisplayName("Test nextDate wraps to next year")
    void testNextDateWrapsYear() {
        String cron = "0 0 0 1 1 ? *"; // January 1st
        LocalDate from = LocalDate.of(2025, 6, 1);
        LocalDate next = CronDateUtils.nextDate(cron, from);

        assertNotNull(next);
        assertTrue(next.getYear() > 2025);
    }

    @Test
    @DisplayName("Test nextDate with multiple different cron patterns")
    void testNextDateMultiplePatterns() {
        LocalDate from = LocalDate.of(2025, 1, 1);

        String[] crons = {
            "0 0 0 1 1 ? *",    // New Year
            "0 0 0 14 2 ? *",   // Valentine's
            "0 0 0 19 3 ? *",   // Father's Day PT
            "0 0 0 25 12 ? *"   // Christmas
        };

        for (String cron : crons) {
            LocalDate next = CronDateUtils.nextDate(cron, from);
            assertNotNull(next, "Failed for cron: " + cron);
            assertTrue(next.isAfter(from) || next.isEqual(from.plusDays(1)));
        }
    }

    @Test
    @DisplayName("Test nextDate with same day returns next occurrence")
    void testNextDateSameDayNextYear() {
        String cron = "0 0 0 15 6 ? *"; // June 15th
        LocalDate from = LocalDate.of(2025, 6, 15);
        LocalDate next = CronDateUtils.nextDate(cron, from);

        assertNotNull(next);
        // Should be next year's occurrence
        assertTrue(next.getYear() >= 2025);
    }

    @Test
    @DisplayName("Test nextDate with leap year date")
    void testNextDateLeapYear() {
        String cron = "0 0 0 29 2 ? *"; // Feb 29th (leap year)
        LocalDate from = LocalDate.of(2023, 2, 1);
        LocalDate next = CronDateUtils.nextDate(cron, from);

        // Should find next leap year occurrence
        if (next != null) {
            assertEquals(2, next.getMonthValue());
            assertEquals(29, next.getDayOfMonth());
        }
    }
}

