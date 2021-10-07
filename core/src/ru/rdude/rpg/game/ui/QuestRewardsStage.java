package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ru.rdude.rpg.game.logic.entities.quests.Quest;
import ru.rdude.rpg.game.logic.entities.quests.rewards.QuestExpReward;
import ru.rdude.rpg.game.logic.entities.quests.rewards.QuestRewards;
import ru.rdude.rpg.game.logic.game.Game;

import java.util.List;

public class QuestRewardsStage extends Stage implements NonClosableMenuStage {

    public QuestRewardsStage(Quest quest, QuestRewards questRewards) {
        super();
        Table mainTable = new Table(UiData.DEFAULT_SKIN);
        mainTable.background(UiData.SEMI_TRANSPARENT_BACKGROUND);
        mainTable.defaults().space(15f);

        // title
        mainTable.add(new Label(quest.getQuestData().getName() + " complete", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE)).center();
        mainTable.row();

        // gold
        questRewards.getGoldReward().ifPresent(goldReward -> {
            mainTable.add(new Label(goldReward.rewardString(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
            mainTable.row();
        });

        // exp
        List<QuestExpReward> questExpRewards = questRewards.questExpRewards();
        if (!questExpRewards.isEmpty()) {
            questExpRewards.forEach(questExpReward -> {
                mainTable.add(new Label(questExpReward.rewardString(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                mainTable.row();
            });
        }

        // players personal rewards
        questRewards.rewardsForEachPlayer().forEach((player, rewards) -> {
            mainTable.add(new Label(player.getName() + ":", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
            mainTable.row();
            rewards.forEach(reward -> {
                mainTable.add(new Label(reward.rewardString(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                mainTable.row();
            });
        });

        // items reward
        questRewards.createItemsRewardsSlotsHolder().ifPresent(itemSlotsHolder -> {
            ItemSlotsHolderVisual itemSlotsHolderVisual = new ItemSlotsHolderVisual(itemSlotsHolder, Math.min(itemSlotsHolder.size(), 10));
            mainTable.add(new Label("Items:", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
            mainTable.row();
            mainTable.add(itemSlotsHolderVisual);
            mainTable.row();
        });

        // button
        Button okButton = new TextButton("Ok", UiData.DEFAULT_SKIN, UiData.YES_BUTTON_STYLE);
        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Game.getGameVisual().closeMenus();
            }
        });
        mainTable.add(okButton);
        mainTable.row();

        mainTable.pack();
        mainTable.setY((Gdx.graphics.getHeight() - mainTable.getHeight()) / 2);
        mainTable.setX((Gdx.graphics.getWidth() - mainTable.getWidth()) / 2);
        addActor(mainTable);
    }
}
