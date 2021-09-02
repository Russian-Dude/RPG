package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.entities.EntityReceiver;
import ru.rdude.rpg.game.logic.entities.beings.Minion;
import ru.rdude.rpg.game.logic.game.Game;

public class SkillApplier {

    public void apply(SkillResult result) {

        // stamina
        int staminaUsed = result.getStaminaUsed();
        if (staminaUsed > 0) {
            result.getCaster().stats().stm().decrease(staminaUsed);
        }

        // cast
        if (result.getCast().isPresent()) {
            Cast cast = result.getCast().get();
            result.getCaster().setCast(cast.isComplete() ? null : cast);
            return;
        }

        // damage
        result.getDamage().ifPresent(damage -> EntityReceiver.damage(result.getTarget(), damage));

        // buff
        if (!result.isResisted()) {
            result.getBuff().ifPresent(buff -> EntityReceiver.buff(buff.getTarget(), buff));
        }

        // items
        result.getReceivedItems()
                .ifPresent(receivedMap -> receivedMap
                        .forEach((guid, amount) -> result.getTarget().receive(Game.getEntityFactory().items().get(guid, amount))));

        // summon
        result.getSummon()
                .ifPresent(minion -> {
                    Game.getMonsterSummoner().summon(result.getCaster(), result.getTarget(), minion, result.getSkillData());
                });
    }

}
