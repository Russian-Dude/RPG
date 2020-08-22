package ru.rdude.rpg.game.logic.time;

import java.util.Set;

public class Duration implements TimeChangeObserver, TurnChangeObserver {

    public enum DurationType  {MINUTES, TURNS}

    private Set<DurationObserver> observers;

    protected Double turns;
    protected Double minutes;


    public Duration(TimeManager timeManager, DurationType type, Double value) {
        timeManager.subscribe(this);
        if (type == DurationType.MINUTES) minutes = value;
        else if (type == DurationType.TURNS) turns = value;
    }

    public Duration(TimeManager timeManager, Double minutes, Double turns) {
        timeManager.subscribe(this);
        this.minutes = minutes;
        this.turns = turns;
    }

    @Override
    public void timeUpdate(int minutes) {
        if (this.minutes != null) {
            this.minutes -= minutes;
            checkDurationEnds();
        }
    }

    @Override
    public void turnUpdate() {
        if (this.turns != null) {
            this.turns--;
            checkDurationEnds();
        }
    }

    protected void checkDurationEnds() {
        boolean ends = (turns == null || turns < 0) && (minutes == null || minutes < 0);
        notifySubscribers(ends);
    }

    public void subscribe(DurationObserver observer) { observers.add(observer); }
    public void unsubscribe(DurationObserver observer) { observers.remove(observer); }

    protected void notifySubscribers(boolean ends) {
        observers.forEach(obs -> obs.update(this, ends));
    }

}
