package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import ru.rdude.rpg.game.logic.game.Game;

public class MapRenderer extends HexagonalTiledMapRenderer {

    public enum Layer {
        BIOM(0),
        ROAD(1),
        RELIEF_UP(2),
        RELIEF_DOWN(3),
        VOID(4);
        
        public final int index;
        Layer(int index) { this.index = index; }
    }

    public enum positionZ { BOTTOM, MIDDLE, TOP }


    private int currentRenderingLayer = 0;
    private MapSpriteObject[][] spriteObjects;

    public MapRenderer(TiledMap map, int width, int height) {
        super(map);
        spriteObjects = new MapSpriteObject[width][height];
        Game.getGameVisual().setCurrentMapRenderer(this);
    }


    @Override
    public void render() {
        beginRender();
        for (int i = 0; i < map.getLayers().size(); i++) {
            currentRenderingLayer = i;
            renderMapLayer(map.getLayers().get(i));
        }
        endRender();
    }

}
