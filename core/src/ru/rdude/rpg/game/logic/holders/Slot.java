package ru.rdude.rpg.game.logic.holders;

import com.fasterxml.jackson.annotation.*;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.utils.SubscribersManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public abstract class Slot<T extends Entity<?>> {

    private SubscribersManager<SlotObserver> subscribers;

    protected T entity;

    @JsonIgnore
    protected Set<? extends Predicate<T>> extraRequirements;

    protected String marker;

    public Slot(String marker, Predicate<T>... extraRequirements) {
        subscribers = new SubscribersManager<>();
        this.marker = marker;
        this.extraRequirements = Arrays.stream(extraRequirements).collect(Collectors.toSet());
    }

    protected Slot(Set<? extends Predicate<T>> extraRequirements) {
        this.extraRequirements = extraRequirements;
    }

    public static <E extends Entity<?>> Slot<E> withEntity(E entity) {
        Slot<E> slot = (Slot<E>) Game.getStaticReferencesHolders().entitiesInSlots().get(entity);
        if (slot == null || slot.entity == null) {
            Game.getStaticReferencesHolders().entitiesInSlots().remove(entity);
            slot = null;
        }
        return slot;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T newEntity) {
        T oldEntity = this.entity;
        this.entity = newEntity;
        if (newEntity != null) {
            Game.getStaticReferencesHolders().entitiesInSlots().put(newEntity, this);
        }
        subscribers.notifySubscribers(subscriber -> subscriber.update(this, oldEntity, newEntity));
    }

    public void removeEntity() {
        setEntity(null);
    }

    @JsonSetter("entity")
    private void setEntityJson(T item) {
        this.entity = item;
        Game.getStaticReferencesHolders().entitiesInSlots().put(item, this);
    }

    public boolean hasEntity(T item) {
        return Game.getSameEntityChecker().check(this.entity, item);
    }

    public boolean hasEntity(long guid) {
        return Game.getSameEntityChecker().check(this.entity, guid);
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

    public void subscribe(SlotObserver subscriber) {
        this.subscribers.subscribe(subscriber);
    }

    public void unsubscribe(SlotObserver subscriber) {
        this.subscribers.unsubscribe(subscriber);
    }

    public SubscribersManager<SlotObserver> getSubscribers() {
        return subscribers;
    }
}
