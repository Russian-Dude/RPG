package ru.rdude.rpg.game.logic.entities.beings;

import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.data.BeingData;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.entities.EntityReceiver;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.items.holders.AvailableSkills;
import ru.rdude.rpg.game.logic.entities.items.holders.EquipmentSlotsHolder;
import ru.rdude.rpg.game.logic.entities.items.holders.ItemSlotsHolder;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.entities.skills.BuffObserver;
import ru.rdude.rpg.game.logic.entities.skills.Cast;
import ru.rdude.rpg.game.logic.entities.skills.Damage;
import ru.rdude.rpg.game.logic.entities.states.StateHolder;
import ru.rdude.rpg.game.logic.enums.*;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.StatObserver;
import ru.rdude.rpg.game.logic.stats.Stats;
import ru.rdude.rpg.game.logic.stats.secondary.Hp;
import ru.rdude.rpg.game.utils.SubscribersManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Being<T extends BeingData> extends Entity<T> implements BuffObserver, StatObserver {

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
    protected Cast cast;

    protected boolean ready;
    protected boolean alive;
    protected SkillEffect effect;

    protected List<Minion> minions;

    protected Being(long guid) {
        super(guid);
    }

    public Being(T beingData) {
        super(beingData);
        alive = true;
        ready = true;
        stats = new Stats(true);
        stats.hp().subscribe(this);
        beingTypes = new StateHolder<>(beingData.getBeingTypes());
        elements = new StateHolder<>(beingData.getElements());
        size = new StateHolder<>(beingData.getSize());
        coefficients = new Coefficients();
        buffs = new HashSet<>();
        backpack = new ItemSlotsHolder(16);
        equipment = new EquipmentSlotsHolder(this);
        availableSkills = new AvailableSkills();
        effect = SkillEffect.NO;
        minions = new ArrayList<>();
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

    public Cast getCast() {
        return cast;
    }

    public void setCast(Cast cast) {
        this.cast = cast;
    }

    public boolean isCasting() {
        return cast != null;
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

    public boolean isAlive() {
        return alive;
    }

    public boolean isReady() {
        return ready && alive && effect != SkillEffect.EXILE && effect != SkillEffect.STUN;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean receive(Damage damage) {
        return EntityReceiver.damage(this, damage);
    }

    public boolean receive(Buff buff) {
        return EntityReceiver.buff(this, buff);
    }

    public boolean receive(Item item) {
        return EntityReceiver.item(this, item);
    }

    public List<Minion> getMinions() {
        return minions;
    }

    public void addMinion(Minion minion) {
        minions.add(minion);
    }

    public void removeMinion(Minion minion) {
        minions.remove(minion);
    }

    public void subscribe(BeingActionObserver observer) {
        beingActionObservers.subscribe(observer);
    }

    public void unsubscribe(BeingActionObserver observer) {
        beingActionObservers.unsubscribe(observer);
    }

    public void notifySubscribers(BeingAction action, Being<?> being) {
        beingActionObservers.notifySubscribers(obs -> obs.update(action, being));
    }

    // removing buffs
    @Override
    public void update(Buff buff, boolean ends) {
        if (!ends) return;
        if (!buff.isPermanent()) {
            buff.unsubscribe(this);
            buff.getStats().ifPresent(buffStats -> stats.decreaseBuffValues(Buff.class, buffStats));
            Game.getCurrentGame().getGameLogger().log(this, buff, new BeingAction(BeingAction.Action.BUFF_REMOVED, buff, buff.getEntityData(), 0));
        }
        beingTypes.remove(buff);
        elements.remove(buff);
        size.remove(buff);
        if (buff.getEntityData().getBuffCoefficients() != null)
            coefficients.removeSumOf(buff.getEntityData().getBuffCoefficients());
        buffs.remove(buff);
    }

    @Override
    public void update(Stat stat) {
        if (stat instanceof Hp) {
            alive = stat.value() > 0;
        }
    }
}
