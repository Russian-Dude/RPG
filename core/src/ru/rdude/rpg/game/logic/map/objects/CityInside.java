package ru.rdude.rpg.game.logic.map.objects;

import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.QuestData;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.quests.QuestsHolder;
import ru.rdude.rpg.game.logic.enums.ItemMainType;
import ru.rdude.rpg.game.logic.enums.ItemRarity;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.time.TimeChangeObserver;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.*;

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
                .type(ItemMainType.EQUIPMENT)
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
        QuestsHolder questHolder = Game.getCurrentGame().getQuestHolder();
        int averageLvl = Game.getCurrentGame().getCurrentPlayers().stream()
                .map(being -> (int) being.stats().lvl().value())
                .reduce(Integer::sum)
                .orElse(1) / Game.getCurrentGame().getCurrentPlayers().getBeings().size();
        List<QuestData> generated = new ArrayList<>(QuestData.getQuests().values()
                .stream()
                .filter(questData -> questData.getUnique() == QuestData.Unique.NO || !questHolder.isUniqueQuestCreated(questData))
                .filter(questData -> questData.getLvl() >= averageLvl - 4 && questData.getLvl() <= averageLvl + 4)
                .collect(Functions.randomCollector(5)));
        if (generated.size() < 5) {
            List<QuestData> collect = new ArrayList<>(QuestData.getQuests().values());
            Collections.shuffle(collect);
            collect.stream()
                    .filter(questData -> questData.getUnique() == QuestData.Unique.NO || (!generated.contains(questData) && !questHolder.isUniqueQuestCreated(questData)))
                    .limit(5 - generated.size())
                    .forEach(generated::add);
        }
        quests.addAll(generated);
        generated.stream()
                .filter(questData -> questData.getUnique() != QuestData.Unique.NO)
                .forEach(questHolder::uniqueQuestCreated);
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
        QuestsHolder questHolder = Game.getCurrentGame().getQuestHolder();
        List<QuestData> collect = QuestData.getQuests().values().stream()
                .filter(questData -> questData.getUnique() == QuestData.Unique.NO || !questHolder.isUniqueQuestCreated(questData))
                .collect(Functions.randomCollector(Functions.random(1, 3)));
        collect.stream()
                .filter(questData -> questData.getUnique() != QuestData.Unique.NO)
                .forEach(questHolder::uniqueQuestCreated);
        quests.addAll(collect);
    }

    @Override
    public void timeUpdate(int minutes) {
        timeToNextUpdate -= minutes;
        if (minutes <= 0) {
            updateShop();
            updateQuests();
            timeToNextUpdate = Functions.random(4320, 7500);
        }
    }
}
