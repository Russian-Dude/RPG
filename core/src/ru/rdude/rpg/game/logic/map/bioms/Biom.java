package ru.rdude.rpg.game.logic.map.bioms;

public abstract class Biom {

    public abstract Biom getThisInstance();

    @Override
    public String toString() {
        return this.getClass().getSimpleName().substring(0, 3);
    }
}
