package ru.rdude.rpg.game.logic.enums;

import ru.rdude.rpg.game.logic.map.bioms.*;

import java.util.Arrays;

public enum Biom {
    DEAD_LAND(DeadLand.getInstance()),
    DIRT(Dirt.getInstance()),
    GRASS(Grass.getInstance()),
    JUNGLE(Jungle.getInstance()),
    SAND(Sand.getInstance()),
    SNOW(Snow.getInstance()),
    SWAMP(Swamp.getInstance()),
    VOLCANIC(Volcanic.getInstance()),
    WATER(Water.getInstance());

    public final BiomCellProperty cellProperty;

    Biom(BiomCellProperty cellProperty) {
        this.cellProperty = cellProperty;
    }

    public static Biom ofCellProperty(BiomCellProperty biomCellProperty) {
        return Arrays.stream(values())
                .filter(biom -> biom.cellProperty.equals(biomCellProperty))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(biomCellProperty.getClass() + " is not implemented"));
    }
}
