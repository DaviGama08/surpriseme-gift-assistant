package pt.isec.gps2526_g42.surprise_me.model.data;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class CronDateUtils {

    private static final CronDefinition CRON_DEFINITION = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);

    private static final CronParser PARSER = new CronParser(CRON_DEFINITION);

    private CronDateUtils() {
    }

    public static LocalDate nextDate(String cronExpression, LocalDate fromDate) {
        if (cronExpression == null || fromDate == null) {
            return null;
        }
        try {
            Cron cron = PARSER.parse(cronExpression);
            ExecutionTime executionTime = ExecutionTime.forCron(cron);
            ZonedDateTime fromZdt = fromDate.atStartOfDay(ZoneId.systemDefault());

            ZonedDateTime next = executionTime.nextExecution(fromZdt).orElse(null);
            if (next == null) {
                return null;
            }
            return next.toLocalDate();
        } catch (RuntimeException e) {
            return null;
        }
    }
}
