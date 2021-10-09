package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.entities.quests.rewards.QuestReward;
import ru.rdude.rpg.game.logic.game.CurrentGameObserver;
import ru.rdude.rpg.game.logic.game.Game;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreType
public class QuestRewardTargetRequestStage extends Stage implements NonClosableMenuStage, CurrentGameObserver {

    private static QuestRewardTargetRequestStage instance;

    private final Table mainTable = new Table(UiData.DEFAULT_SKIN);
    private final Label question = new Label("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
    private final Map<Player, Button> playerButtons = new HashMap<>();

    private QuestReward<?> currentReward;

    public QuestRewardTargetRequestStage() {
        super();
        question.setAlignment(Align.center);
        Game.subscribe(this);
    }

    public static QuestRewardTargetRequestStage getInstance() {
        if (instance == null) {
            instance = new QuestRewardTargetRequestStage();
        }
        return instance;
    }

    public void setCurrentReward(QuestReward<?> currentReward) {
        playerButtons.forEach((player, button) -> button.setDisabled(!player.isAlive()));
        question.setText(currentReward.askRewardTargetQuestion());
        this.currentReward = currentReward;
    }

    private void selectPlayer(Player player) {
        Game.getGameVisual().closeMenus();
        currentReward.setPlayer(player);
        Game.getQuestRewarder().continueRewarding();
    }

    private void createMainTable() {
        mainTable.defaults().space(15f);
        mainTable.background(UiData.SEMI_TRANSPARENT_BACKGROUND);
        mainTable.add(question).width(Gdx.graphics.getWidth() / 2f).center();
        mainTable.row();
        Game.getCurrentGame().getCurrentPlayers().streamOnly(Player.class).forEach(player -> {
            Button playerButton = new TextButton(player.getName(), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
            playerButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    selectPlayer(player);
                }
            });
            mainTable.add(playerButton);
            playerButton.setDisabled(!player.isAlive());
            mainTable.row();
        });
        mainTable.pack();
        addActor(mainTable);
        mainTable.setY((Gdx.graphics.getHeight() - mainTable.getHeight()) / 2);
        mainTable.setX((Gdx.graphics.getWidth() - mainTable.getWidth()) / 2);
    }

    @Override
    public void update(Game game, Action action) {
        if (action == Action.STARTED) {
            mainTable.remove();
            createMainTable();
        }
    }
}
