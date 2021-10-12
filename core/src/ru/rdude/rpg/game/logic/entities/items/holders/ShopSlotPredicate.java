package ru.rdude.rpg.game.logic.entities.items.holders;

import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("shopPredicate")
public class ShopSlotPredicate implements SlotPredicate<Item> {

    @Override
    public boolean test(Item item) {
        return true;
    }

}
