package ru.rdude.rpg.game.logic.holders;

import ru.rdude.rpg.game.logic.entities.Entity;

import java.util.HashSet;
import java.util.Set;

public class Slot<T extends Entity> {

    private Set<SlotObserver> subscribers;

    protected T entity;

    protected Class<? extends T> requiredClass;

    public Slot(Class<? extends T> requiredClass) {
        this.requiredClass = requiredClass;
        subscribers = new HashSet<>();
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T item) {
        this.entity = item;
        notifySubscribers(item);
    }

    public boolean hasEntity(T item) {
        return this.entity != null && this.entity.equals(item);
    }

    public boolean isEmpty() {
        return entity == null;
    }

    public boolean swapEntities(Slot<T> anotherSlot) {
        if (!requiredClass.isAssignableFrom(anotherSlot.entity.getClass()))
            return false;
        T thisEntity = this.entity;
        setEntity(anotherSlot.entity);
        anotherSlot.setEntity(thisEntity);
        return true;
    }

    public Class<? extends T> getRequiredClass() {
        return requiredClass;
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
