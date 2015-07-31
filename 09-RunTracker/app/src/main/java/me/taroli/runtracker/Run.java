package me.taroli.runtracker;

import java.util.Date;

/**
 * Created by Matt on 1/08/15.
 */
public class Run {

    private Date startDate;

    public Run() {
        startDate = new Date();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getDurationSeconds(long endMillis) {
        return (int) ((endMillis - startDate.getTime()) / 1000);
    }

    public static String formatDuration(int durationSeconds) {
        int seconds = durationSeconds % 60;
        int minutes = ((durationSeconds - seconds) / 60) % 60;
        int hours = ((durationSeconds - (minutes * 60) - seconds) / 3600);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
