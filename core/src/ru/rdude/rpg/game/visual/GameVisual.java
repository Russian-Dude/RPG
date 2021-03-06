package ru.rdude.rpg.game.visual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import ru.rdude.rpg.game.logic.entities.skills.SkillVisualTargeter;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.MainMenuGameState;
import ru.rdude.rpg.game.ui.InGameMenuStage;
import ru.rdude.rpg.game.ui.MainMenuStage;
import ru.rdude.rpg.game.ui.UIStage;

import java.util.*;

public class GameVisual {

    private final List<Stage> nonUiStages = new ArrayList<>();
    private UIStage ui;
    private Stage currentMainMenuStage;
    private final Stack<Stage> previousMainMenuStages = new Stack<>();
    private final Set<InputProcessor> savedGameProcessors = new HashSet<>();
    private boolean justOpenedMainMenu = true;
    private final SkillVisualTargeter skillVisualTargeter = new SkillVisualTargeter();

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

    public void addStage(Stage stage) {
        if (stage == null) {
            throw new NullPointerException("Stage can not be null");
        }
        nonUiStages.add(stage);
        multiplexer.addProcessor(stage);
    }

    public void addStage(Stage stage, int index) {
        if (stage == null) {
            throw new NullPointerException("Stage can not be null");
        }
        nonUiStages.add(index, stage);
        multiplexer.addProcessor(stage);
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
            backMenuStage();
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

    public void draw() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
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
        for (Stage stage : nonUiStages) {
            stage.draw();
            stage.act(Gdx.graphics.getDeltaTime());
        }
        if (ui != null) {
            ui.draw();
            ui.act(Gdx.graphics.getDeltaTime());
        }
        if (currentMainMenuStage != null) {
            currentMainMenuStage.draw();
            currentMainMenuStage.act(Gdx.graphics.getDeltaTime());
        }
        skillVisualTargeter.act();
        Game.getCurrentGame().getSkillsSequencer().act(Gdx.graphics.getDeltaTime());
    }
}
