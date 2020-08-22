package ru.rdude.rpg.game.logic.entities.items.simpleItems;

import ru.rdude.rpg.game.logic.data.ItemData;

public class Herb extends SimpleItem{
    public Herb(ItemData itemData, int amount) {
        super(itemData, amount);
    }

    public Herb(ItemData itemData) {
        super(itemData);
    }
}
