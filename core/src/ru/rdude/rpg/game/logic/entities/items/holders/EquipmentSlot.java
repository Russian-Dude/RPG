package ru.rdude.rpg.game.logic.entities.items.holders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.enums.ItemMainOrConcreteType;
import ru.rdude.rpg.game.logic.holders.Slot;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@JsonPolymorphicSubType("equipmentSlot")
public class EquipmentSlot extends Slot<Item> {

    public EquipmentSlot(Being<?> being, ItemMainOrConcreteType itemType, ItemMainOrConcreteType... itemTypes) {
        super(((Enum<?>) itemType).name(), createExtraRequirements(being, itemType, itemTypes));
    }

    private static Predicate<Item> createExtraRequirements(Being<?> being, ItemMainOrConcreteType itemType, ItemMainOrConcreteType... itemTypes) {
        Set<ItemMainOrConcreteType> types = new HashSet<>();
        types.add(itemType);
        if (itemTypes != null) {
            types.addAll(Arrays.asList(itemTypes));
        }
        return new EquipmentSlotPredicate(types, being);
    }

    @JsonCreator
    private EquipmentSlot(@JsonProperty("requirements") Set<EquipmentSlotPredicate> requirements) {
        super(requirements);
    }

    @JsonProperty("requirements")
    private Set<EquipmentSlotPredicate> getExtraRequirements() {
        return (Set<EquipmentSlotPredicate>) extraRequirements;
    }

}
