package ru.rdude.rpg.game.visual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import ru.rdude.rpg.game.logic.entities.skills.SkillVisualTargeter;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.MainMenuGameState;
import ru.rdude.rpg.game.ui.*;

import java.util.*;

public class GameVisual {

    private final List<Stage> nonUiStages = new ArrayList<>();
    private UIStage ui;
    private final EffectsStage effectsStageFront = new EffectsStage();
    private final EffectsStage effectsStageBack = new EffectsStage();
    private Stage currentMainMenuStage;
    private final Stack<Stage> previousMainMenuStages = new Stack<>();
    private final Set<InputProcessor> savedGameProcessors = new HashSet<>();
    private boolean justOpenedMainMenu = true;
    private final SkillVisualTargeter skillVisualTargeter = new SkillVisualTargeter();
    private final Actor actionsActor = new Actor();
    private GameStateStage currentGameStateStage;

    InputMultiplexer multiplexer = new InputMultiplexer();

    public GameVisual() {
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void clear() {
        nonUiStages.clear();
        ui = null;
        previousMainMenuStages.clear();
        savedGameProcessors.clear();
        currentMainMenuStage = null;
    }

    public void clearNonUiStages() {
        nonUiStages.forEach(multiplexer::removeProcessor);
        nonUiStages.clear();
    }

    public UIStage getUi() {
        return ui;
    }

    public void setUi(UIStage ui) {
        Stage oldValue = this.ui;
        this.ui = ui;
        if (oldValue != null && ui != null && !oldValue.equals(ui)) {
            multiplexer.removeProcessor(oldValue);
        }
        if (ui != null) {
            multiplexer.addProcessor(ui);
        }
    }

    public GameStateStage getCurrentGameStateStage() {
        return currentGameStateStage;
    }

    public void addStage(Stage stage) {
        if (stage == null) {
            throw new NullPointerException("Stage can not be null");
        }
        if (stage instanceof GameStateStage) {
            currentGameStateStage = (GameStateStage) stage;
        }
        nonUiStages.add(stage);
        multiplexer.addProcessor(stage);
    }

    public void addStage(Stage stage, int index) {
        if (stage == null) {
            throw new NullPointerException("Stage can not be null");
        }
        if (stage instanceof GameStateStage) {
            currentGameStateStage = (GameStateStage) stage;
        }
        nonUiStages.add(index, stage);
        multiplexer.addProcessor(stage);
    }

    public void removeStage(Stage stage) {
        if (stage == null) {
            throw new NullPointerException("Stage can not be null");
        }
        nonUiStages.remove(stage);
        multiplexer.removeProcessor(stage);
    }

    public void setMenuStage(Stage stage) {
        if (currentMainMenuStage != null) {
            previousMainMenuStages.add(currentMainMenuStage);
        }
        else {
            multiplexer.getProcessors().forEach(savedGameProcessors::add);
            savedGameProcessors.remove(stage);
            multiplexer.clear();
        }

        if (!previousMainMenuStages.isEmpty()) {
            multiplexer.removeProcessor(previousMainMenuStages.peek());
        }

        if (stage != null) {
            multiplexer.addProcessor(stage);
        }
        else {
            savedGameProcessors.forEach(multiplexer::addProcessor);
        }
        currentMainMenuStage = stage;
    }

    public void backMenuStage() {
        backMenuStage(false);
    }

    public void backMenuStage(boolean forceClose) {
        if (!forceClose && currentMainMenuStage != null && currentMainMenuStage instanceof NonClosableMenuStage) {
            return;
        }
        if (previousMainMenuStages.isEmpty()) {
            if (!(Game.getCurrentGame().getCurrentGameState() instanceof MainMenuGameState)) {
                multiplexer.removeProcessor(currentMainMenuStage);
                currentMainMenuStage = null;
                savedGameProcessors.forEach(multiplexer::addProcessor);
            }
            return;
        }
        multiplexer.removeProcessor(currentMainMenuStage);
        Stage temp = previousMainMenuStages.pop();
        multiplexer.addProcessor(temp);
        currentMainMenuStage = temp;
    }

    public void closeMenus() {
        while (currentMainMenuStage != null) {
            backMenuStage(true);
        }
    }

    public boolean isJustOpenedMainMenu() {
        return justOpenedMainMenu;
    }

    public void setJustOpenedMainMenu(boolean justOpenedMainMenu) {
        this.justOpenedMainMenu = justOpenedMainMenu;
    }

    public boolean isInGameMenuShown() {
        return currentMainMenuStage != null;
    }

    public void goToMainMenu() {
        nonUiStages.clear();
        ui = null;
        multiplexer.clear();
        previousMainMenuStages.clear();
        currentMainMenuStage = null;
        setMenuStage(MainMenuStage.instance);
        Game.getCurrentGame().getGameStateHolder().setGameState(new MainMenuGameState());
    }

    public void clearMainMenus() {
        multiplexer.clear();
        previousMainMenuStages.clear();
        currentMainMenuStage = null;
    }

    public Stage getCurrentMainMenuStage() {
        return currentMainMenuStage;
    }

    public SkillVisualTargeter getSkillTargeter() {
        return skillVisualTargeter;
    }

    public EffectsStage getEffectsStageFront() {
        return effectsStageFront;
    }

    public EffectsStage getEffectsStageBack() {
        return effectsStageBack;
    }

    public void addAction(Action action) {
        this.actionsActor.addAction(action);
    }

    public void draw() {
        final float deltaTime = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (ui != null && ui.getCommandsInputVisual().isVisible()) {
                ui.swapConsoleVisibility();
            }
            if (Game.getCurrentGame().getCurrentGameState() instanceof MainMenuGameState) {
                backMenuStage();
            }
            else {
                if (currentMainMenuStage == null) {
                    setMenuStage(InGameMenuStage.getInstance());
                }
                else {
                    backMenuStage();
                }
            }
        }
        if ((Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) && Game.getGameVisual().getSkillTargeter().isTargeting()) {
            Gdx.graphics.setCursor(UiData.Cursor.DEFAULT.SIMPLE);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.GRAVE) && ui != null) {
            ui.swapConsoleVisibility();
        }
        for (Stage stage : nonUiStages) {
            stage.draw();
            stage.act(deltaTime);
        }
        effectsStageBack.draw();
        effectsStageBack.act(deltaTime);
        if (ui != null) {
            ui.draw();
            ui.act(deltaTime);
        }
        effectsStageFront.draw();
        effectsStageFront.act(deltaTime);
        if (currentMainMenuStage != null) {
            currentMainMenuStage.draw();
            currentMainMenuStage.act(deltaTime);
        }
        skillVisualTargeter.act();
        Game.getCurrentGame().getSkillsSequencer().act(deltaTime);
        actionsActor.act(deltaTime);
    }
}
