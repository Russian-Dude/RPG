package ru.rdude.rpg.game.logic.entities.quests.rewards;

import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.game.Game;

public class QuestExpReward extends QuestReward<Integer> {

    public QuestExpReward(Integer reward) {
        super(null, reward);
    }

    @Override
    public boolean needToSelectPlayer() {
        return false;
    }

    @Override
    public void reward() {
        Game.getCurrentGame().getCurrentPlayers().streamOnly(Player.class).forEach(player -> {
            player.stats().lvl().exp().increase(getReward());
            player.getCurrentClass().getLvl().exp().increase(getReward());
        });
    }

    @Override
    public String askRewardTargetQuestion() {
        return "";
    }

    @Override
    public String rewardString() {
        return "Experience +" + getReward();
    }
}
