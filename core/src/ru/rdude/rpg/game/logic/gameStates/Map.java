package ru.rdude.rpg.game.logic.gameStates;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.data.io.GameMapFileLoader;
import ru.rdude.rpg.game.logic.data.io.saves.CellsVisibilitySaveData;
import ru.rdude.rpg.game.logic.data.io.saves.MonstersOnCellsSaveData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.enums.GameState;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.GameMap;
import ru.rdude.rpg.game.logic.map.PlaceObserver;
import ru.rdude.rpg.game.logic.map.Point;
import ru.rdude.rpg.game.logic.map.objects.City;
import ru.rdude.rpg.game.logic.time.TimeForMovingCalculator;
import ru.rdude.rpg.game.mapVisual.MapStage;
import ru.rdude.rpg.game.utils.SubscribersManager;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.NONE)
@JsonPolymorphicSubType("map")
public class Map extends GameStateBase {

    private final GameMap gameMap;
    private Set<City> cities = new HashSet<>();
    private MapStage mapStage;
    private final CellProperties[][] cellProperties;
    private Cell playerPosition;
    private SubscribersManager<PlaceObserver> placePositionSubscribers = new SubscribersManager<>();

    public Map(GameMap gameMap) {
        this.gameMap = gameMap;
        cellProperties = new CellProperties[gameMap.getWidth()][gameMap.getHeight()];
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                cellProperties[x][y] = new CellProperties();
                if (gameMap.cell(x, y).getObject() instanceof City) {
                    cities.add(((City) gameMap.cell(x, y).getObject()));
                }
            }
        }
    }

    // Json custom getters and setters

    @JsonCreator
    private Map(@JsonProperty("gameMap") long gameMapGuid,
                @JsonProperty("cellsVisibility")CellsVisibilitySaveData cellsVisibilitySaveData,
                @JsonProperty("monstersOnCells")MonstersOnCellsSaveData monstersOnCellsSaveData) {

        this.gameMap = GameMapFileLoader.load(Game.getMapFiles().get(gameMapGuid).mapFile);
        cellProperties = new CellProperties[gameMap.getWidth()][gameMap.getHeight()];
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                cellProperties[x][y] = new CellProperties();
            }
        }
        cellsVisibilitySaveData.acceptVisibilityTo(cellProperties);
        monstersOnCellsSaveData.acceptMonstersTo(cellProperties);
    }

    @JsonProperty("cellsVisibility")
    private CellsVisibilitySaveData getCellsVisibility() {
        return new CellsVisibilitySaveData(cellProperties);
    }

    @JsonProperty("monstersOnCells")
    private MonstersOnCellsSaveData getMonstersOnCells() {
        return new MonstersOnCellsSaveData(cellProperties);
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

    public GameMap getGameMap() {
        return gameMap;
    }

    @Override
    public MapStage getStage() {
        if (mapStage == null) {
            mapStage = new MapStage(this);
        }
        return mapStage;
    }

    @Override
    public Party getAllySide(Being<?> of) {
        Party party = Game.getCurrentGame().getCurrentPlayers();
        return party.getBeings().contains(of) ? party : null;
    }

    public Set<City> getCities() {
        return cities;
    }

    @Override
    public void lose() {
        mapStage.stopPlayers();
    }

    public Cell getPlayerPosition() {
        return playerPosition;
    }

    public CellProperties getCellProperties(Cell cell) {
        return cellProperties[cell.getX()][cell.getY()];
    }

    public CellProperties getCellProperties(int x, int y) {
        return cellProperties[x][y];
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

    public MonstersOnCell cellMonsters(Cell cell) {
        return getCellProperties(cell).monsters;
    }

    public boolean isCellVisible(Cell cell) {
        return getCellProperties(cell).isVisible();
    }

    public void forEachCellProperties(BiConsumer<Cell, CellProperties> action) {
        for (int x = gameMap.getWidth() - 1; x >= 0; x--) {
            for (int y = gameMap.getHeight() - 1; y >= 0; y--) {
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

    @Override
    public GameState getEnumValue() {
        return GameState.MAP;
    }



    public static class MonstersOnCell {

        private final List<Monster> monsters;

        public MonstersOnCell(List<Monster> monsters) {
            this.monsters = monsters;
        }

        public List<Monster> getMonsters() {
            return monsters;
        }

        public boolean isEmpty() {
            return monsters.isEmpty();
        }

        public static class Monster {

            public final long guid;
            public final int lvl;

            public Monster(long guid, int lvl) {
                this.guid = guid;
                this.lvl = lvl;
            }
        }

    }

    public static class CellProperties {

        MonstersOnCell monsters;
        boolean visible = false;

        public MonstersOnCell getMonsters() {
            return monsters;
        }

        public void setMonsters(MonstersOnCell monsters) {
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
