package ru.rdude.rpg.game.logic.map.reliefs;

import ru.rdude.rpg.game.logic.map.CellProperty;

import java.util.List;

public abstract class ReliefCellProperty extends CellProperty {

    public static List<ReliefCellProperty> getDefaultReliefs() {
        return List.of(
                Forest.getInstance(),
                Mountains.getInstance(),
                Plain.getInstance());
    }

    public abstract ReliefCellProperty getThisInstance();

    @Override
    public String toString() {
        return this.getClass().getSimpleName().toUpperCase();
    }
}
