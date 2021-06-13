package ru.rdude.rpg.game.logic.holders;

import com.fasterxml.jackson.annotation.*;
import ru.rdude.rpg.game.logic.entities.Entity;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Slot<T extends Entity> {

    // need to drag and drop knows from which slot entity dragged
    private static Map<? super Entity, Slot<? extends Entity>> entitiesInSlots = new HashMap<>();

    @JsonIgnore
    private Set<SlotObserver> subscribers;

    protected T entity;

    @JsonIgnore
    protected Set<Predicate<T>> extraRequirements;

    protected String marker;

    @JsonCreator
    private Slot(@JsonProperty("marker") String marker) {
        subscribers = new HashSet<>();
        this.marker = marker;
        this.extraRequirements = new HashSet<>();
    }

    public Slot(String marker, Predicate<T>... extraRequirements) {
        subscribers = new HashSet<>();
        this.marker = marker;
        this.extraRequirements = Arrays.stream(extraRequirements).collect(Collectors.toSet());
    }

    public static <E extends Entity> Slot<E> withEntity(E entity) {
        return (Slot<E>) entitiesInSlots.get(entity);
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T item) {
        this.entity = item;
        if (item != null) {
            entitiesInSlots.put(item, this);
        }
        notifySubscribers(item);
    }

    public void removeEntity() {
        setEntity(null);
    }

    @JsonSetter
    private void setEntityJson(T item) {
        this.entity = item;
        entitiesInSlots.put(item, this);
    }

    public boolean hasEntity(T item) {
        return this.entity != null && this.entity.equals(item);
    }

    public boolean isEmpty() {
        return entity == null;
    }

    public boolean swapEntities(Slot<T> anotherSlot) {
        if (!anotherSlot.isEntityMatchRequirements(this.entity) || !isEntityMatchRequirements(anotherSlot.entity)) {
            return false;
        }
        T thisEntity = this.entity;
        setEntity(anotherSlot.entity);
        anotherSlot.setEntity(thisEntity);
        return true;
    }

    public boolean isEntityMatchRequirements(T t) {
        return t == null || extraRequirements.stream().allMatch(p -> p.test(t));
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public void addRequirement(Predicate<T> requirement) {
        this.extraRequirements.add(requirement);
    }

    public void subscribe(SlotObserver subscriber) {
        this.subscribers.add(subscriber);
    }

    public void unsubscribe(SlotObserver subscriber) {
        this.subscribers.remove(subscriber);
    }

    private void notifySubscribers(T entity) {
        subscribers.forEach(subscriber -> subscriber.update(this, entity));
    }
}
