package ru.rdude.rpg.game.visual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import ru.rdude.rpg.game.mapVisual.MapRenderer;
import ru.rdude.rpg.game.ui.UIStage;

import java.util.ArrayList;
import java.util.List;

public class GameVisual {

    private List<Stage> nonUiStages = new ArrayList<>();
    private UIStage ui;

    private MapRenderer currentMapRenderer;

    InputMultiplexer multiplexer = new InputMultiplexer();

    public GameVisual() {
        Gdx.input.setInputProcessor(multiplexer);
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

    public void removeStage(Stage stage) {
        if (stage != null) {
            nonUiStages.remove(stage);
            multiplexer.removeProcessor(stage);
        }
    }

    public MapRenderer getCurrentMapRenderer() {
        return currentMapRenderer;
    }

    public void setCurrentMapRenderer(MapRenderer currentMapRenderer) {
        this.currentMapRenderer = currentMapRenderer;
    }

    public void draw() {
        for (Stage stage : nonUiStages) {
            stage.draw();
            stage.act(Gdx.graphics.getDeltaTime());
        }
        if (ui != null) {
            ui.draw();
            ui.act(Gdx.graphics.getDeltaTime());
        }
    }
}
