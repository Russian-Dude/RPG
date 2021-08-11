package ru.rdude.rpg.game.logic.time;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ru.rdude.rpg.game.utils.SubscribersManager;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public class TurnsManager {

    private final SubscribersManager<TurnChangeObserver> subscribers = new SubscribersManager<>();

    public void subscribe(TurnChangeObserver subscriber) {
        subscribers.subscribe(subscriber);
    }

    public void unsubscribe(TurnChangeObserver subscriber) {
        subscribers.unsubscribe(subscriber);
    }

    public void nextTurn() {
        subscribers.notifySubscribers(TurnChangeObserver::turnUpdate);
    }

}
