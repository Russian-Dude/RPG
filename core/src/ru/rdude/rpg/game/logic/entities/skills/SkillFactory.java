package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.utils.Functions;

public class SkillFactory {

    public SkillData describerToReal(long guid) {
        return describerToReal(SkillData.getSkillByGuid(guid));
    }

    public SkillData describerToReal(SkillData skillData) {
        if (!skillData.isDescriber()) {
            return skillData;
        }
        return SkillData.getSkills().values().stream()
                // filter out describers
                .filter(data -> !data.isDescriber())
                // type
                .filter(data -> skillData.getType() == null || data.getType() == skillData.getType())
                // attack type
                .filter(data -> skillData.getAttackType() == null || data.getAttackType() == skillData.getAttackType())
                // elements
                .filter(data -> skillData.getElements() == null
                                || skillData.getElements().isEmpty()
                                || data.getElements().containsAll(skillData.getElements()))
                // effect
                .filter(data -> skillData.getEffect() == null || data.getEffect() == skillData.getEffect())
                // buff type
                .filter(data -> skillData.getBuffType() == null || data.getBuffType() == skillData.getBuffType())
                .collect(Functions.randomCollector());
    }

}
