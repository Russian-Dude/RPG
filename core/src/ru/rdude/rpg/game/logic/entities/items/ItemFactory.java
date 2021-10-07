package ru.rdude.rpg.game.logic.entities.items;

import ru.rdude.rpg.game.logic.data.ItemData;

public class ItemFactory {

    public Item get(long guid) {
        return get(ItemData.getItemDataByGuid(guid));
    }

    public Item get(long guid, int amount) {
        return get(ItemData.getItemDataByGuid(guid), amount);
    }

    public Item get(ItemData itemData) {
        return get(itemData, 1);
    }

    public ItemData describerToReal(ItemData itemData) {
        if (!itemData.isDescriber()) {
            return itemData;
        }
        return ItemData.getItemsWith()
                .rarity(itemData.getRarity())
                .type(itemData.getItemMainType())
                .type(itemData.getItemType())
                .elements(itemData.getElements())
                .getRandom();
    }

    public ItemData describerToReal(long guid) {
        return describerToReal(ItemData.getItemDataByGuid(guid));
    }

    public Item get(ItemData itemData, int amount) {
        return new Item(describerToReal(itemData), amount);
    }

}
