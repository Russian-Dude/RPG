package ru.rdude.rpg.game.logic.map;

import ru.rdude.rpg.game.logic.gameStates.Map;

public interface PlaceObserver {
    void update(Map.CellProperties oldPosition, Map.CellProperties newPosition);
}
