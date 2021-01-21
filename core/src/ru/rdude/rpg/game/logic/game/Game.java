package ru.rdude.rpg.game.logic.game;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import ru.rdude.rpg.game.logic.GameLogger;
import ru.rdude.rpg.game.logic.data.io.GameJsonSerializer;
import ru.rdude.rpg.game.logic.data.io.ModuleFileLoader;
import ru.rdude.rpg.game.logic.entities.skills.SkillUser;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.logic.time.TimeManager;
import ru.rdude.rpg.game.ui.AvatarCreator;
import ru.rdude.rpg.game.ui.ItemImageFactory;

public class Game {

    private static Game currentGame = new Game();

    private DragAndDrop itemsDragAndDrop;
    private GameLogger gameLogger;
    private TimeManager timeManager;
    private SkillUser skillUser;
    private GameStateBase currentGameState;
    private Map gameMap;
    private AvatarCreator avatarCreator;
    private ItemImageFactory itemImageFactory;

    // io
    private ModuleFileLoader moduleFileLoader;
    private GameJsonSerializer gameJsonSerializer;

    public Game() {
        this.itemsDragAndDrop = new DragAndDrop();
        this.gameLogger = new GameLogger();
        this.avatarCreator = new AvatarCreator();
        this.itemImageFactory = new ItemImageFactory();
        this.gameJsonSerializer = new GameJsonSerializer();
        this.moduleFileLoader = new ModuleFileLoader(gameJsonSerializer, itemImageFactory);
    }

    public static Game getCurrentGame() {
        return currentGame;
    }

    public TimeManager getTimeManager() {
        return timeManager;
    }

    public SkillUser getSkillUser() {
        return skillUser;
    }

    public GameStateBase getCurrentGameState() {
        return currentGameState;
    }

    public Map getGameMap() {
        return gameMap;
    }

    public GameLogger getGameLogger() {
        return gameLogger;
    }

    public DragAndDrop getItemsDragAndDrop() {
        return itemsDragAndDrop;
    }

    public AvatarCreator getAvatarCreator() {
        return avatarCreator;
    }

    public ItemImageFactory getItemImageFactory() {
        return itemImageFactory;
    }

    public GameJsonSerializer getGameJsonSerializer() {
        return gameJsonSerializer;
    }
}
