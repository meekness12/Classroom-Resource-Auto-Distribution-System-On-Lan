package com.classroom.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class BackupScheduler {

    private Timer timer;

    public BackupScheduler() {
        timer = new Timer();
    }

    // Schedules weekly backup based on admin's choice
    public void scheduleWeeklyBackup(int dayOfWeek, int hour, int minute, Runnable backupTask) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If the time is already passed for this week, schedule next week
        if (calendar.getTime().before(new Date())) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }

        Date firstRun = calendar.getTime();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                backupTask.run();
            }
        }, firstRun, 7 * 24 * 60 * 60 * 1000L); // repeat every week

        System.out.println("Backup scheduled for: " + firstRun);
    }

    public void stopScheduler() {
        timer.cancel();
    }
}
