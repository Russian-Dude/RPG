package ru.rdude.rpg.game.logic.entities.items.simpleItems;

import ru.rdude.rpg.game.logic.data.ItemData;

public class Gem extends SimpleItem {
    public Gem(ItemData itemData, int amount) {
        super(itemData, amount);
    }

    public Gem(ItemData itemData) {
        super(itemData);
    }
}
