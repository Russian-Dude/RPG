package ru.rdude.rpg.game.utils;

import com.fasterxml.jackson.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public class SubscribersManager<T> {

    private final Set<T> subscribers;
    @JsonIgnore
    private Set<T> readyToUnsubscribe;
    @JsonIgnore
    private Set<T> readyToSubscribe;
    @JsonIgnore
    private boolean notifying = false;

    public SubscribersManager(Set<T> subscribers) {
        this.subscribers = subscribers;
    }

    public SubscribersManager() {
        this(new HashSet<>());
    }

    @JsonProperty("subscribers")
    private Set<T> getSubscribersForJson() {
        return subscribers.stream()
                .filter(sub -> sub.getClass().getAnnotation(JsonIgnoreType.class) == null)
                .collect(Collectors.toSet());
    }

    public void notifySubscribers(Consumer<T> consumer) {
        notifying = true;
        subscribers.forEach(consumer);
        notifying = false;
        if (readyToUnsubscribe != null) {
            readyToUnsubscribe.forEach(this::unsubscribe);
        }
        if (readyToSubscribe != null) {
            readyToSubscribe.forEach(this::subscribe);
        }
        readyToUnsubscribe = null;
        readyToSubscribe = null;
    }

    public void subscribe(T t) {
        if (notifying) {
            if (readyToSubscribe == null) {
                readyToSubscribe = new HashSet<>();
            }
            readyToSubscribe.add(t);
        }
        else {
            subscribers.add(t);
        }
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

    public Set<T> getSubscribers() {
        return subscribers;
    }
}
