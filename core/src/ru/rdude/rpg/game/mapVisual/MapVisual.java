package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Disposable;
import ru.rdude.rpg.game.logic.actions.IncrementAction;
import ru.rdude.rpg.game.logic.enums.Biom;
import ru.rdude.rpg.game.logic.enums.Relief;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.CellSide;
import ru.rdude.rpg.game.logic.map.GameMap;
import ru.rdude.rpg.game.logic.map.objects.City;

import java.util.*;

public class MapVisual extends Actor implements Disposable {

    private final OrthographicCamera camera;

    private final TiledMap map;
    private final MapRenderer renderer;

    private final GameMap gameMap;

    private final Vector3 cursorWorldPosition = new Vector3();

    private final TiledMapTileLayer biomLayer;
    private final List<TiledMapTileLayer> roadLayers;
    private final TiledMapTileLayer pathLayer;
    private final TiledMapTileLayer pointLayer;
    private final TiledMapTileLayer voidLayer;
    private final TiledMapTileLayer monstersLayer;

    private final TiledMapTileLayer reliefLayerBehind;
    private final TiledMapTileLayer reliefLayerFront;

    private float[][] cellsOpacity;

    public MapVisual(OrthographicCamera camera, GameMap gameMap) {
        this.camera = camera;
        this.gameMap = gameMap;
        this.cellsOpacity = new float[gameMap.getWidth()][gameMap.getHeight()];
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                cellsOpacity[x][y] = 0f;
            }
        }
        map = new TiledMap();
        MapLayers layers = map.getLayers();

        biomLayer = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), VisualConstants.TILE_WIDTH, VisualConstants.TILE_HEIGHT);
        reliefLayerBehind = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), VisualConstants.TILE_WIDTH, VisualConstants.TILE_HEIGHT);
        reliefLayerFront = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), VisualConstants.TILE_WIDTH, VisualConstants.TILE_HEIGHT);
        roadLayers = new ArrayList<>();
        pathLayer = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), VisualConstants.TILE_WIDTH, VisualConstants.TILE_HEIGHT);
        pointLayer = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), VisualConstants.TILE_WIDTH, VisualConstants.TILE_HEIGHT);
        monstersLayer = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), VisualConstants.TILE_WIDTH, VisualConstants.TILE_HEIGHT);
        voidLayer = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), VisualConstants.TILE_WIDTH, VisualConstants.TILE_HEIGHT);

        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                Cell gameMapCell = gameMap.cell(x, y);

                //biom
                MapTileCell biomCell = new MapTileCell(x, y);
                biomCell.setTile(MapTilesFactory.getBiomTile(gameMapCell));
                biomLayer.setCell(x, y, biomCell);

                //road
                if (gameMapCell.getRoad() != null && gameMapCell.getRoad().isRealRoad()) {

                    List<TiledMapTile> tiles = new ArrayList<>();
                    Queue<CellSide> queue = new LinkedList<>(gameMapCell.getRoad().getDestinations());
                    while (!queue.isEmpty()) {
                        CellSide dest1 = queue.poll();
                        CellSide dest2 = gameMapCell.getRoad().getDestinations().stream()
                                .filter(d -> d != dest1 && !dest1.isCloseTo(d))
                                .findAny()
                                .orElse(gameMapCell.getRoad().getDestinations().stream()
                                        .filter(d -> d != dest1)
                                        .findAny()
                                        .orElseThrow(() -> new IllegalStateException("Something went wrong in creating road tiles")));
                        queue.remove(dest2);
                        tiles.add(MapTilesFactory.getRoadTile(gameMapCell, dest1, dest2));
                    }

                    for (int i = 0; i < tiles.size(); i++) {
                        if (roadLayers.size() < i + 1) {
                            roadLayers.add(new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), VisualConstants.TILE_WIDTH, VisualConstants.TILE_HEIGHT));
                        }
                        MapTileCell roadCell = new MapTileCell(x, y);
                        roadCell.setTile(tiles.get(i));
                        roadLayers.get(i).setCell(x, y, roadCell);
                    }
                }

                // void
                MapTileCell voidCell = new MapTileCell(x, y);
                voidCell.setTile(MapTilesFactory.getVoid());
                voidLayer.setCell(x, y, voidCell);

                // relief
                createReliefTileOn(gameMapCell);

                // path
                MapTileCell pathCell = new MapTileCell(x, y);
                MapTileCell pointCell = new MapTileCell(x, y);
                pathCell.setTile(MapTilesFactory.getEmpty());
                pointCell.setTile(MapTilesFactory.getEmpty());
                pathLayer.setCell(x, y, pathCell);
                pointLayer.setCell(x, y, pointCell);
            }
        }

        // add layers to tilemap
        layers.add(biomLayer);
        roadLayers.forEach(layers::add);
        layers.add(voidLayer);
        layers.add(reliefLayerBehind);
        layers.add(reliefLayerFront);
        layers.add(monstersLayer);
        layers.add(pathLayer);
        layers.add(pointLayer);
        setBounds(getX(), getY(), gameMap.getWidth() * 128, gameMap.getHeight() * 128);
        setTouchable(Touchable.enabled);

        renderer = new MapRenderer(map);
        this.setOrigin(0f, 0f);
    }

    // to let relief overlap void for better picture, void and relief is on the same layer.
    public void setVoidOnCell(Cell cell, boolean isVoid) {
        final int x = cell.getX();
        final int y = cell.getY();
        if (isVoid) {
            cellsOpacity[x][y] = 0f;
        }
        else {
            final IncrementAction incrementAction = Actions.action(IncrementAction.class);
            incrementAction.setFrom(0f);
            incrementAction.setTo(1f);
            incrementAction.setDuration(0.2f);
            incrementAction.setValueSetter(o -> cellsOpacity[x][y] = Math.max(cellsOpacity[x][y], o));
            Game.getGameVisual().addAction(incrementAction);
        }
    }

    public void showPath(List<Cell> path) {
        for (int i = 0; i < path.size(); i++) {
            CellSide from = i == 0 ? CellSide.CENTER : path.get(i).getRelativeLocation(path.get(i - 1));
            CellSide to = i == path.size() - 1 ? CellSide.CENTER : path.get(i).getRelativeLocation(path.get(i + 1));
            pathLayer.getCell(path.get(i).getX(), path.get(i).getY()).setTile(MapTilesFactory.getPathTile(from, to));
        }
        pointLayer.getCell(path.get(path.size() - 1).getX(), path.get(path.size() - 1).getY()).setTile(MapTilesFactory.getPathEndPoint());
    }

    public void removePath(Collection<Cell> path) {
        path.forEach(this::removePath);
    }

    public void removePath(Cell cell) {
        pathLayer.getCell(cell.getX(), cell.getY()).setTile(MapTilesFactory.getEmpty());
        pointLayer.getCell(cell.getX(), cell.getY()).setTile(MapTilesFactory.getEmpty());
    }

    public float getCellsOpacity(MapLayer layer, int x, int y) {
        float opacity;
        if (layer == voidLayer) {
            final float otherOpacity = cellsOpacity[x][y];
            opacity = 1f - otherOpacity;
        }
        else if (layer == pathLayer || layer == pointLayer) {
            opacity = 1f;
        }
        else {
            opacity = cellsOpacity[x][y];
        }
        return opacity;
    }

    private void createReliefTileOn(Cell cell) {

        MapTileCell frontCell = new MapTileCell(cell.getX(), cell.getY());
        MapTileCell behindCell = new MapTileCell(cell.getX(), cell.getY());
        reliefLayerFront.setCell(cell.getX(), cell.getY(), frontCell);
        reliefLayerBehind.setCell(cell.getX(), cell.getY(), behindCell);

        if (cell.getObject() instanceof City) {
            TiledMapTile cityTile = MapTilesFactory.getCity();
            reliefLayerFront.getCell(cell.getX(), cell.getY()).setTile(cityTile);
            reliefLayerBehind.getCell(cell.getX(), cell.getY()).setTile(MapTilesFactory.getEmpty());
            return;
        }

        // empty tile on water and plains
        if (cell.getBiom() == Biom.WATER || cell.getRelief() == Relief.PLAIN) {
            TiledMapTile emptyTile = MapTilesFactory.getEmpty();
            reliefLayerBehind.getCell(cell.getX(), cell.getY()).setTile(emptyTile);
            return;
        }

        // full tile
        if (cell.getRoad() == null && cell.getObject() == null) {
            TiledMapTile reliefTile = MapTilesFactory.getReliefTile(cell);
            reliefLayerFront.getCell(cell.getX(), cell.getY()).setTile(reliefTile);
            reliefLayerBehind.getCell(cell.getX(), cell.getY()).setTile(MapTilesFactory.getEmpty());
            return;
        }

        // not full tile
        Set<CellSide> freePositions = new HashSet<>(Set.of(CellSide.values()));
        // do not place relief on part of the tile with road
        if (cell.getRoad() != null) {
            freePositions.removeAll(cell.getRoad().getDestinations());
        }

        TiledMapTile frontLayerTile = null;
        TiledMapTile behindLayerTile = null;

        // relief on cell top (bottom layer)
        if (freePositions.contains(CellSide.NW)
        && freePositions.contains(CellSide.NN)
        && freePositions.contains(CellSide.NE)) {
            behindLayerTile = MapTilesFactory.getReliefTile(cell, false, CellSide.NN, false);
        }

        // relief on cell bottom (top layer)
        if (freePositions.contains(CellSide.SW)
        && freePositions.contains(CellSide.SS)
        && freePositions.contains(CellSide.SE)) {
            frontLayerTile = MapTilesFactory.getReliefTile(cell, true, CellSide.SS, false);
        }

        // relief on the left
        // full left
        if (behindLayerTile == null
        && frontLayerTile == null
        && freePositions.contains(CellSide.NW)
        && freePositions.contains(CellSide.SW)) {
            frontLayerTile = MapTilesFactory.getReliefTile(cell, true, CellSide.SW, true);
        }
        // SW
        else if (frontLayerTile == null
        && freePositions.contains(CellSide.SW)) {
            frontLayerTile = MapTilesFactory.getReliefTile(cell, true, CellSide.SW, false);
        }
        // NW
        else if (behindLayerTile == null
        && freePositions.contains(CellSide.NW)) {
            behindLayerTile = MapTilesFactory.getReliefTile(cell, false, CellSide.NW, false);
        }

        // relief on the right
        // full right
        if (behindLayerTile == null
        && frontLayerTile == null
        && freePositions.contains(CellSide.NE)
        && freePositions.contains(CellSide.SE)) {
            frontLayerTile = MapTilesFactory.getReliefTile(cell, true, CellSide.SE, true);
        }
        // NE
        else if (behindLayerTile == null
        && freePositions.contains(CellSide.NE)) {
            behindLayerTile = MapTilesFactory.getReliefTile(cell, false, CellSide.NE, false);
        }
        // SE
        else if (frontLayerTile == null
        && freePositions.contains(CellSide.SE)) {
            frontLayerTile = MapTilesFactory.getReliefTile(cell, true, CellSide.SE, false);
        }

        frontLayerTile = frontLayerTile == null ? MapTilesFactory.getEmpty() : frontLayerTile;
        behindLayerTile = behindLayerTile == null ? MapTilesFactory.getEmpty() : behindLayerTile;

        reliefLayerFront.getCell(cell.getX(), cell.getY()).setTile(frontLayerTile);
        reliefLayerBehind.getCell(cell.getX(), cell.getY()).setTile(behindLayerTile);
    }

    public Vector3 getCursorWorldPosition() {
        return cursorWorldPosition;
    }

    public Vector3 screenToWorld(Vector3 click) {
        return camera.unproject(click);
    }

    public Optional<Cell> cellFromClick(Vector3 click) {
        return cellFromWorld(screenToWorld(click));
    }

    public Optional<Cell> cellFromWorld(Vector3 pos) {

        // tile cell rough
        int x = (int) Math.floor(pos.x / VisualConstants.TILE_WIDTH_0_75);
        float offset = x % 2 == 0 ? VisualConstants.TILE_HEIGHT_HALF : 0;
        int y = (int) Math.floor((pos.y - offset) / VisualConstants.TILE_HEIGHT);

        // tile cell precise
        float startX = x * VisualConstants.TILE_WIDTH_0_75;
        float startY = y * VisualConstants.TILE_HEIGHT + offset;

        float topX = startX + VisualConstants.TILE_WIDTH_QUARTER;
        float topY = startY + VisualConstants.TILE_HEIGHT;

        float middleX = startX;
        float middleY = startY + VisualConstants.TILE_HEIGHT_HALF;

        float bottomX = topX;
        float bottomY = startY;

        boolean left = pos.x < topX;
        boolean top = pos.y >= startY + VisualConstants.TILE_HEIGHT_HALF;

        if (left) {
            if (top) {
                float m = (topY - middleY) / (topX - startX);
                float lineY = middleY + m * (pos.x - startX);
                if (pos.y > lineY) {
                    x = x - 1;
                    y = x % 2 == 0 ? y : y + 1;
                }
            } else {
                float m = (middleY - bottomY) / (middleX - bottomX);
                float lineY = bottomY + m * (pos.x - bottomX);
                if (pos.y < lineY) {
                    x = x - 1;
                    y = x % 2 == 0 ? y - 1 : y;
                }
            }
        }

        if (x >= gameMap.getWidth() || x < 0 || y >= gameMap.getHeight() || y < 0) {
            return Optional.empty();
        }
        return Optional.of(gameMap.cell(x, y));
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

    @Override
    public void act(float delta) {
        super.act(delta);
        // cursor position
        cursorWorldPosition.x = Gdx.input.getX();
        cursorWorldPosition.y = Gdx.input.getY();
        screenToWorld(cursorWorldPosition);
    }
}
