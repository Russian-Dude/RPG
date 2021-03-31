package ru.rdude.rpg.game.logic.map.bioms;

import ru.rdude.rpg.game.logic.map.CellProperty;

import java.util.List;

public abstract class BiomCellProperty extends CellProperty {

    public static List<BiomCellProperty> getDefaultBiomes() {
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

    public abstract BiomCellProperty getThisInstance();

    @Override
    public String toString() {
        return this.getClass().getSimpleName().toUpperCase();
    }
}
