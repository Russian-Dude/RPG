package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Disposable;
import ru.rdude.rpg.game.logic.map.GameMap;

public class MapVisual extends Actor implements Disposable {

    private OrthographicCamera camera;

    private TiledMap map;
    private HexagonalTiledMapRenderer renderer;



    public MapVisual(OrthographicCamera camera, GameMap gameMap) {
        this.camera = camera;
        map = new TiledMap();
        MapLayers layers = map.getLayers();
        TiledMapTileLayer layer = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), 128, 128);
        for (int x = 0; x < gameMap.getWidth() - 1; x++) {
            for (int y = 0; y < gameMap.getHeight() - 1; y++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(MapTilesFabric.getTile(gameMap.cell(x, y)));
                layer.setCell(x, y, cell);
            }
        }
        layers.add(layer);
        setBounds(getX(), getY(), gameMap.getWidth() * 128, gameMap.getHeight() * 128);
        setTouchable(Touchable.enabled);

        renderer = new HexagonalTiledMapRenderer(map);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        renderer.setView(camera);
        renderer.render();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        map.dispose();
    }
}
