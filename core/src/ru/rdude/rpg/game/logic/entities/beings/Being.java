package ru.rdude.rpg.game.logic.entities.beings;

import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.data.BeingData;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.items.holders.AvailableSkills;
import ru.rdude.rpg.game.logic.entities.items.holders.EquipmentSlotsHolder;
import ru.rdude.rpg.game.logic.entities.items.holders.ItemSlotsHolder;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.entities.skills.BuffObserver;
import ru.rdude.rpg.game.logic.entities.skills.Damage;
import ru.rdude.rpg.game.logic.entities.states.StateHolder;
import ru.rdude.rpg.game.logic.enums.*;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.stats.Stats;
import ru.rdude.rpg.game.utils.SubscribersManager;

import java.util.HashSet;
import java.util.Set;

public abstract class Being<T extends BeingData> extends Entity<T> implements BuffObserver {

    private final SubscribersManager<BeingActionObserver> beingActionObservers = new SubscribersManager<>();

    protected Stats stats;
    protected StateHolder<BeingType> beingTypes;
    protected StateHolder<Element> elements;
    protected StateHolder<Size> size;

    protected Coefficients coefficients;

    protected ItemSlotsHolder backpack;
    protected EquipmentSlotsHolder equipment;
    protected Set<Buff> buffs;
    protected AvailableSkills availableSkills;

    protected boolean alive;

    protected Being(long guid) {
        super(guid);
    }

    public Being(T beingData) {
        super(beingData);
        alive = true;
        stats = new Stats(true);
        beingTypes = new StateHolder<>(beingData.getBeingTypes());
        elements = new StateHolder<>(beingData.getElements());
        size = new StateHolder<>(beingData.getSize());
        coefficients = new Coefficients();
        buffs = new HashSet<>();
        backpack = new ItemSlotsHolder(16);
        equipment = new EquipmentSlotsHolder(this);
        availableSkills = new AvailableSkills();
    }


    public abstract AttackType getAttackType();

    public abstract boolean canBlock();

    public abstract boolean canParry();

    public String getName() {
        return entityData.getName();
    }

    public void setName(String name) {
        entityData.setName(name);
    }

    public Set<Buff> getBuffs() {
        return buffs;
    }

    // return true if receive damage
    public boolean receive(Damage damage) {
        if (!alive) {
            return false;
        }
        // damage
        if (damage.value() > 0) {
            // check if damage is missed
            if (damage.isMiss()) {
                final BeingAction beingAction = new BeingAction(BeingAction.Action.DODGE, damage.interactor(), damage.bySkill(), damage.value());
                notifySubscribers(beingAction, this);
                if (damage.interactor() instanceof Being){
                    ((Being<?>) damage.interactor()).notifySubscribers(new BeingAction(BeingAction.Action.MISS, this, damage.bySkill(), damage.value()), (Being) damage.interactor());
                }
                Game.getCurrentGame().getGameLogger().log(beingAction, this);
                return false;
            }
            // check if blocked
            if (damage.isBlock()) {
                final BeingAction beingAction = new BeingAction(BeingAction.Action.BLOCK, damage.interactor(), damage.bySkill(), damage.value());
                notifySubscribers(beingAction, this);
                Game.getCurrentGame().getGameLogger().log(beingAction, this);
                return false;
            }
            // check if parried
            if (damage.isParry()) {
                final BeingAction beingAction = new BeingAction(BeingAction.Action.PARRY, damage.interactor(), damage.bySkill(), damage.value());
                notifySubscribers(beingAction, this);
                Game.getCurrentGame().getGameLogger().log(beingAction, this);
                return false;
            }
            // else take damage
            double realDamage = damage.isCritical() ? damage.value() * 1.2
                    : damage.value() - stats.defValue();
            if (realDamage < 1)
                realDamage = 1;
            boolean isAlive = stats.hp().decrease(realDamage) > 0;
            if (damage.isCritical()) {
                final BeingAction beingAction = new BeingAction(BeingAction.Action.CRITICAL_RECEIVE, damage.interactor(), damage.bySkill(), realDamage);
                notifySubscribers(beingAction, this);
                Game.getCurrentGame().getGameLogger().log(beingAction, this);
            }
            else {
                final BeingAction beingAction = new BeingAction(BeingAction.Action.DAMAGE_RECEIVE, damage.interactor(), damage.bySkill(), realDamage);
                notifySubscribers(beingAction, this);
                Game.getCurrentGame().getGameLogger().log(beingAction, this);
            }
            if (damage.interactor() instanceof Being)
                ((Being<?>) damage.interactor()).notifySubscribers(new BeingAction(BeingAction.Action.DAMAGE_DEAL, this, damage.bySkill(), realDamage), (Being) damage.interactor());
            if (!isAlive) {
                alive = false;
                final BeingAction beingAction = new BeingAction(BeingAction.Action.DIE, damage.interactor(), damage.bySkill(), realDamage);
                Game.getCurrentGame().getGameLogger().log(beingAction, this);
                notifySubscribers(beingAction, this);
                if (damage.interactor() instanceof Being) {
                    ((Being<?>) damage.interactor()).notifySubscribers(new BeingAction(BeingAction.Action.KILL, this, damage.bySkill(), realDamage), (Being) damage.interactor());
                }
            }
        }
        // heal
        else {
            stats.hp().decrease(damage.value());
            notifySubscribers(new BeingAction(BeingAction.Action.HEAL_RECEIVE, damage.interactor(), damage.bySkill(), damage.value()), this);
            if (damage.interactor() instanceof Being)
                ((Being<?>) damage.interactor()).notifySubscribers(new BeingAction(BeingAction.Action.HEAL_DEAL, this, damage.bySkill(), damage.value()), (Being) damage.interactor());
        }
        return true;
    }

    public boolean receive(Buff buff) {
        // notify subscribers
        notifySubscribers(new BeingAction(BeingAction.Action.BUFF_RECEIVE, buff, buff.getEntityData(), 0), this);
        notifySubscribers(new BeingAction(BeingAction.Action.BUFF_DEAL, buff, buff.getEntityData(), 0), buff.getCaster());
        Game.getCurrentGame().getGameLogger().log(this, buff, new BeingAction(BeingAction.Action.BUFF_RECEIVE, buff.getCaster(), buff.getEntityData(), 0));
        // subscribing to buff updates:
        if (!buff.isPermanent())
            buff.subscribe(this);
        // stats:
        if (buff.isPermanent())
            stats.increase(buff.getStats());
        else
            stats.increaseBuffValues(Buff.class, buff.getStats());
        // being types:
        if (buff.getEntityData().getTransformation().getBeingTypes() != null && !buff.getEntityData().getTransformation().getBeingTypes().isEmpty())
            beingTypes.add(buff, buff.getEntityData().getTransformation().getBeingTypes());
        // elements:
        if (buff.getEntityData().getTransformation().getElements() != null && !buff.getEntityData().getTransformation().getElements().isEmpty())
            elements.add(buff, buff.getEntityData().getTransformation().getElements());
        // size:
        if (buff.getEntityData().getTransformation().getSize() != null) {
            size.add(buff, buff.getEntityData().getTransformation().getSize());
        }
        // coefficients:
        if (buff.getEntityData().getBuffCoefficients() != null)
            coefficients.addSumOf(buff.getEntityData().getBuffCoefficients());
        // buff pool:
        buffs.add(buff);
        return true;
    }

    // return false if there is no place to receive item
    public boolean receive(Item item) {
        if (backpack.receiveEntity(item))
            return true;
        if (item.getEntityData().getItemType().getMainType() == ItemMainType.ARMOR
                || item.getEntityData().getItemType().getMainType() == ItemMainType.WEAPON)
            return equipment.receiveEntity(item);
        return false;
    }

    public Stats stats() {
        return stats;
    }

    public StateHolder<BeingType> beingTypes() {
        return beingTypes;
    }

    public StateHolder<Element> elements() {
        return elements;
    }

    public StateHolder<Size> size() {
        return size;
    }

    public ItemSlotsHolder backpack() {
        return backpack;
    }

    public EquipmentSlotsHolder equipment() {
        return equipment;
    }

    public Coefficients coefficients() {
        return coefficients;
    }

    public AvailableSkills getAvailableSkills() {
        return availableSkills;
    }

    public void subscribe(BeingActionObserver observer) {
        beingActionObservers.subscribe(observer);
    }

    public void unsubscribe(BeingActionObserver observer) {
        beingActionObservers.unsubscribe(observer);
    }

    public void notifySubscribers(BeingAction action, Being being) {
        beingActionObservers.notifySubscribers(obs -> obs.update(action, being));
    }

    // removing buffs
    @Override
    public void update(Buff buff, boolean ends) {
        if (!ends) return;
        if (!buff.isPermanent()) {
            buff.unsubscribe(this);
            stats.decreaseBuffValues(Buff.class, buff.getStats());
            Game.getCurrentGame().getGameLogger().log(this, buff, new BeingAction(BeingAction.Action.BUFF_REMOVED, buff, buff.getEntityData(), 0));
        }
        beingTypes.remove(buff);
        elements.remove(buff);
        size.remove(buff);
        if (buff.getEntityData().getBuffCoefficients() != null)
            coefficients.removeSumOf(buff.getEntityData().getBuffCoefficients());
        buffs.remove(buff);
    }

}
