package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Disposable;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.CellSide;
import ru.rdude.rpg.game.logic.map.GameMap;
import ru.rdude.rpg.game.logic.map.bioms.Water;
import ru.rdude.rpg.game.logic.map.reliefs.Plain;

import java.util.*;

public class MapVisual extends Actor implements Disposable {

    private OrthographicCamera camera;

    private TiledMap map;
    private HexagonalTiledMapRenderer renderer;

    private GameMap gameMap;
    private TextureAtlas textureAtlasTest;

    private final float CAM_MOVE_BORDER_BOTTOM = 0;
    private final float CAM_MOVE_BORDER_LEFT = 0;
    private final float CAM_MOVE_BORDER_RIGHT;
    private final float CAM_MOVE_BORDER_TOP;

    public MapVisual(OrthographicCamera camera, GameMap gameMap) {
        this.camera = camera;
        this.gameMap = gameMap;
        map = new TiledMap();
        MapLayers layers = map.getLayers();

        CAM_MOVE_BORDER_RIGHT = gameMap.getWidth() * VisualConstants.TILE_WIDTH_0_75;
        CAM_MOVE_BORDER_TOP = gameMap.getHeight() * VisualConstants.TILE_HEIGHT;

        TiledMapTileLayer biomLayer = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), 128, 128);

        TiledMapTileLayer reliefLayerBottom = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), 128, 128);
        TiledMapTileLayer reliefLayerUp = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), 128, 128);

        List<TiledMapTileLayer> roadLayers = new ArrayList<>();

        for (int x = 0; x < gameMap.getWidth() - 1; x++) {
            for (int y = 0; y < gameMap.getHeight() - 1; y++) {
                Cell gameMapCell = gameMap.cell(x, y);

                //biom
                TiledMapTileLayer.Cell biomCell = new TiledMapTileLayer.Cell();
                biomCell.setTile(MapTilesFactory.getBiomTile(gameMapCell));
                biomLayer.setCell(x, y, biomCell);

                //road
                if (gameMapCell.getRoad() != null) {

                    List<CellSide> destinations = new ArrayList<>(gameMapCell.getRoad().getDestinationsCopy());
                    Queue<CellSide> queue = new LinkedList<>(destinations);
                    Set<CellSide> used = new HashSet<>();
                    List<TiledMapTile> tiles = new ArrayList<>();


                    while (queue.size() > 1) {
                        System.out.println(queue);

                        CellSide dest1 = queue.poll();
                        CellSide dest2 = queue.size() % 2 != 0 ? queue.poll() : queue.peek();

                        if (dest1.isCloseTo(dest2)) {
                            CellSide destPick = used.contains(dest1) ? dest2 : dest1;
                            dest1 = destPick;
                            for (CellSide destination : destinations) {
                                if (!destination.isCloseTo(destPick))
                                    dest2 = destination;
                            }
                        }


                        tiles.add(MapTilesFactory.getRoadTile(gameMapCell, dest1, dest2));
                        used.add(dest1);
                        used.add(dest2);
                    }

                    System.out.println("=========");


                    for (int i = 0; i < tiles.size(); i++) {
                        if (roadLayers.size() < i + 1) {
                            roadLayers.add(new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), 128, 128));
                        }
                        TiledMapTileLayer.Cell roadCell = new TiledMapTileLayer.Cell();
                        roadCell.setTile(tiles.get(i));
                        roadLayers.get(i).setCell(x, y, roadCell);
                    }
                }

                // relief
                if (gameMapCell.getBiom() != Water.getInstance() && gameMapCell.getRelief() != Plain.getInstance()) {

                    // full tile
                    if (gameMapCell.getRoad() == null && gameMapCell.getObject() == null) {
                        TiledMapTileLayer.Cell reliefCell = new TiledMapTileLayer.Cell();
                        reliefCell.setTile(MapTilesFactory.getReliefTile(gameMapCell));
                        reliefLayerUp.setCell(x, y, reliefCell);
                    }

                    // not full tile
                    else {
                        TiledMapTile bottomTile = null;
                        TiledMapTile upTile = null;

                        Set<CellSide> freePositions = new HashSet<>(Set.of(CellSide.values()));
                        if (gameMapCell.getRoad() != null)
                            freePositions.removeAll(gameMapCell.getRoad().getDestinations());
                        if (gameMapCell.getObject() != null)
                            freePositions.removeAll(gameMapCell.getObject().getPositions());

                        // sides
                        if (freePositions.contains(CellSide.SE)
                                && freePositions.contains(CellSide.SW)
                                && freePositions.contains(CellSide.NE)
                                && freePositions.contains(CellSide.NW)
                                && freePositions.contains(CellSide.CENTER)) {
                            upTile = MapTilesFactory.getReliefTile(gameMapCell, CellSide.SE, true);
                            bottomTile = MapTilesFactory.getReliefTile(gameMapCell, CellSide.SW, true);
                        } else if (freePositions.contains(CellSide.NE) && freePositions.contains(CellSide.SE)) {
                            upTile = MapTilesFactory.getReliefTile(gameMapCell, CellSide.SE, true);
                        } else if (freePositions.contains(CellSide.NW) && freePositions.contains(CellSide.SW)) {
                            upTile = MapTilesFactory.getReliefTile(gameMapCell, CellSide.SW, true);
                        } else {
                            // bottom
                            if (freePositions.contains(CellSide.SS) && freePositions.contains(CellSide.SE) && freePositions.contains(CellSide.SW)) {
                                bottomTile = MapTilesFactory.getReliefTile(gameMapCell, CellSide.SS, false);
                            } else if (freePositions.contains(CellSide.SW)) {
                                bottomTile = MapTilesFactory.getReliefTile(gameMapCell, CellSide.SW, false);
                            } else if (freePositions.contains(CellSide.SE)) {
                                bottomTile = MapTilesFactory.getReliefTile(gameMapCell, CellSide.SE, false);
                            }

                            // up
                            if (freePositions.contains(CellSide.NN) && freePositions.contains(CellSide.NE) && freePositions.contains(CellSide.NW)) {
                                upTile = MapTilesFactory.getReliefTile(gameMapCell, CellSide.NN, false);
                            } else if (freePositions.contains(CellSide.NW)) {
                                upTile = MapTilesFactory.getReliefTile(gameMapCell, CellSide.NW, false);
                            } else if (freePositions.contains(CellSide.NE)) {
                                upTile = MapTilesFactory.getReliefTile(gameMapCell, CellSide.NE, false);
                            }
                        }

                        if (upTile != null) {
                            TiledMapTileLayer.Cell upReliefCell = new TiledMapTileLayer.Cell();
                            upReliefCell.setTile(upTile);
                            reliefLayerUp.setCell(x, y, upReliefCell);
                        }
                        if (bottomTile != null) {
                            TiledMapTileLayer.Cell bottomReliefCell = new TiledMapTileLayer.Cell();
                            bottomReliefCell.setTile(bottomTile);
                            reliefLayerBottom.setCell(x, y, bottomReliefCell);
                        }
                    }
                }
            }
        }
        layers.add(biomLayer);
        roadLayers.forEach(layers::add);
        layers.add(reliefLayerBottom);
        layers.add(reliefLayerUp);
        setBounds(getX(), getY(), gameMap.getWidth() * 128, gameMap.getHeight() * 128);
        setTouchable(Touchable.enabled);

        renderer = new HexagonalTiledMapRendererWithObjectsLayer(map);
        this.setOrigin(0f, 0f);

    }

    private Vector3 screenToWorld(Vector3 click) {
        return camera.unproject(click);
    }

    private Optional<Cell> cellFromClick(Vector3 click) {
        return cellFromWorld(screenToWorld(click));
    }

    private Optional<Cell> cellFromWorld(Vector3 pos) {

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
            }
            else {
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
        // camera movement
        if (Gdx.input.getY() > Gdx.graphics.getHeight() - 25 && camera.position.y > CAM_MOVE_BORDER_BOTTOM) {
            camera.translate(0, (Gdx.graphics.getHeight() - 25 - Gdx.input.getY()) / 2f, 0);
        } else if (Gdx.input.getY() < 25 && camera.position.y < CAM_MOVE_BORDER_TOP) {
            camera.translate(0, (25 - Gdx.input.getY()) / 2f);
        }
        if (Gdx.input.getX() < 25 && camera.position.x > CAM_MOVE_BORDER_LEFT) {
            camera.translate((Gdx.input.getX() - 25) / 2f, 0);
        } else if (Gdx.input.getX() > Gdx.graphics.getWidth() - 25 && camera.position.x < CAM_MOVE_BORDER_RIGHT) {
            camera.translate((25 - (Gdx.graphics.getWidth() - Gdx.input.getX())) / 2f, 0);
        }
    }
}
