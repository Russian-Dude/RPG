package ru.rdude.rpg.game.logic.data;

public class EventData extends EntityData {
    @Override
    public boolean hasEntityDependency(long guid) {
        return false;
    }

    @Override
    public void replaceEntityDependency(long oldValue, long newValue) {

    }
}
