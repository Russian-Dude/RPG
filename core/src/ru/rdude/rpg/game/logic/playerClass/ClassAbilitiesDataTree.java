package ru.rdude.rpg.game.logic.playerClass;

import ru.rdude.rpg.game.logic.data.AbilityData;
import ru.rdude.rpg.game.logic.data.AbilityDataCell;

import java.util.Optional;

public class ClassAbilitiesDataTree {

    private long[][] cells = new long[9][9];

    public ClassAbilitiesDataTree() {
        clear();
    }

    public void clear() {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                setEmpty(x, y);
            }
        }
    }

    public void setCell(int x, int y, AbilityData abilityData) {
        cells[x][y] = abilityData.getGuid();
    }

    public void setCell(int x, int y, AbilityPath abilityPath) {
        cells[x][y] = abilityPath.ordinal();
    }

    public void setEmpty(int x, int y) {
        cells[x][y] = -1;
    }

    public long[][] getCells() {
        return cells;
    }

    public Optional<AbilityDataCell<?>> get(int x, int y) {
        long l = cells[x][y];
        if (l < 0) {
            return Optional.empty();
        }
        else if (l <= 10) {
            return Optional.of(AbilityPath.values()[(int) l]);
        }
        else {
            return Optional.ofNullable(AbilityData.getAbilityByGuid(l));
        }
    }

    public Optional<AbilityData> getAbilityData(int x, int y) {
        Optional<AbilityDataCell<?>> abilityDataCell = get(x, y);
        if (abilityDataCell.isPresent() && abilityDataCell.get() instanceof AbilityData) {
            return Optional.of((AbilityData) abilityDataCell.get());
        }
        else {
            return Optional.empty();
        }
    }

    public Optional<AbilityPath> getAbilityPath(int x, int y) {
        Optional<AbilityDataCell<?>> abilityDataCell = get(x, y);
        if (abilityDataCell.isPresent() && abilityDataCell.get() instanceof AbilityPath) {
            return Optional.of((AbilityPath) abilityDataCell.get());
        }
        else {
            return Optional.empty();
        }
    }
}
