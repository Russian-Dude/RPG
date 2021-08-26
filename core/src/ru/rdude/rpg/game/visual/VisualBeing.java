package ru.rdude.rpg.game.visual;

import ru.rdude.rpg.game.logic.entities.beings.Being;

public interface VisualBeing<T extends Being<?>> extends VisualTarget{

    T getBeing();
    float getPrefWidth();
    float getPrefHeight();
    boolean remove();

    VisualBeingFinder VISUAL_BEING_FINDER = new VisualBeingFinder();
}
