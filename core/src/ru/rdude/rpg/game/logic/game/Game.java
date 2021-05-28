package ru.rdude.rpg.game.logic.game;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.rdude.rpg.game.logic.GameLogger;
import ru.rdude.rpg.game.logic.data.io.GameFileLoader;
import ru.rdude.rpg.game.logic.data.io.GameFileSaver;
import ru.rdude.rpg.game.logic.data.io.GameJsonSerializer;
import ru.rdude.rpg.game.logic.data.io.ModuleFileLoader;
import ru.rdude.rpg.game.logic.entities.beings.MonsterFactory;
import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.GameStateHolder;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.logic.time.TimeManager;
import ru.rdude.rpg.game.ui.AvatarCreator;
import ru.rdude.rpg.game.ui.ItemImageFactory;
import ru.rdude.rpg.game.ui.MapInfo;
import ru.rdude.rpg.game.visual.GameVisual;

import java.util.HashMap;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Game {

    private static Game currentGame = new Game();

    private static final GameVisual gameVisual = new GameVisual();

    @JsonIgnore
    private final GameLogger gameLogger;
    private TimeManager timeManager;
    private final GameStateHolder gameStateHolder;
    private Map gameMap;
    private Party currentPlayers;

    private static final DragAndDrop itemsDragAndDrop = new DragAndDrop();
    private static final AvatarCreator avatarCreator = new AvatarCreator();
    private static final ItemImageFactory itemImageFactory = new ItemImageFactory();
    private static final MonsterFactory monsterFactory = new MonsterFactory();
    private static final GameFileSaver gameSaver = new GameFileSaver();
    private static final GameFileLoader gameLoader = new GameFileLoader();

    // io
    private static GameJsonSerializer gameJsonSerializer = new GameJsonSerializer();
    private static ModuleFileLoader moduleFileLoader = new ModuleFileLoader(gameJsonSerializer, itemImageFactory);
    // map files by guid
    private static java.util.Map<Long, MapInfo> mapFiles = new HashMap<>();

    public Game() {
        this.gameLogger = new GameLogger();
        this.gameStateHolder = new GameStateHolder();
        this.timeManager = new TimeManager();
    }

    public static void initNewGame() {
        currentGame = new Game();
    }

    public static void setCurrentGame(Game game) {
        currentGame = game;
    }

    public static Game getCurrentGame() {
        return currentGame;
    }

    public static GameVisual getGameVisual() {
        return gameVisual;
    }

    public static DragAndDrop getItemsDragAndDrop() {
        return itemsDragAndDrop;
    }

    public static AvatarCreator getAvatarCreator() {
        return avatarCreator;
    }

    public static ItemImageFactory getItemImageFactory() {
        return itemImageFactory;
    }

    public static GameJsonSerializer getGameJsonSerializer() {
        return gameJsonSerializer;
    }

    public static MonsterFactory getMonsterFactory() {
        return monsterFactory;
    }

    public static ModuleFileLoader getModuleFileLoader() {
        return moduleFileLoader;
    }

    public static java.util.Map<Long, MapInfo> getMapFiles() {
        return mapFiles;
    }

    public static GameFileSaver getGameSaver() {
        return gameSaver;
    }

    public static GameFileLoader getGameLoader() {
        return gameLoader;
    }

    public TimeManager getTimeManager() {
        return timeManager;
    }

    public GameStateHolder getGameStateHolder() {
        return gameStateHolder;
    }

    public GameStateBase getCurrentGameState() {
        return gameStateHolder.getGameState();
    }

    public Map getGameMap() {
        return gameMap;
    }

    public void setGameMap(Map gameMap) {
        this.gameMap = gameMap;
    }

    public Party getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayers(Party currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    public GameLogger getGameLogger() {
        return gameLogger;
    }
}
