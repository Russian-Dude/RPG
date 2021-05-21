package ru.rdude.rpg.game.logic.gameStates;

import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.GameMap;
import ru.rdude.rpg.game.logic.map.PlaceObserver;
import ru.rdude.rpg.game.logic.time.TimeForMovingCalculator;
import ru.rdude.rpg.game.mapVisual.MapStage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Map extends GameStateBase {

    private final GameMap gameMap;
    private MapStage mapStage;
    private final HashMap<Cell, CellProperties> cellProperties = new HashMap();
    private Cell playerPosition;
    private Set<PlaceObserver> placePositionSubscribers = new HashSet<>();

    public Map(GameMap gameMap) {
        this.gameMap = gameMap;
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                cellProperties.put(gameMap.cell(x, y), new CellProperties(gameMap.cell(x, y)));
            }
        }
    }

    public MapStage createStage() {
        this.mapStage = new MapStage(this);
        return this.mapStage;
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
        notifySubscribers(cellProperties.get(oldPosition), cellProperties.get(playerPosition));
        Game.getCurrentGame().getTimeManager()
                .increaseTime(TimeForMovingCalculator.calculate(cellProperties.get(oldPosition), cellProperties.get(playerPosition)));
    }

    public void placePlayerOnStartPosition() {
        this.playerPosition = gameMap.cell(gameMap.getStartPoint());
        playerPosition.getArea(2, true).forEach(cell -> cellProperties.get(cell).setVisible(true));
    }

    public boolean cellHasMonster(Cell cell) {
        return cellProperties.get(cell).monsters != null;
    }

    public Party cellMonsters(Cell cell) {
        return cellProperties.get(cell).monsters;
    }

    public void subscribe(PlaceObserver subscriber) {
        placePositionSubscribers.add(subscriber);
    }

    private void notifySubscribers(CellProperties oldPosition, CellProperties newPosition) {
        placePositionSubscribers.forEach(subscriber -> subscriber.update(oldPosition, newPosition));
    }

    public class CellProperties {

        final Cell cell;
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

        public Cell getCell() {
            return cell;
        }
    }
}
