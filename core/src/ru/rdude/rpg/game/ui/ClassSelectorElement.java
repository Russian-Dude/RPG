package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.enums.GameState;
import ru.rdude.rpg.game.logic.game.CurrentGameObserver;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.GameStateObserver;
import ru.rdude.rpg.game.logic.playerClass.PlayerClass;
import ru.rdude.rpg.game.logic.playerClass.PlayerClassObserver;

@JsonIgnoreType
public class ClassSelectorElement extends Table implements Comparable<ClassSelectorElement>, PlayerClassObserver, GameStateObserver, CurrentGameObserver {

    private final PlayerClass playerClass;
    private final Player player;
    private final Label score;
    private final Group progressOrButton;
    private final ProgressBar progressBar;
    private final Button selectButton;

    public ClassSelectorElement(PlayerClass playerClass, Player player) {
        super(UiData.DEFAULT_SKIN);
        this.playerClass = playerClass;
        this.player = player;
        this.progressOrButton = new Group();
        score = new Label(player.getCurrentClass() == playerClass ? "current" : (playerClass.isOpen() ? "open" : playerClass.getClassData().getRequiredPoints() - playerClass.getNeedToOpen() + " / " + playerClass.getClassData().getRequiredPoints()), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        progressBar = new ProgressBar(0f, playerClass.getClassData().getRequiredPoints(), 1f, false, UiData.DEFAULT_SKIN, "mini");
        progressBar.setValue(playerClass.getClassData().getRequiredPoints() - playerClass.getNeedToOpen());
        selectButton = new TextButton("select", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        selectButton.setDisabled(Game.getCurrentGame().getCurrentGameState().getEnumValue() != GameState.CAMP);
        selectButton.setVisible(player.getCurrentClass() != playerClass);
        progressBar.setVisible(player.getCurrentClass() != playerClass);
        selectButton.setHeight(25f);
        progressBar.setWidth(selectButton.getWidth());

        selectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (!selectButton.isDisabled()) {
                    player.setCurrentClass(playerClass);
                }
            }
        });

        playerClass.subscribe(this);
        Game.getCurrentGame().getGameStateHolder().subscribe(this);


        progressOrButton.addActor(playerClass.isOpen() ? selectButton : progressBar);
        progressOrButton.setSize(selectButton.getWidth(), selectButton.getHeight());
        add(new Label(playerClass.getClassData().getName(), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        row();
        add(score);
        row();
        add(progressOrButton).fillX();
        pack();

        Label tooltipLabel = new Label(playerClass.getClassData().getDescription(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        Table tooltipTable = new Table(UiData.DEFAULT_SKIN);
        tooltipTable.background(UiData.SEMI_TRANSPARENT_BACKGROUND);
        tooltipTable.add(tooltipLabel)
                        .width(Math.min(tooltipLabel.getPrefWidth(), Gdx.graphics.getWidth() / 3f));
        tooltipLabel.setWrap(true);
        Tooltip<Table> tooltip = new Tooltip<>(tooltipTable);
        tooltip.setInstant(true);
        addListener(tooltip);

        background(UiData.SEMI_TRANSPARENT_BACKGROUND);
    }

    @Override
    public int compareTo(ClassSelectorElement o) {
        if (player.getCurrentClass() == playerClass) {
            return Integer.MIN_VALUE;
        }
        else if (player.getCurrentClass() == o.playerClass) {
            return Integer.MAX_VALUE;
        }
        int openScore = playerClass.isOpen() ? Integer.MAX_VALUE / 2 : 0;
        int otherOpenScore = o.playerClass.isOpen() ? Integer.MAX_VALUE / 2 : 0;
        return (int) (((100.0 / playerClass.getClassData().getRequiredPoints()) * playerClass.getNeedToOpen() + openScore)
                        - ((100.0 / o.playerClass.getClassData().getRequiredPoints()) * o.playerClass.getNeedToOpen()) + otherOpenScore);
    }

    @Override
    public void updatePlayerClass(PlayerClass playerClass) {
        long requiredPoints = playerClass.getClassData().getRequiredPoints();
        long points = requiredPoints - playerClass.getNeedToOpen();
        score.setText(player.getCurrentClass() == playerClass ? "current" : (playerClass.isOpen() ? "open" : points + " / " + requiredPoints));
        progressBar.setValue(points);
        if (playerClass.isOpen()) {
            progressBar.remove();
            progressOrButton.addActor(selectButton);
        }
        selectButton.setVisible(player.getCurrentClass() != playerClass);
        progressBar.setVisible(player.getCurrentClass() != playerClass);
    }

    @Override
    public void update(GameStateBase oldValue, GameStateBase newValue) {
        selectButton.setDisabled(newValue.getEnumValue() != GameState.CAMP);
    }

    @Override
    public void update(Game game, Action action) {
        playerClass.unsubscribe(this);
        Game.getCurrentGame().getGameStateHolder().unsubscribe(this);
    }
}
