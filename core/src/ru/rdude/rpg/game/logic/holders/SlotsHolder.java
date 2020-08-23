package ru.rdude.rpg.game.logic.holders;

import ru.rdude.rpg.game.logic.entities.Entity;

import java.util.ArrayList;
import java.util.List;

public abstract class SlotsHolder<T extends Entity> {

    protected List<Slot<T>> slots;
    protected Class<? extends T> requiredClass;

    public SlotsHolder(int capacity, Class<T> requiredClass) {
        this.requiredClass = requiredClass;
        slots = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            slots.add(new Slot<>(requiredClass));
        }
    }

    protected SlotsHolder() {
    }

    public boolean hasEntity(T entity) {
        for (Slot<T> slot : slots) {
            if (slot.hasEntity(entity))
                return true;
        }
        return false;
    }

    public boolean hasEmptySlot() {
        return slots.stream().anyMatch(Slot::isEmpty);
    }

    public Slot<T> findEmptySlot() {
        return slots.stream().filter(Slot::isEmpty).findFirst().orElse(null);
    }

    public int size() {
        return slots.size();
    }

    public int emptySlotsAmount() {
        return (int) slots.stream().filter(Slot::isEmpty).count();
    }

    // return false if no space to receive
    public boolean receiveEntity(T entity) {
        if (!requiredClass.isAssignableFrom(entity.getClass()))
            return false;
        Slot<T> slot = findEmptySlot();
        if (slot != null) {
            slot.setEntity(entity);
            return true;
        }
        else return false;
    }

    public List<Slot<T>> getSlots() {
        return slots;
    }
}
