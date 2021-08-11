package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ru.rdude.rpg.game.logic.enums.Biom;
import ru.rdude.rpg.game.logic.enums.WaterDepth;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.GameMap;
import ru.rdude.rpg.game.logic.map.aStarImpl.MapMovingScorer;
import ru.rdude.rpg.game.logic.map.aStarImpl.MapPathFinder;
import ru.rdude.rpg.game.settings.GameSettings;
import ru.rdude.rpg.game.visual.GameStateStage;
import ru.rdude.rpg.game.visual.VisualBeing;

import java.util.*;
import java.util.stream.Collectors;

public class MapStage extends Stage implements GameStateStage {

    private final OrthographicCamera camera = new OrthographicCamera();
    private final GameMap gameMap;
    private final Map gameStateMap;
    private final MapVisual mapVisual;
    private final PlayersOnMap players;
    private final MonstersOnMap[][] monsters;

    private Cell selectedCell;
    private Queue<Cell> movingPath;
    private MapPathFinder pathFinder;

    // camera zooming
    private float timeToZoom = -1;
    private float zoomDuration = -1;
    private float zoomTarget = -1;
    private float zoomOrigin = -1;

    private final float CAM_MOVE_BORDER_BOTTOM = 0;
    private final float CAM_MOVE_BORDER_LEFT = 0;
    private final float CAM_MOVE_BORDER_RIGHT;
    private final float CAM_MOVE_BORDER_TOP;

    public MapStage(Map map) {
        super(new FitViewport(map.getGameMap().getWidth() * VisualConstants.TILE_WIDTH, map.getGameMap().getHeight() * VisualConstants.TILE_HEIGHT));
        this.gameMap = map.getGameMap();
        this.gameStateMap = map;
        this.monsters = new MonstersOnMap[map.getGameMap().getWidth()][map.getGameMap().getHeight()];

        // camera
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera.setToOrtho(false, w, h);
        camera.update();
        getViewport().setCamera(camera);
        // camera movement borders
        CAM_MOVE_BORDER_RIGHT = gameMap.getWidth() * VisualConstants.TILE_WIDTH_0_75;
        CAM_MOVE_BORDER_TOP = gameMap.getHeight() * VisualConstants.TILE_HEIGHT;

        // path finder
        pathFinder = new MapPathFinder(gameMap, new MapMovingScorer(map), (c1, c2) -> false);

        // map visual
        mapVisual = new MapVisual(camera, gameMap);
        addActor(mapVisual);
        // players on map
        players = new PlayersOnMap(map.getPlayerPosition());
        addActor(players);
        map.forEachCellProperties((cell, cellProperties) -> {
            if (cellProperties.isVisible()) {
                mapVisual.setVoidOnCell(cell, false);
                if (cellProperties.getMonsters() != null && !cellProperties.getMonsters().isEmpty()) {
                    final MonstersOnMap monstersOnCell = createMonstersOnCell(cell, cellProperties.getMonsters());
                    this.monsters[cell.getX()][cell.getY()] = monstersOnCell;
                }
            }
            cell.getAroundCells(1).forEach(c -> pathFinder.changeConnections(c, c.getAroundCells(1).stream()
                    .filter(c2 -> Game.getCurrentGame().getGameMap().isCellVisible(c2)
                            && (c2.getBiom() != Biom.WATER || c2.getWaterDepth() == WaterDepth.SMALL
                            || c2.getWaterDepth() == WaterDepth.RIVER))
                    .collect(Collectors.toSet())));
        });

        playerChangedPosition(map.getPlayerPosition(), map.getPlayerPosition());
        camera.position.set(players.getX(), players.getY(), 0);
    }

    @Override
    public OrthographicCamera getCamera() {
        return camera;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public MapVisual getMapVisual() {
        return mapVisual;
    }

    public void playerChangedPosition(Cell oldPosition, Cell newPosition) {
        newPosition.getArea(2, true).forEach(c -> {
            final Map gameMap = Game.getCurrentGame().getGameMap();
            if (this.gameMap == gameMap.getGameMap()) {
                mapVisual.setVoidOnCell(c, false);
                final Map.MonstersOnCell monsters = gameMap.cellMonsters(c);
                if (monsters != null && this.monsters[c.getX()][c.getY()] == null) {
                    final MonstersOnMap monstersOnMap = createMonstersOnCell(c, monsters);
                    this.monsters[c.getX()][c.getY()] = monstersOnMap;
                }
                pathFinder.changeConnections(c, c.getAroundCells(1).stream()
                        .filter(c2 -> gameMap.isCellVisible(c2)
                                && (c2.getBiom() != Biom.WATER || c2.getWaterDepth() == WaterDepth.SMALL
                                || c2.getWaterDepth() == WaterDepth.RIVER))
                        .collect(Collectors.toSet()));
            }
        });
        if (oldPosition != null) {
            mapVisual.removePath(oldPosition);
            mapVisual.removePath(newPosition);
        }
        final Map.MonstersOnCell monsters = Game.getCurrentGame().getGameMap().getCellProperties(newPosition).getMonsters();
        if (monsters != null && !monsters.isEmpty()) {
            Game.getGameStateSwitcher().switchToBattle(monsters, newPosition);
        }
    }

    private MonstersOnMap createMonstersOnCell(Cell cell, Map.MonstersOnCell monsters) {
        final MonstersOnMap monstersOnMap = new MonstersOnMap(monsters);
        addActor(monstersOnMap);
        players.toFront();
        monstersOnMap.setX((int) (cell.getX() * VisualConstants.TILE_WIDTH_0_75));
        int offset = cell.getX() % 2 == 0 ? (int) VisualConstants.TILE_HEIGHT_HALF : 0;
        monstersOnMap.setY(cell.getY() * VisualConstants.TILE_HEIGHT + offset);
        return monstersOnMap;
    }

    private void zoomTo(float newZoom, float duration) {
        zoomOrigin = camera.zoom;
        zoomTarget = newZoom;
        timeToZoom = zoomDuration = duration;
    }

    @Override
    public boolean scrolled(int amount) {
        if (Game.getGameVisual().isInGameMenuShown() || Game.getGameVisual().getUi().isHit()) {
            return super.scrolled(amount);
        }
        boolean res = super.scrolled(amount);
        if (amount > 0) {
            zoomTo(camera.zoom + (0.1f * camera.zoom), 0.15f);
        } else if (amount < 0) {
            if (camera.zoom > 1) {
                zoomTo(camera.zoom - (0.1f * camera.zoom), 0.15f);
                if (camera.zoom < 1) {
                    camera.zoom = 1;
                }
            }
        }
        return res;
    }

    @Override
    public void draw() {
        super.draw();
        act();
        camera.update();
    }

    private void lmbClicked() {
        if (Game.getGameVisual().isInGameMenuShown() || Game.getGameVisual().getUi().isHit()) {
            return;
        }
        if (players.isMoving()) {
            if (movingPath != null) {
                mapVisual.removePath(movingPath);
                movingPath.clear();
            }
        } else {
            mapVisual.cellFromWorld(mapVisual.getCursorWorldPosition()).ifPresent(cell -> {
                if (cell.equals(selectedCell)) {
                    Cell startCell = movingPath.poll();
                    players.moveByPath(movingPath);
                    if (startCell != null) {
                        mapVisual.removePath(startCell);
                    }
                } else {
                    if (movingPath != null) {
                        mapVisual.removePath(movingPath);
                    }

                    List<Cell> path = pathFinder.find(gameStateMap.getPlayerPosition(), cell).orElse(null);
                    if (path != null) {
                        movingPath = new LinkedList<>(path);
                        mapVisual.showPath(path);
                        selectedCell = path.get(path.size() - 1);
                    } else {
                        selectedCell = null;
                    }
                }
            });
        }
    }

    private void rmbClicked() {
        if (Game.getGameVisual().isInGameMenuShown() || Game.getGameVisual().getUi().isHit()) {
            return;
        }
    }

    @Override
    public void act() {
        super.act();
        // camera movement
        if (!Game.getGameVisual().isInGameMenuShown()) {
            if (Gdx.input.getY() > Gdx.graphics.getHeight() - 25 && camera.position.y > CAM_MOVE_BORDER_BOTTOM) {
                camera.translate(0, (Gdx.graphics.getHeight() - 25 - Gdx.input.getY()) / 2f * camera.zoom, 0);
            } else if (Gdx.input.getY() < 25 && camera.position.y < CAM_MOVE_BORDER_TOP) {
                camera.translate(0, (25 - Gdx.input.getY()) / 2f * camera.zoom);
            }
            if (Gdx.input.getX() < 25 && camera.position.x > CAM_MOVE_BORDER_LEFT) {
                camera.translate((Gdx.input.getX() - 25) / 2f * camera.zoom, 0);
            } else if (Gdx.input.getX() > Gdx.graphics.getWidth() - 25 && camera.position.x < CAM_MOVE_BORDER_RIGHT) {
                camera.translate((25 - (Gdx.graphics.getWidth() - Gdx.input.getX())) / 2f * camera.zoom, 0);
            }
        }
        // camera zooming
        if (timeToZoom >= 0) {
            timeToZoom -= Gdx.graphics.getDeltaTime();
            float progress = timeToZoom < 0 ? 1 : 1f - timeToZoom / zoomDuration;
            camera.zoom = Interpolation.pow3Out.apply(zoomOrigin, zoomTarget, progress);
        }
        // moving camera with players
        if (GameSettings.isCameraFollowPlayers() && players.isMoving()) {
            camera.position.set(players.getX(), players.getY(), 0);
        }
        // click
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            lmbClicked();
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            rmbClicked();
        }
    }

    @Override
    public List<VisualBeing<?>> getVisualBeings() {
        return new ArrayList<>(Game.getGameVisual().getUi().getPlayerVisuals());
    }
}
