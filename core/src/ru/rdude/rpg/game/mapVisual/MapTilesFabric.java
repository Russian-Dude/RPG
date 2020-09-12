package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.bioms.Water;
import ru.rdude.rpg.game.logic.map.reliefs.Hills;
import ru.rdude.rpg.game.logic.map.reliefs.Mountains;
import ru.rdude.rpg.game.logic.map.reliefs.Plain;
import ru.rdude.rpg.game.utils.Functions;

import java.util.HashMap;
import java.util.Map;

public class MapTilesFabric {

    private static Map<String, StaticTiledMapTile> tiles = new HashMap<>();
    private static TextureAtlas textureAtlas = new TextureAtlas("map_tiles.txt");;

    private static Texture tilesTexture = new Texture(Gdx.files.internal("map_tiles.png"));
    //private static StaticTiledMapTile test = new StaticTiledMapTile(new TextureRegion(tilesTexture));

    public static StaticTiledMapTile getTile(Cell cell) {
        if (cell.getObject() != null || cell.getRoad() != null) {
            return getTileOrPutAndGet("VOID");
        }
        return getTileOrPutAndGet(cell.getBiom().getClass().getSimpleName().toUpperCase());
    }


    private static StaticTiledMapTile getTileOrPutAndGet(String name) {
        if (!tiles.containsKey(name)) {
            StaticTiledMapTile tile = new StaticTiledMapTile(textureAtlas.findRegion(name));
            tiles.put(name, tile);
        }
        return tiles.get(name);
    }

}
