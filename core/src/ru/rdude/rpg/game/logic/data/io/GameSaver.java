package ru.rdude.rpg.game.logic.data.io;

import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.enums.GameState;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.Point;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;
import ru.rdude.rpg.game.utils.Pair;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class GameSaver {

    public GameSave save(Game game) {
        GameSave gameSave = new GameSave();

        // game state
        gameSave.currentGameState = GameState.get(game.getCurrentGameState().getClass());

        // map
        gameSave.mapGuid = game.getGameMap().getGameMap().guid;
        gameSave.playerPosition = game.getGameMap().getPlayerPosition().point();
        // cell properties
        game.getGameMap().getCellProperties().forEach((cell, properties) -> {
            Point point = cell.point();
            boolean visible = properties.isVisible();
            Set<Pair<Long, Integer>> monstersInfo = properties.getMonsters().getBeings().stream()
                    .map(monster -> new Pair<>(monster.getBeingData().getGuid(), (int) monster.stats().lvl().value()))
                    .collect(Collectors.toSet());
            gameSave.cellProperties.add(new GameSave.CellPropertiesInfo(point, monstersInfo, visible));
        });

        // TODO: 23.05.2021 save camp
        // TODO: 23.05.2021 save battle

        // time
        gameSave.minute = Game.getCurrentGame().getTimeManager().minute();
        gameSave.hour = Game.getCurrentGame().getTimeManager().hour();
        gameSave.day = Game.getCurrentGame().getTimeManager().day();
        gameSave.month = Game.getCurrentGame().getTimeManager().month();
        gameSave.year = Game.getCurrentGame().getTimeManager().year();

        // players
        for (Being being : Game.getCurrentGame().getCurrentPlayers().getBeings()) {
            if (!(being instanceof Player)) {
                continue;
            }
            Player player = (Player) being;

        }

        return gameSave;
    }

}
