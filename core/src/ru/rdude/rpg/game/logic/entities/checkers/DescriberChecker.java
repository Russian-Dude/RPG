package ru.rdude.rpg.game.logic.entities.checkers;

import ru.rdude.rpg.game.logic.data.EntityData;

abstract class DescriberChecker<T extends EntityData> {

    public boolean check(long describerGuid, long entityDataGuid) {
        T describerData = getDataByGuid(describerGuid);
        T entityData = getDataByGuid(entityDataGuid);
        return describerData != null && entityData != null && check(describerData, entityData);
    }

    public boolean check(T describer, long entityDataGuid) {
        T entityByGuid = getDataByGuid(entityDataGuid);
        return entityByGuid != null && check(describer, entityByGuid);
    }

    public boolean check(long describerGuid, T entityData) {
        T describerData = getDataByGuid(describerGuid);
        return describerData != null && check(describerData, entityData);
    }

    protected abstract T getDataByGuid(long guid);

    public abstract boolean check(T describer, T entityData);

}
