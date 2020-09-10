package ru.rdude.rpg.game.logic.map.bioms;

import ru.rdude.rpg.game.logic.map.CellProperty;

import java.util.List;

public abstract class Biom extends CellProperty {

    public static List<Biom> getDefaultBiomes() {
        return List.of(
                DeadLand.getInstance(),
                Dirt.getInstance(),
                Grass.getInstance(),
                Jungle.getInstance(),
                Sand.getInstance(),
                Snow.getInstance(),
                Swamp.getInstance(),
                Volcanic.getInstance(),
                Water.getInstance());
    }

    public abstract Biom getThisInstance();

    @Override
    public String toString() {
        return this.getClass().getSimpleName().toUpperCase();
    }
}
