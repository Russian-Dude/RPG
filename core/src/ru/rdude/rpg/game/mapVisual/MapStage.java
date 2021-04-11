package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ru.rdude.rpg.game.logic.map.GameMap;

public class MapStage extends Stage {

    private final OrthographicCamera camera = new OrthographicCamera();
    private final GameMap gameMap;
    private final MapVisual mapVisual;

    public MapStage(GameMap gameMap) {
        super(new FitViewport(gameMap.getWidth() * VisualConstants.TILE_WIDTH, gameMap.getHeight() * VisualConstants.TILE_HEIGHT));
        this.gameMap = gameMap;

        // camera
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera.setToOrtho(false, w, h);
        camera.update();
        getViewport().setCamera(camera);

        // map visual
        mapVisual = new MapVisual(camera, gameMap);
        addActor(mapVisual);
    }

    @Override
    public OrthographicCamera getCamera() {
        return camera;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public MapVisual getMapVisual() {
        return mapVisual;
    }

    @Override
    public void draw() {
        super.draw();
        camera.update();
    }

    @Override
    public void act() {
        super.act();
    }

}
