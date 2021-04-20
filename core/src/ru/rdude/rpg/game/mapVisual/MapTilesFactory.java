package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.CellSide;
import ru.rdude.rpg.game.logic.map.bioms.BiomCellProperty;
import ru.rdude.rpg.game.logic.map.bioms.Water;
import ru.rdude.rpg.game.utils.Functions;

import java.util.*;
import java.util.stream.Collectors;

public class MapTilesFactory {

    private static Map<String, StaticTiledMapTile> tiles = new HashMap<>();
    private static final TextureAtlas textureAtlas = new TextureAtlas("map_tiles.txt");

    private static Set<TextureAtlas.AtlasRegion> roadAtlasRegions;
    private static Set<TextureAtlas.AtlasRegion> pathAtlasRegions;

    static {
        fillRoadAndPathAtlasRegionsSet();
    }


    public static TiledMapTile getBiomTile(Cell cell) {
        BiomCellProperty biom = cell.getBiom();
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

    public static TiledMapTile getReliefTile(Cell cell, CellSide side, boolean isFullLeftOrRightSide) {
        String sideSuffix;
        if (isFullLeftOrRightSide)
            sideSuffix = "SIDE";
        else if (side == CellSide.NN || side == CellSide.SS)
            sideSuffix = "HALF";
        else
            sideSuffix = "QUARTER";
        return getTileOrPutAndGet(
                cell.getBiom().getClass().getSimpleName().toUpperCase()
                        + "_"
                        + cell.getRelief().getClass().getSimpleName().toUpperCase()
                        + "_"
                        + sideSuffix
                        + Functions.random(1, 3),
                side);
    }

    public static TiledMapTile getRoadTile(Cell cell, CellSide directionFrom, CellSide directionTo) {
        String roadType;
        if (cell.getBiom() == Water.getInstance()) {
            // TODO: 19.04.2021 here must be implemented logic to use empty tile if road is pseudo-road in deep waters
            roadType = "BRIDGE";
        } else {
            roadType = "ROAD";
        }
        List<TextureAtlas.AtlasRegion> collect = roadAtlasRegions.stream()
                .filter(atlasRegion -> atlasRegion.name.contains(directionFrom.name())
                        && atlasRegion.name.contains(directionTo.name())
                        && atlasRegion.name.contains(roadType))
                .collect(Collectors.toList());
        if (collect.isEmpty()) {
            throw new IllegalArgumentException("There is no road image with directions: " + directionFrom + " to " + directionTo + ", type: " + roadType);
        }
        TextureAtlas.AtlasRegion region = Functions.random(collect);
        return getTileOrPutAndGet(region.name);
    }

    public static TiledMapTile getPathTile(CellSide directionFrom, CellSide directionTo) {
        TextureAtlas.AtlasRegion region = pathAtlasRegions.stream()
                .filter(atlasRegion -> atlasRegion.name.contains(directionFrom.name()) && atlasRegion.name.contains(directionTo.name()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("There is no path image with directions: " + directionFrom + " to " + directionTo));
        return getTileOrPutAndGet(region.name);
    }

    public static TiledMapTile getPathEndPoint() {
        return getTileOrPutAndGet("WALKING_POINT");
    }

    public static TiledMapTile getVoid() {
        return getTileOrPutAndGet("VOID");
    }

    public static TiledMapTile getEmpty() {
        return getTileOrPutAndGet("EMPTY");
    }

    public static TextureRegion getAvatar(int partySize) {
        return textureAtlas.findRegion("MAP_AVATAR" + Math.min(partySize, 5));
    }

    private static void fillRoadAndPathAtlasRegionsSet() {
        roadAtlasRegions = new HashSet<>();
        pathAtlasRegions = new HashSet<>();
        Array.ArrayIterator<TextureAtlas.AtlasRegion> atlasRegions = new Array.ArrayIterator<>(textureAtlas.getRegions());
        List<String> directionNames = Arrays.stream(CellSide.values()).map(Enum::name).collect(Collectors.toList());
        while (atlasRegions.hasNext()) {
            TextureAtlas.AtlasRegion region = atlasRegions.next();
            for (String directionName : directionNames) {
                if ((region.name.contains("ROAD") || region.name.contains("BRIDGE")) && region.name.contains(directionName)) {
                    roadAtlasRegions.add(region);
                }
                else if (region.name.contains("WALKING") && region.name.contains(directionName)) {
                    pathAtlasRegions.add(region);
                }
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
