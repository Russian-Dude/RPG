package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.bioms.Water;
import ru.rdude.rpg.game.logic.map.reliefs.Hills;
import ru.rdude.rpg.game.logic.map.reliefs.Mountains;
import ru.rdude.rpg.game.logic.map.reliefs.Plain;

import java.util.HashMap;
import java.util.Map;

public class MapTilesFabric {

    private static Map<String, StaticTiledMapTile> tiles = new HashMap<>();
    private static TextureAtlas textureAtlas = new TextureAtlas("map_tiles.txt");;

    public static StaticTiledMapTile getTile(Cell cell) {
        if (cell.getObject() == null && cell.getRoad() == null) {
            if (cell.getBiom() == Water.getInstance())
                return getTileOrPutAndGet("WATER_PLAIN");
            if (cell.getRelief() == Mountains.getInstance())
                return getTileOrPutAndGet(cell.getBiom().toString() + "_MOUNTAIN");
            return getTileOrPutAndGet(cell.getBiom().toString() + "_" + cell.getRelief().toString());
        }
        return tiles.get("VOID");
    }

    private static StaticTiledMapTile getTileOrPutAndGet(String name) {
        System.out.println(name + "  " + textureAtlas.findRegion(name));
        if (!tiles.containsKey(name))
            tiles.put(name, new StaticTiledMapTile(textureAtlas.findRegion(name)));
        return tiles.get(name);
    }

}
