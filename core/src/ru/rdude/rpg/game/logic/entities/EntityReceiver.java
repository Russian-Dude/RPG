package ru.rdude.rpg.game.logic.entities;

import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.entities.skills.Damage;
import ru.rdude.rpg.game.logic.enums.ItemMainType;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.stats.Stats;
import ru.rdude.rpg.game.visual.VisualBeing;

import java.util.Set;

public final class EntityReceiver {


    public static boolean damage(Being<?> target, Damage damage) {
        if (!target.isAlive()) {
            return false;
        }
        // receive
        if (damage.isHit()) {
            VisualBeing.VISUAL_BEING_FINDER.find(target).ifPresent(vb -> vb.getHpBar().addDelayed(damage));
            target.stats().hp().decrease(damage.value());
        }
        // notify subscribers
        // damage
        if (damage.value() > 0) {
            // check if damage is missed
            if (damage.isMiss()) {
                final BeingAction beingAction = new BeingAction(BeingAction.Action.MISS, damage.interactor(), damage.bySkill(), damage.value());
                if (damage.interactor() instanceof Being) {
                    ((Being<?>) damage.interactor()).notifySubscribers(beingAction, (Being<?>) damage.interactor());
                    Game.getCurrentGame().getGameLogger().log(beingAction, (Being<?>) damage.interactor());
                }
                return false;
            }
            if (damage.isDodge()) {
                final BeingAction beingAction = new BeingAction(BeingAction.Action.DODGE, damage.interactor(), damage.bySkill(), damage.value());
                target.notifySubscribers(beingAction, target);
                Game.getCurrentGame().getGameLogger().log(beingAction, target);
                return false;
            }
            // check if blocked
            if (damage.isBlock()) {
                final BeingAction beingAction = new BeingAction(BeingAction.Action.BLOCK, damage.interactor(), damage.bySkill(), damage.value());
                target.notifySubscribers(beingAction, target);
                Game.getCurrentGame().getGameLogger().log(beingAction, target);
                return false;
            }
            // check if parried
            if (damage.isParry()) {
                final BeingAction beingAction = new BeingAction(BeingAction.Action.PARRY, damage.interactor(), damage.bySkill(), damage.value());
                target.notifySubscribers(beingAction, target);
                Game.getCurrentGame().getGameLogger().log(beingAction, target);
                return false;
            }
            // check if critical
            if (damage.isCritical()) {
                final BeingAction beingAction = new BeingAction(BeingAction.Action.CRITICAL_RECEIVE, damage.interactor(), damage.bySkill(), damage.value());
                target.notifySubscribers(beingAction, target);
                if (damage.interactor() instanceof Being) {
                    final BeingAction beingAction1 = new BeingAction(BeingAction.Action.CRITICAL_DEAL, target, damage.bySkill(), damage.value());
                    ((Being<?>) damage.interactor()).notifySubscribers(beingAction1, (Being<?>) damage.interactor());
                }
                Game.getCurrentGame().getGameLogger().log(beingAction, target);
            } else {
                final BeingAction beingAction = new BeingAction(BeingAction.Action.DAMAGE_RECEIVE, damage.interactor(), damage.bySkill(), damage.value());
                target.notifySubscribers(beingAction, target);
                Game.getCurrentGame().getGameLogger().log(beingAction, target);
                if (damage.interactor() instanceof Being) {
                    final BeingAction beingAction1 = new BeingAction(BeingAction.Action.DAMAGE_DEAL, target, damage.bySkill(), damage.value());
                }
            }
            if (!target.isAlive()) {
                final BeingAction beingAction = new BeingAction(BeingAction.Action.DIE, damage.interactor(), damage.bySkill(), damage.value());
                Game.getCurrentGame().getGameLogger().log(beingAction, target);
                target.notifySubscribers(beingAction, target);
                if (damage.interactor() instanceof Being) {
                    ((Being<?>) damage.interactor()).notifySubscribers(new BeingAction(BeingAction.Action.KILL, target, damage.bySkill(), damage.value()), (Being<?>) damage.interactor());
                }
            }
        }
        // heal
        else {
            target.notifySubscribers(new BeingAction(BeingAction.Action.HEAL_RECEIVE, damage.interactor(), damage.bySkill(), damage.value()), target);
            if (damage.interactor() instanceof Being)
                ((Being<?>) damage.interactor()).notifySubscribers(new BeingAction(BeingAction.Action.HEAL_DEAL, target, damage.bySkill(), damage.value()), (Being<?>) damage.interactor());
        }
        return true;
    }

    public static boolean buff(Being<?> target, Buff buff) {
        if (target.getBuffs().stream()
                .anyMatch(b -> b.getEntityData().equals(buff.getEntityData()))) {
            switch (buff.getEntityData().getOverlay()) {
                case NEGATE:
                    buff.remove();
                    return true;
                case UPDATE:
                    buff.updateDuration();
                    return true;
                case REPLACE:
                    buff.remove();
                    break;
            }
        }
        // notify subscribers
        target.notifySubscribers(new BeingAction(BeingAction.Action.BUFF_RECEIVE, buff, buff.getEntityData(), 0), target);
        buff.getCaster().notifySubscribers(new BeingAction(BeingAction.Action.BUFF_DEAL, buff, buff.getEntityData(), 0), buff.getCaster());
        Game.getCurrentGame().getGameLogger().log(target, buff, new BeingAction(BeingAction.Action.BUFF_RECEIVE, buff.getCaster(), buff.getEntityData(), 0));
        // subscribing to buff updates:
        if (!buff.isPermanent()) {
            buff.subscribe(target);
        }

        // permanent
        if (buff.isPermanent()) {
            // being types
            if (buff.getEntityData().getTransformation().getBeingTypes() != null && !buff.getEntityData().getTransformation().getBeingTypes().isEmpty()) {
                target.beingTypes().add(buff, buff.getEntityData().getTransformation().getBeingTypes());
                if (buff.entityData.getTransformation().isOverride()) {
                    target.beingTypes().setDefault(buff.entityData.getTransformation().getBeingTypes());
                } else {
                    target.beingTypes().getDefault().addAll(buff.entityData.getTransformation().getBeingTypes());
                }
            }
            // elements
            if (buff.getEntityData().getTransformation().getElements() != null && !buff.getEntityData().getTransformation().getElements().isEmpty()) {
                target.elements().add(buff, buff.getEntityData().getTransformation().getElements());
                if (buff.entityData.getTransformation().isOverride()) {
                    target.elements().setDefault(buff.entityData.getTransformation().getElements());
                } else {
                    target.elements().getDefault().addAll(buff.entityData.getTransformation().getElements());
                }
            }
            // size
            if (buff.getEntityData().getTransformation().getSize() != null) {
                target.size().add(buff, buff.getEntityData().getTransformation().getSize());
                if (buff.entityData.getTransformation().isOverride()) {
                    target.size().setDefault(Set.of(buff.entityData.getTransformation().getSize()));
                }
            }
            // stats
            buff.getStats().ifPresent(target.stats()::increase);
        }

        // not permanent
        else {
            // being types:
            if (buff.getEntityData().getTransformation().getBeingTypes() != null && !buff.getEntityData().getTransformation().getBeingTypes().isEmpty()) {
                target.beingTypes().add(buff, buff.getEntityData().getTransformation().getBeingTypes());
            }
            // elements:
            if (buff.getEntityData().getTransformation().getElements() != null && !buff.getEntityData().getTransformation().getElements().isEmpty()) {
                target.elements().add(buff, buff.getEntityData().getTransformation().getElements());
            }
            // size:
            if (buff.getEntityData().getTransformation().getSize() != null) {
                target.size().add(buff, buff.getEntityData().getTransformation().getSize());
            }
            // coefficients:
            if (buff.getEntityData().getBuffCoefficients() != null) {
                target.coefficients().addSumOf(buff.getEntityData().getBuffCoefficients());
            }
            buff.getStats().ifPresent(stats -> target.stats().increaseBuffValues(Buff.class, stats));
        }
        // buff pool:
        target.getBuffs().add(buff);
        return true;
    }

    // return false if there is no place to receive item
    public static boolean item(Being<?> target, Item item) {
        if (target.backpack().receiveEntity(item))
            return true;
        if (item.getEntityData().getItemType().getMainType() == ItemMainType.ARMOR
                || item.getEntityData().getItemType().getMainType() == ItemMainType.WEAPON)
            return target.equipment().receiveEntity(item);
        return false;
    }


}
