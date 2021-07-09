package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.entities.EntityReceiver;

public class SkillApplier {

    public void apply(SkillResult result) {
        result.getDamage().ifPresent(damage -> EntityReceiver.damage(result.getTarget(), damage));
        if (!result.isResisted()) {
            result.getBuff().ifPresent(buff -> EntityReceiver.buff(buff.getTarget(), buff));
        }
    }

}
