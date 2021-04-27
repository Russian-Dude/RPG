package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.GameMap;

public class SmallMapVisual extends Actor implements Disposable {

    private final OrthographicCamera camera;

    private final TiledMap map = new TiledMap();
    private final HexagonalTiledMapRenderer renderer;

    private final TiledMapTileLayer mainLayer;
    private final TiledMapTileLayer highlightLayer;
    private final TiledMapTileLayer voidLayer;

    public SmallMapVisual(OrthographicCamera camera, GameMap gameMap) {
        this.camera = camera;
        mainLayer = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), VisualConstants.MINI_TILE_WIDTH, VisualConstants.MINI_TILE_HEIGHT);
        highlightLayer = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), VisualConstants.MINI_TILE_WIDTH, VisualConstants.MINI_TILE_HEIGHT);
        voidLayer = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), VisualConstants.MINI_TILE_WIDTH, VisualConstants.MINI_TILE_HEIGHT);

        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                Cell gameMapCell = gameMap.cell(x, y);

                // main
                TiledMapTileLayer.Cell mainCell = new TiledMapTileLayer.Cell();
                mainCell.setTile(MapTilesFactory.getMiniMapTile(gameMapCell, false));
                mainLayer.setCell(x, y, mainCell);

                // highlight
                TiledMapTileLayer.Cell highlightCell = new TiledMapTileLayer.Cell();
                highlightCell.setTile(MapTilesFactory.getMiniMapTile(gameMapCell, true));
                highlightLayer.setCell(x, y, highlightCell);

                // void
                TiledMapTileLayer.Cell voidCell = new TiledMapTileLayer.Cell();
                voidCell.setTile(MapTilesFactory.getMiniVoid());
                voidLayer.setCell(x, y, voidCell);
            }
        }

        highlightLayer.setVisible(false);
        map.getLayers().add(mainLayer);
        map.getLayers().add(highlightLayer);
        map.getLayers().add(voidLayer);
        renderer = new HexagonalTiledMapRendererWithObjectsLayer(map);

        setBounds(getX(), getY(), gameMap.getWidth() * VisualConstants.MINI_TILE_WIDTH_0_75, gameMap.getHeight() * VisualConstants.MINI_TILE_HEIGHT);
    }

    public Texture getMapTexture() {
        voidLayer.setVisible(false);
        highlightLayer.setVisible(false);
        mainLayer.setVisible(true);
        FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 1024, 512, false);
        frameBuffer.begin();
        camera.zoom = 0.935f;
        renderer.setView(camera);
        renderer.render();
        Texture texture = frameBuffer.getColorBufferTexture();
        frameBuffer.end();
        //frameBuffer.dispose();
        return texture;
    }

    public Texture getRoadsAndObjectsTexture() {
        voidLayer.setVisible(false);
        mainLayer.setVisible(false);
        highlightLayer.setVisible(true);
        FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 1024, 512, false);
        frameBuffer.begin();
        camera.zoom = 0.935f;
        renderer.setView(camera);
        renderer.render();
        Texture texture = frameBuffer.getColorBufferTexture();
        frameBuffer.end();
        //frameBuffer.dispose();
        return texture;
    }

    public void setHighlightVisible(boolean value) {
        highlightLayer.setVisible(value);
    }

    public void setVoid(boolean value) {
        voidLayer.setVisible(value);
        mainLayer.setVisible(!value);
        highlightLayer.setVisible(!value);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setProjectionMatrix(camera.combined);
        renderer.setView(camera);
        renderer.render();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        map.dispose();
    }
}
