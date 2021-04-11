package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ru.rdude.rpg.game.logic.entities.beings.Player;

public class UIStage extends Stage {

    private final OrthographicCamera camera = new OrthographicCamera();

    public UIStage() {
        super(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        // camera
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera.setToOrtho(false, w, h);
        camera.update();
        getViewport().setCamera(camera);

        // ui elements
        Player p = new Player();
        p.stats().lvl().increase(5);
        addActor(new PlayersVisualBottom(new Player(), new Player(), new Player(), p));
        LoggerVisual loggerVisual = new LoggerVisual();
        addActor(loggerVisual);
    }

    @Override
    public void draw() {
        super.draw();
        camera.update();
    }
}
