package ru.rdude.rpg.game.logic.map;

public interface PlaceObserver {
    void update(Cell oldPosition, Cell newPosition);
}
