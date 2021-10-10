package ru.rdude.rpg.game.logic.entities.items.holders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.enums.ItemMainOrConcreteType;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.Set;
import java.util.function.Predicate;

@JsonPolymorphicSubType("equipmentPredicate")
public class EquipmentSlotPredicate implements SlotPredicate<Item> {

    private final Set<ItemMainOrConcreteType> itemTypes;
    private final Being<?> being;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public EquipmentSlotPredicate(@JsonProperty("itemTypes") Set<ItemMainOrConcreteType> itemTypes, @JsonProperty("being") Being<?> being) {
        this.itemTypes = itemTypes;
        this.being = being;
    }

    @Override
    public boolean test(Item item) {
        return being.isReady()
                && (itemTypes.contains(item.getEntityData().getItemType())
                || itemTypes.contains(item.getEntityData().getItemType().getMainType()))
                && being.stats().isMatchRequirementsOf(item.requirements());
    }
}
