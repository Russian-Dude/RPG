package ru.rdude.rpg.game.logic.entities.items.holders;

import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.holders.Slot;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("shopSlot")
public class ShopItemSlot extends Slot<Item> {

    public ShopItemSlot() {
        super(null, new ShopSlotPredicate());
    }
}
