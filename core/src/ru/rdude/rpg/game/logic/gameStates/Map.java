package ru.rdude.rpg.game.logic.gameStates;

import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.GameMap;
import ru.rdude.rpg.game.mapVisual.MapStage;

import java.util.HashMap;

public class Map extends GameStateBase {

    private final GameMap gameMap;
    private final MapStage mapStage;
    private final HashMap<Cell, CellProperties> cellProperties = new HashMap();
    private Cell playerPosition;

    public Map(GameMap gameMap) {
        this.gameMap = gameMap;
        this.mapStage = new MapStage(this);
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                cellProperties.put(gameMap.cell(x, y), new CellProperties(gameMap.cell(x, y)));
            }
        }
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public MapStage getMapStage() {
        return mapStage;
    }

    public HashMap<Cell, CellProperties> getCellProperties() {
        return cellProperties;
    }

    public Cell getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerPosition(Cell playerPosition) {
        Cell oldPosition = this.playerPosition;
        this.playerPosition = playerPosition;
        playerPosition.getArea(2, true).forEach(cell -> cellProperties.get(cell).setVisible(true));
        mapStage.playerChangedPosition(oldPosition, playerPosition);
    }

    public boolean cellHasMonster(Cell cell) {
        return cellProperties.get(cell).monsters != null;
    }

    public Party cellMonsters(Cell cell) {
        return cellProperties.get(cell).monsters;
    }

    public class CellProperties {

        Cell cell;
        Party monsters;
        boolean visible = false;

        public CellProperties(Cell cell) {
            this.cell = cell;
        }

        public Party getMonsters() {
            return monsters;
        }

        public void setMonsters(Party monsters) {
            this.monsters = monsters;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }
}
