package ru.rdude.rpg.game.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;

/**
 * Starts counting when created. Use get count or to String to get time from creation in seconds with description.
 * */
public class TimeCounter {

    private String description;
    private LocalDateTime start;
    private LocalDateTime previous;

    public TimeCounter(String description) {
        this.description = description != null ? description : "";
        start = LocalDateTime.now();
        previous = start;
    }

    public String getCount() {
        LocalDateTime thisMoment = LocalDateTime.now();
        Duration duration = Duration.between(start, thisMoment);
        previous = thisMoment;
        return description + ": " + duration.toMillis() + " ms (" + duration.getSeconds() + " seconds)";
    }

    public String getCount(String momentDescription) {
        LocalDateTime thisMoment = LocalDateTime.now();
        Duration duration = Duration.between(start, thisMoment);
        previous = thisMoment;
        return this.description + " (" + momentDescription + "): " + duration.toMillis() + " ms (" + duration.getSeconds() + " seconds)";
    }

    public String getCountFromPrevious(String momentDescription) {
        LocalDateTime thisMoment = LocalDateTime.now();
        Duration duration = Duration.between(previous, thisMoment);
        previous = thisMoment;
        return this.description + " (" + momentDescription + "): " + duration.toMillis() + " ms (" + duration.getSeconds() + " seconds)";
    }

    @Override
    public String toString() {
        return getCount();
    }
}
