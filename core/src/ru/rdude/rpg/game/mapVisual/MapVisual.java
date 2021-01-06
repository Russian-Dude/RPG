package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
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


    public MapVisual(OrthographicCamera camera, GameMap gameMap) {
        this.camera = camera;
        map = new TiledMap();
        MapLayers layers = map.getLayers();

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
                        CellSide dest2 = queue.size() % 2 != 0 ?  queue.poll() : queue.peek();

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
                        }
                        else if (freePositions.contains(CellSide.NE) && freePositions.contains(CellSide.SE)) {
                            upTile = MapTilesFactory.getReliefTile(gameMapCell, CellSide.SE, true);
                        }
                        else if (freePositions.contains(CellSide.NW) && freePositions.contains(CellSide.SW)) {
                            upTile = MapTilesFactory.getReliefTile(gameMapCell, CellSide.SW, true);
                        }


                        else {
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

        renderer = new HexagonalTiledMapRendererWithObjectsLayer(map, 0.1f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        renderer.setView(camera);
        renderer.render();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        map.dispose();
    }
}
