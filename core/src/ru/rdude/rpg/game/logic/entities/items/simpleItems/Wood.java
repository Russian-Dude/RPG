package ru.rdude.rpg.game.logic.entities.items.simpleItems;

import ru.rdude.rpg.game.logic.data.ItemData;

public class Wood extends SimpleItem {
    public Wood(ItemData itemData, int amount) {
        super(itemData, amount);
    }

    public Wood(ItemData itemData) {
        super(itemData);
    }
}
