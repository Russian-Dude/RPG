package ru.rdude.rpg.game.logic.data.io;

import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.entities.beings.Monster;
import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.enums.GameState;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.logic.map.GameMap;

import java.util.List;
import java.util.stream.Collectors;

public class GameLoader {

    public Game load(GameSave gameSave) {
        Game game = new Game();

        // map
        GameMap gameMap = GameMapFileLoader.load(Game.getMapFiles().get(gameSave.mapGuid).mapFile);
        Map map = new Map(gameMap);
        // player position
        map.setPlayerPosition(gameMap.cell(gameSave.playerPosition));
        // cell properties
        gameSave.cellProperties.forEach(loadedProperties -> {
            Map.CellProperties cellProperties = map.getCellProperties().get(gameMap.cell(loadedProperties.point));
            cellProperties.setVisible(loadedProperties.visible);
            List<Monster> monsters = loadedProperties.monsters.stream()
                    .map(monsterInfo -> Game.getMonsterFactory().create(monsterInfo.getSecond(), MonsterData.getMonsterByGuid(monsterInfo.getFirst())))
                    .collect(Collectors.toList());
            cellProperties.setMonsters(new Party(monsters));
        });

        // current game state
        if (gameSave.currentGameState.equals(GameState.MAP)) {
            game.getGameStateHolder().setGameState(map);
        }
        // TODO: 23.05.2021 load camp
        // TODO: 23.05.2021 load battle

        // time
        game.getTimeManager().setTime(gameSave.minute, gameSave.hour, gameSave.day, gameSave.month, gameSave.year);

        // players


        /*

        // players
        for (Being being : Game.getCurrentGame().getCurrentPlayers().getBeings()) {
            if (!(being instanceof Player)) {
                continue;
            }
            Player player = (Player) being;

        }

        return gameSave;
         */


        /*
                // game state
        gameSave.currentGameState = GameState.get(game.getCurrentGameState().getClass());

        // map
        gameSave.mapGuid = game.getGameMap().getGameMap().guid;
        gameSave.playerPosition = game.getGameMap().getPlayerPosition().point();
        // cell properties
        HashMap<Point, Pair<Set<Long>, Boolean>> cellProperties = new HashMap<>();
        game.getGameMap().getCellProperties().forEach((cell, properties) -> {
            Set<Long> beingsOnCell = properties.getMonsters().getBeings().stream()
                    .map(being -> being.getBeingData().getGuid())
                    .collect(Collectors.toSet());
            cellProperties.put(cell.point(), new Pair<>(beingsOnCell, properties.isVisible()));
        });
         */

        return game;
    }
}
