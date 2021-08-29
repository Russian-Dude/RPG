package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.enums.Target;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.holders.Slot;

import java.util.*;

public class SkillUser {

    public void use(SkillData skillData, Being<?> caster, Target mainTarget) {

        // stamina requirements
        if (caster.stats().stmValue() < skillData.getStaminaReq()) {
            return;
        }

        // items requirements (only for players)
        Map<Long, Integer> requiredItems = skillData.getRequirements().getItems();
        if (caster instanceof Player && requiredItems != null && !requiredItems.isEmpty()) {
            boolean hasItems = requiredItems.entrySet().stream()
                    .allMatch(entry -> caster.backpack().hasEntity(entry.getKey(), entry.getValue())
                                        || caster.equipment().hasEntity(entry.getKey()));
            if (!hasItems) {
                Game.getCurrentGame().getGameLogger().log(caster.getName() + " can't use " + skillData.getName() + ". Not enough required items.");
                return;
            }
            else if (skillData.getRequirements().isTakeItems()) {
                requiredItems.forEach((guid, amount) -> {
                    boolean isAllRemoved = caster.backpack().removeEntity(guid, amount);
                    if (!isAllRemoved) {
                        caster.equipment().removeEntity(guid);
                    }
                });
            }
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
