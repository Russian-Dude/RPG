package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.enums.Target;
import ru.rdude.rpg.game.logic.game.Game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SkillUser {

    public void use(SkillData skillData, Being<?> caster, Target mainTarget) {

        if (caster.stats().stmValue() < skillData.getStaminaReq()) {
            return;
        }

        Game.getCurrentGame().getGameLogger().log(caster, skillData);

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
