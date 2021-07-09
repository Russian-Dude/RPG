package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.enums.Target;
import ru.rdude.rpg.game.logic.game.Game;

public class SkillUser {

    public void use(SkillData skillData, Being<?> caster, Target mainTarget) {

        if (caster.stats().stmValue() < skillData.getStaminaReq()) {
            return;
        }

        if (mainTarget == Target.ALLY
                || mainTarget == Target.ENEMY
                || mainTarget == Target.ANY
                || mainTarget == Target.ANY_OTHER) {
            Game.getGameVisual().getSkillTargeter().start(caster, mainTarget, skillData);
            return;
        }

        SkillTargets skillTargets = Game.getSkillTargeter().get(skillData, caster, mainTarget);
        Game.getCurrentGame().getSkillsSequencer().add(skillData, caster, skillTargets);
    }

}
