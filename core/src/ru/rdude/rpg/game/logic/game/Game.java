package ru.rdude.rpg.game.logic.game;

import com.badlogic.gdx.assets.AssetManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.rdude.rpg.game.logic.GameLogger;
import ru.rdude.rpg.game.logic.actions.SkillsSequencer;
import ru.rdude.rpg.game.logic.data.io.*;
import ru.rdude.rpg.game.logic.entities.beings.MonsterFactory;
import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.entities.items.ItemUser;
import ru.rdude.rpg.game.logic.entities.skills.*;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.GameStateHolder;
import ru.rdude.rpg.game.logic.gameStates.GameStateSwitcher;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.logic.time.TimeManager;
import ru.rdude.rpg.game.logic.time.TurnsManager;
import ru.rdude.rpg.game.ui.*;
import ru.rdude.rpg.game.utils.SubscribersManager;
import ru.rdude.rpg.game.visual.GameVisual;
import ru.rdude.rpg.game.visual.ParticleEffectsPools;
import ru.rdude.rpg.game.visual.SkillAnimator;

import java.util.HashMap;

public class Game {

    private static GameVisual gameVisual;
    private static final SubscribersManager<CurrentGameObserver> currentGameObservers = new SubscribersManager<>();

    private static Game currentGame;

    @JsonIgnore
    private final GameLogger gameLogger;
    @JsonIgnore
    private final ItemDragAndDroper itemsDragAndDrop;
    @JsonIgnore
    private final SkillsSequencer skillsSequencer;
    private TimeManager timeManager;
    private TurnsManager turnsManager;
    private final GameStateHolder gameStateHolder;
    private Map gameMap;
    private Party currentPlayers;

    private static final CustomObjectMapper customObjectMapper = new CustomObjectMapper("ru.rdude.rpg.game");
    private static AvatarCreator avatarCreator;
    private static final ImageFactory imageFactory = new ImageFactory();
    private static final MonsterFactory monsterFactory = new MonsterFactory();
    private static final GameFileSaver gameSaver = new GameFileSaver();
    private static final GameFileLoader gameLoader = new GameFileLoader();
    private static final ItemUser itemUser = new ItemUser();
    private static final StaticReferencesHolders staticReferencesHolders = new StaticReferencesHolders();
    private static final TooltipInfoFactory tooltipInfoFactory = new TooltipInfoFactory();
    private static final SkillUser skillUser = new SkillUser();
    private static final SkillTargeter skillTargeter = new SkillTargeter();
    private static final SkillParser skillParser = new SkillParser();
    private static final SkillResultsCreator skillResultsCreator = new SkillResultsCreator();
    private static final SkillApplier skillApplier = new SkillApplier();
    private static final AssetManager assetManager = new AssetManager();
    private static final ParticleEffectsPools particleEffectsPool = new ParticleEffectsPools();
    private static final SkillAnimator skillAnimator = new SkillAnimator();
    private static final GameStateSwitcher gameStateSwitcher = new GameStateSwitcher();

    // io
    private static GameJsonSerializer gameJsonSerializer = new GameJsonSerializer();
    private static ModuleFileLoader moduleFileLoader = new ModuleFileLoader(gameJsonSerializer, imageFactory);
    // map files by guid
    private static java.util.Map<Long, MapInfo> mapFiles = new HashMap<>();

    public Game() {
        // game visual and avatar creator uses Gdx static methods which available only when gdx application starts.
        // this leads to an error when editor started (which do not use LibGdx) so game visual
        // is being created only when first game instance is created.
        if (gameVisual == null) {
            gameVisual = new GameVisual();
            avatarCreator = new AvatarCreator();
        }
        currentGameObservers.notifySubscribers(sub -> sub.update(this, CurrentGameObserver.Action.CREATED));
        this.itemsDragAndDrop = new ItemDragAndDroper();
        this.gameLogger = new GameLogger();
        this.gameStateHolder = new GameStateHolder();
        this.turnsManager = new TurnsManager();
        this.timeManager = new TimeManager(turnsManager);
        this.skillsSequencer = new SkillsSequencer();
    }

    public static void initNewGame() {
        setCurrentGame(new Game());
    }

    public static void setCurrentGame(Game game) {
        currentGame = game;
        currentGameObservers.notifySubscribers(sub -> sub.update(currentGame, CurrentGameObserver.Action.BECOME_CURRENT));
    }

    public static Game getCurrentGame() {
        return currentGame;
    }

    public static GameVisual getGameVisual() {
        return gameVisual;
    }

    public static AvatarCreator getAvatarCreator() {
        return avatarCreator;
    }

    public static ImageFactory getImageFactory() {
        return imageFactory;
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

    public static ItemUser getItemUser() {
        return itemUser;
    }

    public static void subscribe(CurrentGameObserver subscriber) {
        currentGameObservers.subscribe(subscriber);
    }

    public static void unsubscribe(CurrentGameObserver subscriber) {
        currentGameObservers.unsubscribe(subscriber);
    }

    public static CustomObjectMapper getCustomObjectMapper() {
        return customObjectMapper;
    }

    public static StaticReferencesHolders getStaticReferencesHolders() {
        return staticReferencesHolders;
    }

    public static TooltipInfoFactory getTooltipInfoFactory() {
        return tooltipInfoFactory;
    }

    public static SkillUser getSkillUser() {
        return skillUser;
    }

    public static SkillResultsCreator getSkillResultsCreator() {
        return skillResultsCreator;
    }

    public static SkillParser getSkillParser() {
        return skillParser;
    }

    public static SkillTargeter getSkillTargeter() {
        return skillTargeter;
    }

    public static SkillApplier getSkillApplier() {
        return skillApplier;
    }

    public static AssetManager getAssetManager() {
        return assetManager;
    }

    public static ParticleEffectsPools getParticleEffectsPool() {
        return particleEffectsPool;
    }

    public static SkillAnimator getSkillAnimator() {
        return skillAnimator;
    }

    public static GameStateSwitcher getGameStateSwitcher() {
        return gameStateSwitcher;
    }

    public ItemDragAndDroper getItemsDragAndDrop() {
        return itemsDragAndDrop;
    }

    public TimeManager getTimeManager() {
        return timeManager;
    }

    public TurnsManager getTurnsManager() {
        return turnsManager;
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

    public SkillsSequencer getSkillsSequencer() {
        return skillsSequencer;
    }
}
