package ru.rdude.rpg.game.logic.entities.items.holders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.holders.Slot;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("backpackPredicate")
public class BackpackSlotPredicate implements SlotPredicate<Item> {

    private final Being<?> being;

    @JsonCreator
    public BackpackSlotPredicate(@JsonProperty("being") Being<?> being) {
        this.being = being;
    }

    @Override
    public boolean test(Item item) {
        Slot<Item> slot = Slot.withEntity(item);
        if (slot instanceof ShopItemSlot && slot.getEntity() != null && Game.getCurrentGame().getGold().getAmount() < slot.getEntity().price() * slot.getEntity().getAmount()) {
            return false;
        }
        if (item == null || being.isReady()) {
            return true;
        }
        return !(slot instanceof EquipmentSlot);
    }

}
