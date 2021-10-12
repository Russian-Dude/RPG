package ru.rdude.rpg.game.logic.entities.items.holders;

import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.holders.SlotsHolder;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.ArrayList;

@JsonPolymorphicSubType("shopPredicate")
public class ShopSlotsHolder extends SlotsHolder<Item> {

    public ShopSlotsHolder() {
        slots = new ArrayList<>(60);
        for (int i = 0; i < 60; i++) {
            slots.add(new ShopItemSlot());
        }
    }

}
