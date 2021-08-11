package ru.rdude.rpg.game.logic.time;

import com.fasterxml.jackson.annotation.*;
import ru.rdude.rpg.game.logic.enums.GameState;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.GameStateObserver;
import ru.rdude.rpg.game.utils.SubscribersManager;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("timeManager")
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public class TimeManager implements TurnChangeObserver {

    private final SubscribersManager<TimeObserver> timeObservers;
    private final SubscribersManager<TimeChangeObserver> timeChangeObservers;

    private int minute = 0;
    private int hour = 12;
    private int day = 1;
    private int month = 1;
    private int year = 1;

        @JsonCreator
    private TimeManager(
            @JsonProperty("timeObservers") SubscribersManager<TimeObserver> timeObservers,
            @JsonProperty("timeChangeObservers") SubscribersManager<TimeChangeObserver> timeChangeObservers) {
        this.timeObservers = timeObservers;
        this.timeChangeObservers = timeChangeObservers;
    }

    public TimeManager(TurnsManager turnsManager) {
        timeChangeObservers = new SubscribersManager<>();
        timeObservers = new SubscribersManager<>();
        turnsManager.subscribe(this);
    }

    public String stringTime() {
        return String.format("%02d:%02d of day %d, month %d, year %d",
                minute, hour, day, month, year);
    }

    public int minute() { return minute; }
    public int hour() { return hour; }
    public int day() { return day; }
    public int month() { return month; }
    public int year() { return year; }

    private void calculateTime() {
        while (minute > 59) {
            minute -= 60;
            hour +=1;
        }
        while (minute < 0) {
            minute += 60;
            hour -= 1;
        }
        while (hour > 23) {
            hour -= 24;
            day += 1;
        }
        while (hour < 0) {
            hour += 24;
            day -= 1;
        }
        while (day > 28) {
            day -= 28;
            month += 1;
        }
        while (day < 0) {
            day += 28;
            month -= 1;
        }
        while (month > 12) {
            month -= 12;
            year += 1;
        }
        while (month < 0) {
            month += 12;
            year -= 1;
        }
    }

    public void increaseTime() {
        increaseTime(1);
    }

    public void increaseTime(int minutes) {
        this.minute += minutes;
        calculateTime();
        notifySubscribers(minutes);
    }

    public void setTime(int minute, int hour, int day, int month, int year) {
        this.minute = minute;
        this.hour = hour;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public void subscribe(TimeObserver observer) { timeObservers.subscribe(observer); }
    public void subscribe(TimeChangeObserver observer) { timeChangeObservers.subscribe(observer); }

    public void unsubscribe(TimeObserver observer) { timeObservers.unsubscribe(observer); }
    public void unsubscribe(TimeChangeObserver observer) { timeChangeObservers.unsubscribe(observer); }

    private void notifySubscribers(int minutesChanges) {
        timeObservers.notifySubscribers(obs -> obs.update(this));
        timeChangeObservers.notifySubscribers(obs -> obs.timeUpdate(minutesChanges));
    }

    @Override
    public void turnUpdate() {
        increaseTime();
    }
}
