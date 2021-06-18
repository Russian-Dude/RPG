package ru.rdude.rpg.game.logic.stats;

import ru.rdude.rpg.game.utils.SubscribersManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Stat implements Comparable<Stat>, StatObserver {

    protected SubscribersManager<StatObserver> subscribers;
    protected double value;
    protected Map<String, Double> buffs;

    public Stat() {
        this(0);
    }

    public Stat(double value) {
        this.value = value;
        buffs = new HashMap<>();
        subscribers = new SubscribersManager<>();
    }

    public abstract String getName();

    public double increase(double value) {
        if (value == 0) return this.value();
        this.value += value;
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
        this.value = value;
        notifySubscribers();
    }

    public void forceCalculate() {
        if (Calculatable.class.isAssignableFrom(this.getClass()))
            ((Calculatable) this).calculate();
    }

    public double value() {
        return buffs.values().stream().reduce(Double::sum).orElse(0d) + value;
    }

    public double pureValue() { return this.value; }

    public void increaseBuffValue(Class<?> clazz, Stat stat) {
        increaseBuffValue(clazz, stat.value());
    }

    public void increaseBuffValue(Class<?> clazz, double value) {
        increaseBuffValue(clazz.getSimpleName(), value);
    }

    public void increaseBuffValue(String className, double value) {
        buffs.merge(className, value, Double::sum);
        notifySubscribers();
    }

    public void decreaseBuffValue(Class<?> clazz, double value) {
        increaseBuffValue(clazz, value * (-1));
    }

    public void setBuffValue(Class<?> clazz, Stat stat) {
        setBuffValue(clazz, stat.value());
    }

    public void setBuffValue(Class<?> clazz, double value) {
        setBuffValue(clazz.getSimpleName(), value);
    }

    public void setBuffValue(String className, double value) {
        buffs.put(className, value);
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
