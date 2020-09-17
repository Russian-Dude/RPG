package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.CellSide;
import ru.rdude.rpg.game.logic.map.Generator;
import ru.rdude.rpg.game.logic.map.bioms.Biom;
import ru.rdude.rpg.game.logic.map.bioms.Water;
import ru.rdude.rpg.game.utils.Functions;

import java.util.*;
import java.util.stream.Collectors;

public class MapTilesFactory {

    private static Map<String, StaticTiledMapTile> tiles = new HashMap<>();
    private static TextureAtlas textureAtlas = new TextureAtlas("map_tiles.txt");

    private static Set<TextureAtlas.AtlasRegion> roadAtlasRegions;


    public static TiledMapTile getBiomTile(Cell cell) {
        Biom biom = cell.getBiom();
        if (biom == Water.getInstance()) {
            switch (cell.getDeepProperty()) {
                case DEEP:
                    return getTileOrPutAndGet("WATER_DEEP");
                case SMALL:
                case RIVER:
                    return getTileOrPutAndGet("WATER_SMALL");
                default:
                    return getTileOrPutAndGet("WATER");
            }
        }
        return getTileOrPutAndGet(cell.getBiom().getClass().getSimpleName().toUpperCase());
    }

    public static TiledMapTile getReliefTile(Cell cell) {
        TiledMapTile tile = getTileOrPutAndGet(
                cell.getBiom().getClass().getSimpleName().toUpperCase()
                        + "_"
                        + cell.getRelief().getClass().getSimpleName().toUpperCase()
                        + "_FULL"
                        + Functions.random(1, 3));
        tile.setOffsetX(- 64);
        tile.setOffsetY( - 64);
        return tile;
    }

    public static TiledMapTile getReliefTile(Cell cell, CellSide side, boolean isFullSide) {
        String sideSuffix;
        if (isFullSide)
            sideSuffix = "SIDE";
        else if (side == CellSide.NN || side == CellSide.SS)
            sideSuffix = "HALF";
        else
            sideSuffix = "QUARTER";
        TiledMapTile tile = getTileOrPutAndGet(
                cell.getBiom().getClass().getSimpleName().toUpperCase()
                        + "_"
                        + cell.getRelief().getClass().getSimpleName().toUpperCase()
                        + "_"
                        + sideSuffix
                        + Functions.random(1, 3),
                side);
        tile.setOffsetX(tile.getOffsetX() - 64);
        tile.setOffsetY(tile.getOffsetY() - 64);
        return tile;
    }

    public static TiledMapTile getRoadTile(Cell cell, CellSide directionFrom, CellSide directionTo) {
        if (roadAtlasRegions == null) {
            fillRoadAtlasRegionsSet();
        }
        String roadType;
        if (cell.getBiom() == Water.getInstance()) {
            // here must be implemented logic to use empty tile if road is pseudo-road in deep waters
            roadType = "BRIDGE";
        } else {
            roadType = "ROAD";
        }
        TextureAtlas.AtlasRegion region = roadAtlasRegions.stream()
                .filter(atlasRegion -> atlasRegion.name.contains(directionFrom.name())
                        && atlasRegion.name.contains(directionTo.name())
                        && atlasRegion.name.contains(roadType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No image to represent road ffs!"));
        return getTileOrPutAndGet(region.name);
    }

    private static void fillRoadAtlasRegionsSet() {
        roadAtlasRegions = new HashSet<>();
        Array.ArrayIterator<TextureAtlas.AtlasRegion> atlasRegions = new Array.ArrayIterator<>(textureAtlas.getRegions());
        List<String> directionNames = Arrays.stream(CellSide.values()).map(Enum::name).collect(Collectors.toList());
        while (atlasRegions.hasNext()) {
            TextureAtlas.AtlasRegion region = atlasRegions.next();
            for (String directionName : directionNames) {
                if ((region.name.contains("ROAD") || region.name.contains("BRIDGE")) && region.name.contains(directionName))
                    roadAtlasRegions.add(region);
            }
        }
    }

    private static TiledMapTile getTileOrPutAndGet(String name) {
        if (!tiles.containsKey(name)) {
            StaticTiledMapTile tile = new StaticTiledMapTile(textureAtlas.findRegion(name));
            tiles.put(name, tile);
        }
        return tiles.get(name);
    }

    private static TiledMapTile getTileOrPutAndGet(String name, CellSide side) {
        if (!tiles.containsKey(name + side)) {
            StaticTiledMapTile tile = new StaticTiledMapTile(textureAtlas.findRegion(name));
            tiles.put(name + side, tile);
            // default position for quarter tile - SW, for half-vertical tile - SS
            switch (side) {
                case SE:
                    tile.setOffsetX(64);
                    break;
                case NW:
                case NN:
                    tile.setOffsetY(64);
                    break;
                case NE:
                    tile.setOffsetX(64);
                    tile.setOffsetY(64);
                    break;
            }
        }
        return tiles.get(name + side);
    }


}
