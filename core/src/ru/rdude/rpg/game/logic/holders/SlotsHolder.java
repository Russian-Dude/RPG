package ru.rdude.rpg.game.logic.holders;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.entities.items.holders.SlotPredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class SlotsHolder<T extends Entity<?>> {

    @JsonProperty
    protected List<Slot<T>> slots;

    public SlotsHolder(int capacity) {
        this(capacity, null);
    }

    public SlotsHolder(int capacity, String marker, SlotPredicate<T>... extraRequirements) {
        slots = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            slots.add(new SimpleSlot<>(marker, extraRequirements));
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

    public boolean hasEntity(long guid) {
        for (Slot<T> slot : slots) {
            if (slot.hasEntity(guid)) {
                return true;
            }
        }
        return false;
    }

    public boolean removeEntity(T entity) {
        for (Slot<T> slot : slots) {
            if (slot.hasEntity(entity)) {
                slot.removeEntity();
                return true;
            }
        }
        return false;
    }

    public boolean removeEntity(long guid) {
        for (Slot<T> slot : slots) {
            if (slot.hasEntity(guid)) {
                slot.removeEntity();
                return true;
            }
        }
        return false;
    }

    public boolean hasEmptySlot() {
        return slots.stream().anyMatch(Slot::isEmpty);
    }

    public Optional<Slot<T>> findEmptySlot() {
        return slots.stream().filter(Slot::isEmpty).findFirst();
    }

    public int size() {
        return slots.size();
    }

    public int emptySlotsAmount() {
        return (int) slots.stream().filter(Slot::isEmpty).count();
    }

    // return false if no space to receive
    public boolean receiveEntity(T entity) {
        if (slots.isEmpty() || !slots.get(0).isEntityMatchRequirements(entity))
            return false;
        Optional<Slot<T>> emptySlot = findEmptySlot();
        if (emptySlot.isPresent()) {
            emptySlot.get().setEntity(entity);
            return true;
        }
        else return false;
    }

    public List<Slot<T>> getSlots() {
        return slots;
    }

    public void clear() {
        for (Slot<T> slot : slots) {
            slot.removeEntity();
        }
    }
}
