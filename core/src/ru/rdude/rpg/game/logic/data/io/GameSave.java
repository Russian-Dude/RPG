package ru.rdude.rpg.game.logic.data.io;

import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.enums.GameState;
import ru.rdude.rpg.game.logic.map.Point;
import ru.rdude.rpg.game.utils.Pair;

import java.util.Set;

public class GameSave {

    // game state
    GameState currentGameState;
    // map
    long mapGuid;
    Point playerPosition;
    Set<CellPropertiesInfo> cellProperties;
    // battle
    // TODO: 23.05.2021 saving battle
    //Battle battle;
    // camp
    // TODO: 23.05.2021 saving camp
    //Camp camp;

    // players
    Party players;

    // time
    int minute = 0;
    int hour = 12;
    int day = 1;
    int month = 1;
    int year = 1;

    public static class CellPropertiesInfo {

        public final Point point;
        public final Set<Pair<Long, Integer>> monsters; // guid & level
        public final boolean visible;

        public CellPropertiesInfo(Point point, Set<Pair<Long, Integer>> monsters, boolean visible) {
            this.point = point;
            this.monsters = monsters;
            this.visible = visible;
        }
    }

}
