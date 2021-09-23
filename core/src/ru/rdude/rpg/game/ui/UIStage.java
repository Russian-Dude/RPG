package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.GameStateObserver;
import ru.rdude.rpg.game.visual.VisualBeing;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonIgnoreType
public class UIStage extends Stage implements GameStateObserver {

    private final OrthographicCamera camera = new OrthographicCamera();
    private final List<VisualBeing<?>> visualBeings;
    private final PlayersVisualBottom playersVisualBottom;
    private final CommandsInputVisual commandsInputVisual = new CommandsInputVisual();
    private final BattleVictoryWindow battleVictoryWindow = new BattleVictoryWindow();
    private final LoggerVisual loggerVisual;
    private final Button endTurnButton;

    public UIStage() {
        super(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        // camera
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera.setToOrtho(false, w, h);
        camera.update();
        getViewport().setCamera(camera);

        // area for throwing items away
        ItemRemoverArea itemRemoverArea = new ItemRemoverArea();
        addActor(itemRemoverArea);
        Game.getCurrentGame().getItemsDragAndDrop().addRemoverArea(itemRemoverArea);

        // ui elements
        // players visuals
        playersVisualBottom = new PlayersVisualBottom();
        visualBeings = playersVisualBottom.getVisualBeings();
        addActor(playersVisualBottom);
        // logger
        loggerVisual = new LoggerVisual();
        addActor(loggerVisual);
        // time and place
        TimeAndPlaceUi timeAndPlaceUi = new TimeAndPlaceUi();
        addActor(timeAndPlaceUi);
        timeAndPlaceUi.setPosition(5f, Gdx.graphics.getHeight() - timeAndPlaceUi.getHeight() - 5f);
        // gold
        GoldUi goldUi = new GoldUi();
        addActor(goldUi);
        goldUi.setPosition(timeAndPlaceUi.getWidth() + 15f, Gdx.graphics.getHeight() - goldUi.getHeight() - 5f);
        // end turn button
        endTurnButton = new EndTurnButton();
        addActor(endTurnButton);
        endTurnButton.setPosition(Gdx.graphics.getWidth() - endTurnButton.getWidth() - 25f, Gdx.graphics.getHeight() - endTurnButton.getHeight() - 25f);
        // commands input (console)
        addActor(commandsInputVisual);
        commandsInputVisual.setPosition(Gdx.graphics.getWidth() / 2f - commandsInputVisual.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - commandsInputVisual.getHeight() / 2f);
        commandsInputVisual.setVisible(false);
        // battle victory window
        addActor(battleVictoryWindow);
        battleVictoryWindow.setPosition(Gdx.graphics.getWidth() / 2f - battleVictoryWindow.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - battleVictoryWindow.getHeight() / 2f);
        battleVictoryWindow.setVisible(false);
    }

    public boolean isHit() {
        Vector2 stageCoordinates = screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        return this.hit(stageCoordinates.x, stageCoordinates.y, true) != null;
    }

    public Set<PlayerVisual> getPlayerVisuals() {
        return visualBeings.stream()
                .filter(visualBeing -> visualBeing instanceof PlayerVisual)
                .map(visualBeing -> (PlayerVisual) visualBeing)
                .collect(Collectors.toSet());
    }

    public List<VisualBeing<?>> getVisualBeings() {
        return visualBeings;
    }

    public void swapConsoleVisibility() {
        commandsInputVisual.setVisible(!commandsInputVisual.isVisible());
    }

    public CommandsInputVisual getCommandsInputVisual() {
        return commandsInputVisual;
    }

    public BattleVictoryWindow getBattleVictoryWindow() {
        return battleVictoryWindow;
    }

    public LoggerVisual getLoggerVisual() {
        return loggerVisual;
    }

    public PlayersVisualBottom getPlayersVisualBottom() {
        return playersVisualBottom;
    }

    @Override
    public void draw() {
        super.draw();
        camera.update();
    }

    @Override
    public void update(GameStateBase oldValue, GameStateBase newValue) {
        endTurnButton.setVisible(newValue instanceof Battle);
    }
}
