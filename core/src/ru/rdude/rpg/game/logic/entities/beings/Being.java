package ru.rdude.rpg.game.logic.entities.beings;

import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.data.BeingData;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.items.equip.EquippableItem;
import ru.rdude.rpg.game.logic.entities.items.holders.EquipmentSlotsHolder;
import ru.rdude.rpg.game.logic.entities.items.holders.ItemSlotsHolder;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.entities.skills.BuffObserver;
import ru.rdude.rpg.game.logic.entities.skills.Damage;
import ru.rdude.rpg.game.logic.entities.states.StateHolder;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.enums.BeingType;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.enums.Size;
import ru.rdude.rpg.game.logic.stats.Stats;

import java.util.HashSet;
import java.util.Set;

public abstract class Being extends Entity implements BuffObserver {

    private Set<BeingActionObserver> beingActionObservers;

    protected BeingData beingData;
    protected Stats stats;
    protected StateHolder<BeingType> beingTypes;
    protected StateHolder<Element> elements;
    protected StateHolder<Size> size;

    protected Coefficients coefficients;

    protected ItemSlotsHolder<Item> backpack;
    protected EquipmentSlotsHolder equipment;
    protected Set<Buff> buffs;

    protected boolean alive;

    public Being(BeingData beingData) {
        this.beingData = beingData;
        alive = true;
        stats = new Stats(true);
        stats.addBuffClass(Buff.class);
        stats.addBuffClass(Item.class);
        beingTypes = new StateHolder<>(beingData.getBeingTypes());
        elements = new StateHolder<>(beingData.getElements());
        size = new StateHolder<>(beingData.getSize());
        coefficients = new Coefficients();
        buffs = new HashSet<>();
        backpack = new ItemSlotsHolder<>(16, Item.class);
        equipment = new EquipmentSlotsHolder();
    }


    public abstract AttackType getAttackType();

    public abstract boolean canBlock();

    public abstract boolean canParry();


    // return true if receive damage
    public boolean receive(Damage damage) {
        // damage
        if (damage.value() > 0) {
            // check if damage is missed
            if (damage.isMiss()) {
                notifySubscribers(new BeingAction(BeingAction.Action.DODGE, damage.interactor()), this);
                if (damage.interactor() instanceof Being)
                    ((Being) damage.interactor()).notifySubscribers(new BeingAction(BeingAction.Action.MISS, this), (Being) damage.interactor());
                return false;
            }
            // check if blocked
            if (damage.isBlock()) {
                notifySubscribers(new BeingAction(BeingAction.Action.BLOCK, damage.interactor()), this);
                return false;
            }
            // check if parried
            if (damage.isParry()) {
                notifySubscribers(new BeingAction(BeingAction.Action.PARRY, damage.interactor()), this);
                return false;
            }
            // else take damage
            double realDamage = damage.isCritical() ? damage.value() * 1.2
                    : damage.value() - stats.defValue();
            if (realDamage < 1)
                realDamage = 1;
            boolean isAlive = stats.hp().decrease(realDamage) > 0;
            if (damage.isCritical())
                notifySubscribers(new BeingAction(BeingAction.Action.CRITICAL_RECEIVE, damage.interactor()), this);
            else
                notifySubscribers(new BeingAction(BeingAction.Action.DAMAGE_RECEIVE, damage.interactor()), this);
            if (damage.interactor() instanceof Being)
                ((Being) damage.interactor()).notifySubscribers(new BeingAction(BeingAction.Action.DAMAGE_DEAL, this), (Being) damage.interactor());
            if (!isAlive) {
                alive = false;
                notifySubscribers(new BeingAction(BeingAction.Action.DIE, damage.interactor()), this);
                if (damage.interactor() instanceof Being)
                    ((Being) damage.interactor()).notifySubscribers(new BeingAction(BeingAction.Action.KILL, this), (Being) damage.interactor());
            }
        }
        // heal
        else {
            stats.hp().increase(damage.value());
            notifySubscribers(new BeingAction(BeingAction.Action.HEAL_RECEIVE, damage.interactor()), this);
            if (damage.interactor() instanceof Being)
                ((Being) damage.interactor()).notifySubscribers(new BeingAction(BeingAction.Action.HEAL_DEAL, this), (Being) damage.interactor());
        }
        return true;
    }

    public boolean receive(Buff buff) {
        // subscribing to buff updates:
        if (!buff.isPermanent())
            buff.subscribe(this);
        // stats:
        if (buff.isPermanent())
            stats.increase(buff.getStats());
        else
            stats.increaseBuffValues(Buff.class, buff.getStats());
        // being types:
        if (buff.getSkillData().getTransformation().getBeingTypes() != null && !buff.getSkillData().getTransformation().getBeingTypes().isEmpty())
            beingTypes.add(buff, buff.getSkillData().getTransformation().getBeingTypes());
        // elements:
        if (buff.getSkillData().getTransformation().getElements() != null && !buff.getSkillData().getTransformation().getElements().isEmpty())
            elements.add(buff, buff.getSkillData().getTransformation().getElements());
        // size:
        if (buff.getSkillData().getTransformation().getSize() != null) {
            size.add(buff, buff.getSkillData().getTransformation().getSize());
        }
        // coefficients:
        if (buff.getSkillData().getBuffCoefficients() != null)
            coefficients.addSumOf(buff.getSkillData().getBuffCoefficients());
        // buff pool:
        buffs.add(buff);
        return true;
    }

    // return false if there is no place to receive item
    public boolean receive(Item item) {
        if (backpack.receiveEntity(item))
            return true;
        if (item instanceof EquippableItem)
            return equipment.receiveEntity((EquippableItem) item);
        return false;
    }


    public BeingData getBeingData() {
        return beingData;
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

    public ItemSlotsHolder<Item> backpack() {
        return backpack;
    }

    public EquipmentSlotsHolder equipment() {
        return equipment;
    }

    public Coefficients coefficients() {
        return coefficients;
    }

    public void subscribe(BeingActionObserver observer) {
        beingActionObservers.add(observer);
    }

    public void unsubscribe(BeingActionObserver observer) {
        beingActionObservers.remove(observer);
    }

    private void notifySubscribers(BeingAction action, Being being) {
        beingActionObservers.forEach(obs -> obs.update(action, this));
    }

    // removing buffs
    @Override
    public void update(Buff buff, boolean ends) {
        if (!ends) return;
        if (!buff.isPermanent()) {
            buff.unsubscribe(this);
            stats.decreaseBuffValues(Buff.class, buff.getStats());
        }
        beingTypes.remove(buff);
        elements.remove(buff);
        size.remove(buff);
        if (buff.getSkillData().getBuffCoefficients() != null)
            coefficients.removeSumOf(buff.getSkillData().getBuffCoefficients());
        buffs.remove(buff);
    }

}
