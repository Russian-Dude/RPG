package ru.rdude.rpg.game.logic.entities.quests.rewards;

import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.enums.StatName;
import ru.rdude.rpg.game.utils.Functions;

import java.util.Map;
import java.util.stream.Collectors;

public class QuestStatsReward extends QuestReward<Map<StatName, Double>> {

    public QuestStatsReward(Player player, Map<StatName, Double> reward) {
        super(player, reward);
    }

    @Override
    public void reward() {
        if (getPlayer() == null) {
            throw new IllegalStateException("Can not reward player because player is not present");
        }
        getReward().forEach((statName, value) -> {
            getPlayer().stats().get(statName).increase(value);
        });
    }

    @Override
    public String askRewardTargetQuestion() {
        return "Select who will receive " + rewardString();
    }

    @Override
    public String rewardString() {
        return getReward().entrySet().stream()
                .map(entry -> "+ " + Functions.trimDouble(entry.getValue()) + " " + entry.getKey().getName())
                .collect(Collectors.joining(", "));
    }
}
