package ru.rdude.rpg.game;

import com.badlogic.gdx.Gdx;
import ru.rdude.rpg.game.logic.data.io.GameMapFileLoader;
import ru.rdude.rpg.game.logic.enums.StatName;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.MainMenuGameState;
import ru.rdude.rpg.game.logic.holders.Slot;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.Stats;
import ru.rdude.rpg.game.logic.stats.secondary.Hp;
import ru.rdude.rpg.game.ui.MainMenuStage;

import java.util.Arrays;

public final class GameStarter {

    private GameStarter() { }

    public static void startGame() {
        // load modules
        Game.getModuleFileLoader().load();

        // preload map files
        Arrays.stream(Gdx.files.local("maps").list(".map"))
                .map(GameMapFileLoader::loadInfo)
                .forEach(mapInfo -> Game.getMapFiles().put(mapInfo.guid, mapInfo));

        // launch main menu
        Game.setCurrentGame(new Game());
        Game.getGameVisual().setMenuStage(MainMenuStage.instance);
        Game.getCurrentGame().getGameStateHolder().setGameState(new MainMenuGameState());


    }
}
