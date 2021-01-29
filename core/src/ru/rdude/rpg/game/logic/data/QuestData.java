package ru.rdude.rpg.game.logic.data;

public class QuestData extends EntityData {
    @Override
    public boolean hasEntityDependency(long guid) {
        return false;
    }

    @Override
    public void replaceEntityDependency(long oldValue, long newValue) {

    }
}
