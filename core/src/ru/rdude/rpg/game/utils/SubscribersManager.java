package ru.rdude.rpg.game.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SubscribersManager<T> {

    private final Set<T> subscribers = new HashSet<>();
    private Set<T> readyToUnsubscribe;
    private boolean notifying = false;

    public void notifySubscribers(Consumer<T> consumer) {
        notifying = true;
        subscribers.forEach(consumer);
        notifying = false;
        if (readyToUnsubscribe != null) {
            readyToUnsubscribe.forEach(this::unsubscribe);
        }
        readyToUnsubscribe = null;
    }

    public void subscribe(T t) {
        subscribers.add(t);
    }

    public void unsubscribe(T t) {
        if (notifying) {
            if (readyToUnsubscribe == null) {
                readyToUnsubscribe = new HashSet<>();
            }
            readyToUnsubscribe.add(t);
        }
        else {
            subscribers.remove(t);
        }
    }

}
