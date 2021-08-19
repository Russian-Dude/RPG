package ru.rdude.rpg.game.logic.map.objects;

import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.QuestData;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.enums.ItemMainType;
import ru.rdude.rpg.game.logic.enums.ItemRarity;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.time.TimeChangeObserver;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.ArrayList;
import java.util.List;

@JsonPolymorphicSubType("cityInside")
public class CityInside implements TimeChangeObserver {

    private int timeToNextUpdate = 4320;

    private final List<Item> shop = new ArrayList<>();
    private final List<QuestData> quests = new ArrayList<>();

    public void generateShopAndQuests() {
        generateShop();
        generateQuests();
    }

    private void generateShop() {
        // start armor
        List<ItemData> armor = ItemData.getItemsWith()
                .rarity(ItemRarity.BRONZE)
                .type(ItemMainType.ARMOR)
                .getRandomItems(5);
        // start weapons
        List<ItemData> weapons = ItemData.getItemsWith()
                .rarity(ItemRarity.BRONZE)
                .type(ItemMainType.WEAPON)
                .getRandomItems(5);
        // other random items
        List<ItemData> other = ItemData.getItemsWith().getRandomItems(5);

        List<ItemData> preRes = new ArrayList<>();
        preRes.addAll(armor);
        preRes.addAll(weapons);
        preRes.addAll(other);

        preRes.forEach(itemData -> {
            int amount = itemData.isStackable() ? Functions.random(5, Item.MAX_AMOUNT) : 1;
            shop.add(Game.getEntityFactory().items().get(itemData, amount));
        });
    }

    private void generateQuests() {
        // TODO: 29.05.2021 generate quests in city
    }

    private void updateShop() {
        int removing = Math.max(5, shop.size());
        while (shop.size() > 5 && removing > 0) {
            shop.remove(Functions.random(shop));
            removing--;
        }
        ItemData.getItemsWith().getRandomItems(Functions.random(5, 8))
                .forEach(itemData -> shop.add(Game.getEntityFactory().items().get(itemData, itemData.isStackable() ? Functions.random(5, Item.MAX_AMOUNT) : 1)));
    }

    private void updateQuests() {
        // TODO: 29.05.2021 update quests in city
    }

    @Override
    public void timeUpdate(int minutes) {
        timeToNextUpdate -= minutes;
        if (minutes <= 0) {
            updateShop();
            updateQuests();
        }
        timeToNextUpdate = Functions.random(4320, 7500);
    }
}
