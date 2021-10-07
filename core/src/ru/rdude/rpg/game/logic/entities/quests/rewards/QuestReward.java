package ru.rdude.rpg.game.logic.entities.quests.rewards;

import ru.rdude.rpg.game.logic.entities.beings.Player;

public abstract class QuestReward<T> {

    private Player player;
    private final T reward;

    public QuestReward(Player player, T reward) {
        this.player = player;
        this.reward = reward;
    }

    public abstract void reward();

    public abstract String askRewardTargetQuestion();

    public abstract String rewardString();

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public T getReward() {
        return reward;
    }

    public boolean needToSelectPlayer() {
        return player == null;
    }

}
