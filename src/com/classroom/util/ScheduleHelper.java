package com.classroom.util;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class ScheduleHelper {

    public static long computeDelay(DayOfWeek dayOfWeek, LocalTime backupTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.with(backupTime)
                                   .with(java.time.temporal.TemporalAdjusters.nextOrSame(dayOfWeek));
        return ChronoUnit.SECONDS.between(now, nextRun);
    }
}
