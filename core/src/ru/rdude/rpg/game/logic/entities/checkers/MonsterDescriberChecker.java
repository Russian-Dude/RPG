package ru.rdude.rpg.game.logic.entities.checkers;

import ru.rdude.rpg.game.logic.data.MonsterData;

class MonsterDescriberChecker extends DescriberChecker<MonsterData> {

    @Override
    protected MonsterData getDataByGuid(long guid) {
        return null;
    }

    @Override
    public boolean check(MonsterData describer, MonsterData entityData) {
        // levels
        if (entityData.getMaxLvl() < describer.getMinLvl() || entityData.getMinLvl() > describer.getMaxLvl()) {
            return false;
        }
        // attack type
        if (describer.getDefaultAttackType() != null && describer.getDefaultAttackType() != entityData.getDefaultAttackType()) {
            return false;
        }
        // size
        if (describer.getSize() != null && describer.getSize() != entityData.getSize()) {
            return false;
        }
        // elements
        if (describer.getElements() != null && !entityData.getElements().containsAll(describer.getElements())) {
            return false;
        }
        // types
        if (describer.getBeingTypes() != null && !entityData.getBeingTypes().containsAll(describer.getBeingTypes())) {
            return false;
        }
        // spawn biomes
        if (describer.getSpawnBioms() != null && !entityData.getSpawnBioms().containsAll(describer.getSpawnBioms())) {
            return false;
        }
        // spawn reliefs
        if (describer.getSpawnReliefs() != null && !entityData.getSpawnReliefs().containsAll(describer.getSpawnReliefs())) {
            return false;
        }
        return true;
    }
}
