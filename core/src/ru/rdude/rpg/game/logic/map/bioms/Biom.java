package ru.rdude.rpg.game.logic.map.bioms;

import ru.rdude.rpg.game.logic.map.CellProperty;

public abstract class Biom extends CellProperty {

    public abstract Biom getThisInstance();

    @Override
    public String toString() {
        return this.getClass().getSimpleName().substring(0, 3);
    }
}
