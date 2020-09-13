package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.CellSide;
import ru.rdude.rpg.game.logic.map.bioms.Water;
import ru.rdude.rpg.game.logic.map.reliefs.Hills;
import ru.rdude.rpg.game.logic.map.reliefs.Mountains;
import ru.rdude.rpg.game.logic.map.reliefs.Plain;
import ru.rdude.rpg.game.utils.Functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapTilesFabric {

    private static Map<String, StaticTiledMapTile> tiles = new HashMap<>();
    private static TextureAtlas textureAtlas = new TextureAtlas("map_tiles.txt");;

    private static Texture tilesTexture = new Texture(Gdx.files.internal("map_tiles.png"));
    //private static StaticTiledMapTile test = new StaticTiledMapTile(new TextureRegion(tilesTexture));

    public static StaticTiledMapTile getTile(Cell cell) {

        if (cell.getBiom() == null) return getTileOrPutAndGet("VOID");

        if (cell.getBiom() == Water.getInstance()) {
            switch (cell.getDeepProperty()) {
                case DEEP:
                    return getTileOrPutAndGet("WATER_DEEP");
                case SMALL:
                    return getTileOrPutAndGet("WATER_SMALL");
                default:
                    return getTileOrPutAndGet("WATER");
            }
        }

        if (cell.getObject() != null) {
            return getTileOrPutAndGet("VOID");
        }

        if (cell.getRoad() != null)
            return getRoadTile(cell);

        return getTileOrPutAndGet(cell.getBiom().getClass().getSimpleName().toUpperCase());
    }


    private static StaticTiledMapTile getTileOrPutAndGet(String name) {
        if (!tiles.containsKey(name)) {
            StaticTiledMapTile tile = new StaticTiledMapTile(textureAtlas.findRegion(name));
            tiles.put(name, tile);
        }
        return tiles.get(name);
    }

    private static StaticTiledMapTile getRoadTile(Cell cell) {
        if (cell.getRoad().getDestinations().size() < 2)
            return getTileOrPutAndGet("VOID");
        List<CellSide> twoOf = cell.getRoad().getDestinations().stream()
                .limit(2)
                .collect(Collectors.toList());
        cell.getRoad().getDestinations().removeAll(twoOf);
        for (TextureAtlas.AtlasRegion region : textureAtlas.getRegions()) {
            if (region.name.contains(twoOf.get(0).name()) && region.name.contains(twoOf.get(1).name())) {
                return getTileOrPutAndGet(region.name);
            }
        }
        return getTileOrPutAndGet("VOID");
    }

}
