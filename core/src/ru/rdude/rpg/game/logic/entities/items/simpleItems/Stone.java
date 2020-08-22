package ru.rdude.rpg.game.logic.entities.items.simpleItems;

import ru.rdude.rpg.game.logic.data.ItemData;

public class Stone extends SimpleItem {
    public Stone(ItemData itemData, int amount) {
        super(itemData, amount);
    }

    public Stone(ItemData itemData) {
        super(itemData);
    }
}
