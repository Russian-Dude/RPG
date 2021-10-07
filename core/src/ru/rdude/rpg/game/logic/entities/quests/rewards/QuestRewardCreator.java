package ru.rdude.rpg.game.logic.entities.quests.rewards;

import ru.rdude.rpg.game.logic.data.QuestData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.entities.quests.Quest;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.utils.Functions;

import java.util.List;
import java.util.stream.Collectors;

public class QuestRewardCreator {

    public QuestRewards create(Quest quest) {

        QuestRewards rewards = new QuestRewards();
        QuestData questData = quest.getQuestData();
        List<Player> players = Game.getCurrentGame().getCurrentPlayers().getBeings().stream()
                .filter(being -> being instanceof Player)
                .map(being -> (Player) being)
                .collect(Collectors.toList());

        // learn skills
        questData.getLearnSkills().forEach(guid -> {
            SkillData skillData = Game.getEntityFactory().skills().describerToReal(guid);
            switch (questData.getLearnSkillsRewardTarget()) {
                case ALL:
                    players.forEach(player -> rewards.add(new QuestLearnSkillReward(player, skillData)));
                    break;
                case SELECTED:
                    rewards.add(new QuestLearnSkillReward(null, skillData));
                    break;
                case RANDOM:
                    rewards.add(new QuestLearnSkillReward(players.stream().collect(Functions.randomCollector()), skillData));
                    break;
                default:
                    throw new IllegalArgumentException("Quest reward target not implemented");
            }
        });

        // receive items
        questData.getReceiveItems().forEach((guid, amount) -> {
            rewards.add(new QuestItemsReward(Game.getEntityFactory().items().get(guid, amount)));
        });

        // receive stats
        if (!questData.getReceiveStats().isEmpty()) {
            switch (questData.getReceiveStatsRewardTarget()) {
                case ALL:
                    players.forEach(player -> rewards.add(new QuestStatsReward(player, questData.getReceiveStats())));
                    break;
                case SELECTED:
                    rewards.add(new QuestStatsReward(null, questData.getReceiveStats()));
                    break;
                case RANDOM:
                    rewards.add(new QuestStatsReward(players.stream().collect(Functions.randomCollector()), questData.getReceiveStats()));
                    break;
            }
        }

        // gold
        if (questData.getReceiveGold() > 0) {
            rewards.add(new QuestGoldReward(questData.getReceiveGold()));
        }

        // exp reward
        if (questData.getExpReward() > 0) {
            rewards.add(new QuestExpReward(questData.getExpReward()));
        }

        return rewards;
    }

}
