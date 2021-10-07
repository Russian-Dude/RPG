package ru.rdude.rpg.game.logic.entities.quests.rewards;

import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.game.Game;

import java.util.List;
import java.util.stream.Collectors;

public class QuestItemsReward extends QuestReward<Item> {

    public QuestItemsReward(Item reward) {
        super(null, reward);
    }

    @Override
    public boolean needToSelectPlayer() {
        return false;
    }


    @Override
    public void reward() {
        int i = 0;
        List<Player> beings = Game.getCurrentGame().getCurrentPlayers().streamOnly(Player.class)
                .collect(Collectors.toList());
        Player player = beings.get(i);
        while (i < beings.size() && !player.receive(getReward())) {
            i++;
        }
    }

    @Override
    public String askRewardTargetQuestion() {
        return "";
    }

    @Override
    public String rewardString() {
        return "Items:";
    }
}
