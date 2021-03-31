package ru.rdude.rpg.game.logic.enums;

import ru.rdude.rpg.game.logic.map.reliefs.Forest;
import ru.rdude.rpg.game.logic.map.reliefs.Mountains;
import ru.rdude.rpg.game.logic.map.reliefs.Plain;
import ru.rdude.rpg.game.logic.map.reliefs.ReliefCellProperty;

import java.util.Arrays;

public enum Relief {
    FOREST(Forest.getInstance()),
    MOUNTAINS(Mountains.getInstance()),
    PLAIN(Plain.getInstance());

    public final ReliefCellProperty cellProperty;

    Relief(ReliefCellProperty cellProperty) {
        this.cellProperty = cellProperty;
    }

    public static Relief ofCellProperty(ReliefCellProperty biomCellProperty) {
        return Arrays.stream(values())
                .filter(relief -> relief.cellProperty.equals(biomCellProperty))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(biomCellProperty.getClass() + " is not implemented"));
    }
}
