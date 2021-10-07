package ru.rdude.rpg.game.logic.entities.quests.rewards;

import ru.rdude.rpg.game.logic.game.Game;

public class QuestGoldReward extends QuestReward<Integer> {


    public QuestGoldReward(Integer reward) {
        super(null, reward);
    }

    @Override
    public boolean needToSelectPlayer() {
        return false;
    }

    @Override
    public void reward() {
        Game.getCurrentGame().getGold().increase(getReward());
    }

    @Override
    public String askRewardTargetQuestion() {
        return "";
    }

    @Override
    public String rewardString() {
        return "Gold +" + getReward();
    }
}
