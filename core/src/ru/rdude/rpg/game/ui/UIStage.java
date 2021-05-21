package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ru.rdude.rpg.game.logic.entities.beings.Player;

public class UIStage extends Stage {

    private final OrthographicCamera camera = new OrthographicCamera();

    public UIStage(PlayerVisual... playerVisuals) {
        super(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        // camera
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera.setToOrtho(false, w, h);
        camera.update();
        getViewport().setCamera(camera);

        // ui elements
        addActor(new PlayersVisualBottom(playerVisuals));
        LoggerVisual loggerVisual = new LoggerVisual();
        addActor(loggerVisual);
        TimeAndPlaceUi timeAndPlaceUi = new TimeAndPlaceUi();
        addActor(timeAndPlaceUi);
        timeAndPlaceUi.setPosition(5f, Gdx.graphics.getHeight() - timeAndPlaceUi.getHeight() - 25f);
    }

    public boolean isHit() {
        Vector2 stageCoordinates = screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        return this.hit(stageCoordinates.x, stageCoordinates.y, true) != null;
    }

    @Override
    public void draw() {
        super.draw();
        camera.update();
    }
}
