package ru.rdude.rpg.game.logic.entities.beings;

public interface BeingActionObserver {
    void update(BeingAction action, Being being);
}
