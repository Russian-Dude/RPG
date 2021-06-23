package ru.rdude.rpg.game.logic.time;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ru.rdude.rpg.game.utils.SubscribersManager;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
@JsonPolymorphicSubType("duration")
public class Duration implements TimeChangeObserver, TurnChangeObserver {

    public enum DurationType  {MINUTES, TURNS}

    private SubscribersManager<DurationObserver> observers;

    protected Double turns;
    protected Double minutes;

    protected Duration() { }

    public Duration(TimeManager timeManager, DurationType type, Double value) {
        observers = new SubscribersManager<>();
        timeManager.subscribe(this);
        if (type == DurationType.MINUTES) minutes = value;
        else if (type == DurationType.TURNS) turns = value;
    }

    public Duration(TimeManager timeManager, Double minutes, Double turns) {
        observers = new SubscribersManager<>();
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

    public void subscribe(DurationObserver observer) { observers.subscribe(observer); }
    public void unsubscribe(DurationObserver observer) { observers.unsubscribe(observer); }

    protected void notifySubscribers(boolean ends) {
        observers.notifySubscribers(obs -> obs.update(this, ends));
    }

}
