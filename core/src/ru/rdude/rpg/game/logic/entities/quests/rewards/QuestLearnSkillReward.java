package ru.rdude.rpg.game.logic.entities.quests.rewards;

import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Player;

public class QuestLearnSkillReward extends QuestReward<SkillData> {

    public QuestLearnSkillReward(Player player, SkillData reward) {
        super(player, reward);
    }

    @Override
    public void reward() {
        if (getPlayer() == null) {
            throw new IllegalStateException("Can not reward player because player is not present");
        }
        getPlayer().getAvailableSkills().add(getReward());
    }

    @Override
    public String askRewardTargetQuestion() {
        return "Select who will learn " + getReward().getName();
    }

    @Override
    public String rewardString() {
        return "Learn skill: " + getReward().getName();
    }
}
