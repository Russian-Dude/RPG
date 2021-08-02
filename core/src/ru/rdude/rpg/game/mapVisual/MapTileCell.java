package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class MapTileCell extends TiledMapTileLayer.Cell {

    public final int x;
    public final int y;

    public MapTileCell(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }
}
