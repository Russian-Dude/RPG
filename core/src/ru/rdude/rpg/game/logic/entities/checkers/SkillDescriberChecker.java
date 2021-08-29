package ru.rdude.rpg.game.logic.entities.checkers;

import ru.rdude.rpg.game.logic.data.SkillData;

class SkillDescriberChecker extends DescriberChecker<SkillData> {

    @Override
    protected SkillData getDataByGuid(long guid) {
        return SkillData.getSkillByGuid(guid);
    }

    @Override
    public boolean check(SkillData describer, SkillData entityData) {
        // skill type
        if (describer.getType() != null && describer.getType() != entityData.getType()) {
            return false;
        }
        // attack type
        if (describer.getAttackType() != null && describer.getAttackType() != entityData.getAttackType()) {
            return false;
        }
        // elements
        if (describer.getElements() != null && !entityData.getElements().containsAll(describer.getElements())) {
            return false;
        }
        // effect
        if (describer.getEffect() != null && describer.getEffect() != entityData.getEffect()) {
            return false;
        }
        // buff type
        if (describer.getBuffType() != null && describer.getBuffType() != entityData.getBuffType()) {
            return false;
        }
        return true;
    }
}
