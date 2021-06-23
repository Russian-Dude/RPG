package ru.rdude.rpg.game.logic.gameStates;

import com.fasterxml.jackson.annotation.*;
import ru.rdude.rpg.game.logic.data.io.GameMapFileLoader;
import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.GameMap;
import ru.rdude.rpg.game.logic.map.PlaceObserver;
import ru.rdude.rpg.game.logic.map.Point;
import ru.rdude.rpg.game.logic.time.TimeForMovingCalculator;
import ru.rdude.rpg.game.mapVisual.MapStage;
import ru.rdude.rpg.game.utils.SubscribersManager;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.*;
import java.util.function.BiConsumer;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.NONE)
@JsonPolymorphicSubType("map")
public class Map extends GameStateBase {

    private final GameMap gameMap;
    private MapStage mapStage;
    @JsonProperty
    private final CellProperties[][] cellProperties;
    private Cell playerPosition;
    private SubscribersManager<PlaceObserver> placePositionSubscribers = new SubscribersManager<>();

    public Map(GameMap gameMap) {
        this.gameMap = gameMap;
        cellProperties = new CellProperties[gameMap.getWidth()][gameMap.getHeight()];
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                cellProperties[x][y] = new CellProperties();
            }
        }
    }

    // Json custom getters and setters

    @JsonCreator
    private Map(@JsonProperty("gameMap") long gameMapGuid, @JsonProperty("cellProperties") CellProperties[][] properties) {
        this.gameMap = GameMapFileLoader.load(Game.getMapFiles().get(gameMapGuid).mapFile);
        this.cellProperties = properties;
    }

    @JsonProperty("gameMap")
    private long getGameMapJson() {
        return gameMap.guid;
    }

    @JsonProperty("playerPosition")
    private Point getPlayerPositionJson() {
        return playerPosition.point();
    }

    @JsonProperty("playerPosition")
    private void setPlayerPositionJson(Point point) {
        this.playerPosition = gameMap.cell(point);
    }

    ///////////////////////////////////

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

    public Cell getPlayerPosition() {
        return playerPosition;
    }

    public CellProperties getCellProperties(Cell cell) {
        return cellProperties[cell.getX()][cell.getY()];
    }

    public void setPlayerPosition(Cell playerPosition) {
        Cell oldPosition = this.playerPosition;
        this.playerPosition = playerPosition;
        playerPosition.getArea(2, true).forEach(cell -> getCellProperties(cell).setVisible(true));
        mapStage.playerChangedPosition(oldPosition, playerPosition);
        notifySubscribers(oldPosition, playerPosition);
        Game.getCurrentGame().getTimeManager()
                .increaseTime(TimeForMovingCalculator.calculate(oldPosition, playerPosition));
    }

    public void placePlayerOnStartPosition() {
        this.playerPosition = gameMap.cell(gameMap.getStartPoint());
        playerPosition.getArea(2, true).forEach(cell -> getCellProperties(cell).setVisible(true));
    }

    public boolean cellHasMonster(Cell cell) {
        return getCellProperties(cell).monsters != null;
    }

    public Party cellMonsters(Cell cell) {
        return getCellProperties(cell).monsters;
    }

    public boolean isCellVisible(Cell cell) {
        return getCellProperties(cell).isVisible();
    }

    public void forEachCellProperties(BiConsumer<Cell, CellProperties> action) {
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                action.accept(gameMap.cell(x, y), cellProperties[x][y]);
            }
        }
    }

    public void subscribe(PlaceObserver subscriber) {
        placePositionSubscribers.subscribe(subscriber);
    }

    private void notifySubscribers(Cell oldPosition, Cell newPosition) {
        placePositionSubscribers.notifySubscribers(subscriber -> subscriber.update(oldPosition, newPosition));
    }


    public static class CellProperties {

        Party monsters;
        boolean visible = false;

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
