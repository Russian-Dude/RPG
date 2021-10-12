package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
    private final QuestsJournal questsJournal = new QuestsJournal();
    private final LoggerVisual loggerVisual;
    private final Button endTurnButton;
    private final Button enterCityButton;

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

        // quests button
        Button showQuestsButton = new TextButton("Quests", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        showQuestsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                questsJournal.setVisible(!questsJournal.isVisible());
            }
        });
        addActor(showQuestsButton);
        showQuestsButton.setPosition(timeAndPlaceUi.getWidth() + goldUi.getWidth() + 20f, Gdx.graphics.getHeight() - showQuestsButton.getHeight() - 5f);

        // quests journal
        addActor(questsJournal);
        questsJournal.setPosition(Gdx.graphics.getWidth() / 2f - questsJournal.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - questsJournal.getHeight() / 2f);
        questsJournal.setVisible(false);

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

        // enter city button
        enterCityButton = new EnterCityButton();
        addActor(enterCityButton);
        enterCityButton.setPosition(5f, Gdx.graphics.getHeight() - timeAndPlaceUi.getHeight() - enterCityButton.getHeight() - 10f);
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
