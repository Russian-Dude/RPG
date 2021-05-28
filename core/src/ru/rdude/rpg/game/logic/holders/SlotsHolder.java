package ru.rdude.rpg.game.logic.holders;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.Module;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.entities.items.holders.EquipmentSlotsHolder;
import ru.rdude.rpg.game.logic.entities.items.holders.ItemSlotsHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "Implementation")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EquipmentSlotsHolder.class, name = "Equipment"),
        @JsonSubTypes.Type(value = ItemSlotsHolder.class, name = "Items")
})
public abstract class SlotsHolder<T extends Entity> {

    @JsonProperty
    protected List<Slot<T>> slots;

    public SlotsHolder(int capacity) {
        this(capacity, null);
    }

    public SlotsHolder(int capacity, String marker, Predicate<T> ... extraRequirements) {
        slots = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            slots.add(new Slot<T>(marker, extraRequirements));
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
        if (slots.isEmpty() || !slots.get(0).isEntityMatchRequirements(entity))
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
