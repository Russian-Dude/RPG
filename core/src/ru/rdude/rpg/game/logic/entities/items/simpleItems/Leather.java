package ru.rdude.rpg.game.logic.entities.items.simpleItems;

import ru.rdude.rpg.game.logic.data.ItemData;

public class Leather extends SimpleItem {
    public Leather(ItemData itemData, int amount) {
        super(itemData, amount);
    }

    public Leather(ItemData itemData) {
        super(itemData);
    }
}
