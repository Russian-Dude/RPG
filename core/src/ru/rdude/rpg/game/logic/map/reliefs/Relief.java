package ru.rdude.rpg.game.logic.map.reliefs;

import ru.rdude.rpg.game.logic.map.CellProperty;

import java.util.List;

public abstract class Relief extends CellProperty {

    public static List<Relief> getDefaultReliefs() {
        return List.of(
                Forest.getInstance(),
                Hills.getInstance(),
                Mountains.getInstance(),
                Plain.getInstance());
    }

    public abstract Relief getThisInstance();

    @Override
    public String toString() {
        return this.getClass().getSimpleName().toUpperCase();
    }
}
