package ru.rdude.rpg.game.logic.entities.items;

import ru.rdude.rpg.game.logic.data.ItemData;

import java.lang.reflect.InvocationTargetException;

public class ItemFabric {

    public static ItemFabric instance = new ItemFabric();

    public Item getItemByGuid(long guid, int count) {
        ItemData data = ItemData.getItemDataByGuid(guid);
        try {
            return data.getItemType().getDeclaredConstructor(ItemData.class, Integer.class).newInstance(data, count);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("can not create Item with this guid");
    }

}
