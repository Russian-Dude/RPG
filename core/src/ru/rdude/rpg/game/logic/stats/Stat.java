package ru.rdude.rpg.game.logic.stats;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ru.rdude.rpg.game.logic.entities.beings.BeingActionObserver;
import ru.rdude.rpg.game.logic.entities.items.ItemCountObserver;
import ru.rdude.rpg.game.logic.entities.skills.BuffObserver;
import ru.rdude.rpg.game.logic.game.CurrentGameObserver;
import ru.rdude.rpg.game.logic.gameStates.GameStateObserver;
import ru.rdude.rpg.game.logic.holders.SlotObserver;
import ru.rdude.rpg.game.logic.map.MapGenerationObserver;
import ru.rdude.rpg.game.logic.map.PlaceObserver;
import ru.rdude.rpg.game.logic.statistics.StatisticValueObserver;
import ru.rdude.rpg.game.logic.stats.primary.*;
import ru.rdude.rpg.game.logic.stats.secondary.Block;
import ru.rdude.rpg.game.logic.stats.secondary.Concentration;
import ru.rdude.rpg.game.logic.stats.secondary.Crit;
import ru.rdude.rpg.game.logic.stats.secondary.Dmg;
import ru.rdude.rpg.game.logic.time.DurationObserver;
import ru.rdude.rpg.game.logic.time.TimeChangeObserver;
import ru.rdude.rpg.game.logic.time.TimeObserver;
import ru.rdude.rpg.game.logic.time.TurnChangeObserver;
import ru.rdude.rpg.game.utils.SubscribersManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public abstract class Stat implements Comparable<Stat>, StatObserver {

    protected SubscribersManager<StatObserver> subscribers;
    protected double value;
    protected Map<String, Double> buffs;

    public Stat() {
        this(0);
    }

    public Stat(double value) {
        this.value = (this instanceof RoundStat) ? Math.round(value) : value;
        buffs = new HashMap<>();
        subscribers = new SubscribersManager<>();
    }

    public abstract String getName();

    public double increase(double value) {
        if (value == 0) return this.value();
        this.value += (this instanceof RoundStat) ? Math.round(value) : value;
        notifySubscribers();
        return this.value();
    }

    public double decrease(double value) {
        return increase(value * (-1));
    }

    public double increase(Stat stat) {
        return this.increase(stat.value());
    }

    public void set(double value) {
        this.value = (this instanceof RoundStat) ? Math.round(value) : value;
        notifySubscribers();
    }

    public void forceCalculate() {
        if (Calculatable.class.isAssignableFrom(this.getClass()))
            ((Calculatable) this).calculate();
    }

    public double value() {
        final double value = buffs.values().stream().reduce(Double::sum).orElse(0d) + this.value;
        return (this instanceof RoundStat) ? Math.round(value) : value;
    }

    public double pureValue() { return (this instanceof RoundStat) ? Math.round(value) : value; }

    public void increaseBuffValue(Class<?> clazz, Stat stat) {
        increaseBuffValue(clazz, stat.value());
    }

    public void increaseBuffValue(Class<?> clazz, double value) {
        increaseBuffValue(clazz.getSimpleName(), (this instanceof RoundStat) ? Math.round(value) : value);
    }

    public void increaseBuffValue(String className, double value) {
        buffs.merge(className, (this instanceof RoundStat) ? Math.round(value) : value, Double::sum);
        notifySubscribers();
    }

    public void decreaseBuffValue(Class<?> clazz, double value) {
        increaseBuffValue(clazz, value * (-1));
    }

    public void setBuffValue(Class<?> clazz, Stat stat) {
        final double value = stat.value();
        setBuffValue(clazz, (this instanceof RoundStat) ? Math.round(value) : value);
    }

    public void setBuffValue(Class<?> clazz, double value) {
        setBuffValue(clazz.getSimpleName(), value);
    }

    public void setBuffValue(String className, double value) {
        buffs.put(className, (this instanceof RoundStat) ? Math.round(value) : value);
        notifySubscribers();
    }

    public double getBuffValue(Class<?> clazz) {
        return getBuffValue(clazz.getSimpleName());
    }

    public double getBuffValue(String className) {
        return buffs.getOrDefault(className, 0d);
    }

    public Map<String, Double> getBuffs() {
        return buffs;
    }

    public void setBuffs(Map<String, Double> buffs) {
        this.buffs = buffs;
    }

    public void subscribe(StatObserver observer) {
        this.subscribers.subscribe(observer);
    }

    public void unsubscribe(StatObserver observer) {
        this.subscribers.unsubscribe(observer);
    }

    protected void notifySubscribers() {
        subscribers.notifySubscribers(subscriber -> subscriber.update(this));
    }

    @Override
    public void update(Stat stat) {
        forceCalculate();
    }

    @Override
    public int compareTo(Stat stat) {
        return (int) (value() - stat.value());
    }
}
