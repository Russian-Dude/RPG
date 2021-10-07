package ru.rdude.rpg.game.logic.entities.quests.rewards;

import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.items.holders.ItemSlotsHolder;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class QuestRewards {

    private final List<QuestReward<?>> rewards = new ArrayList<>();
    private final List<QuestReward<?>> rewardsThatNeedToBeSelected = new ArrayList<>();

    public void add(QuestReward<?> reward) {
        if (reward.needToSelectPlayer()) {
            rewardsThatNeedToBeSelected.add(reward);
        }
        else {
            rewards.add(reward);
        }
    }

    public List<QuestReward<?>> getRewards() {
        return rewards;
    }

    public List<QuestReward<?>> getRewardsThatNeedToBeSelected() {
        return rewardsThatNeedToBeSelected;
    }

    public List<QuestReward<?>> getAllRewards() {
        return Stream.of(rewards, rewardsThatNeedToBeSelected)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    public Optional<QuestGoldReward> getGoldReward() {
        return rewards.stream()
                .filter(reward -> reward instanceof QuestGoldReward)
                .map(reward -> (QuestGoldReward) reward)
                .findAny();
    }

    public List<QuestExpReward> questExpRewards() {
        return rewards.stream()
                .filter(reward -> reward instanceof QuestExpReward)
                .map(reward -> (QuestExpReward) reward)
                .collect(toList());
    }

    public Map<Player, Set<QuestReward<?>>> rewardsForEachPlayer() {
        return getAllRewards().stream()
                .filter(reward -> reward.getPlayer() != null)
                .collect(groupingBy(QuestReward::getPlayer, mapping(Function.identity(), toSet())));
    }

    public Optional<ItemSlotsHolder> createItemsRewardsSlotsHolder() {
        List<Item> itemRewards = getAllRewards().stream()
                .filter(reward -> reward instanceof QuestItemsReward)
                .map(reward -> (QuestItemsReward) reward)
                .map(QuestReward::getReward)
                .map(Item::copy)
                .collect(toList());
        if (itemRewards.isEmpty()) {
            return Optional.empty();
        }
        List<Item> moreItems = new ArrayList<>();
        for (Item itemReward : itemRewards) {
            int remainder = itemReward.getAmount();
            Item lastItem = itemReward;
            while (remainder > Item.MAX_AMOUNT) {
                lastItem.setAmount(Item.MAX_AMOUNT);
                remainder -= Item.MAX_AMOUNT;
                Item copy = lastItem.copy();
                copy.setAmount(remainder);
                lastItem = copy;
                moreItems.add(copy);
            }
        }
        itemRewards.addAll(moreItems);
        ItemSlotsHolder itemSlotsHolder = new ItemSlotsHolder(itemRewards.size());
        itemRewards.forEach(itemSlotsHolder::receiveEntity);
        return Optional.of(itemSlotsHolder);
    }
}
