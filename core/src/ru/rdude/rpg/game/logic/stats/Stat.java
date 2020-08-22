package ru.rdude.rpg.game.logic.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Stat implements StatObserver {

    protected Set<StatObserver> subscribers;
    private double value;
    private Map<Class<?>, Double> buffs;

    public Stat() {
        this(0);
    }

    public Stat(double value) {
        this.value = value;
        buffs = new HashMap<>();
        subscribers = new HashSet<>();
    }

    public void addBuffClass(Class<?> clazz) {
        buffs.put(clazz, 0d);
    }

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

    public void increaseBuffValue(Class<?> clazz, Stat stat) {
        increaseBuffValue(clazz, stat.value());
    }

    public void increaseBuffValue(Class<?> clazz, double value) {
        if (!buffs.containsKey(clazz))
            throw new IllegalArgumentException(clazz.getSimpleName() + " class is not presented as buff field");
        buffs.put(clazz, buffs.get(clazz) + value);
        notifySubscribers();
    }

    public void decreaseBuffValue(Class<?> clazz, double value) {
        increaseBuffValue(clazz, value * (-1));
    }

    public void setBuffValue(Class<?> clazz, Stat stat) {
        setBuffValue(clazz, stat.value());
    }

    public void setBuffValue(Class<?> clazz, double value) {
        if (!buffs.containsKey(clazz))
            throw new IllegalArgumentException(clazz.getSimpleName() + " class is not presented as buff field");
        buffs.put(clazz, value);
        notifySubscribers();
    }

    public void subscribe(StatObserver observer) {
        this.subscribers.add(observer);
    }

    public void unsubscribe(StatObserver observer) {
        this.subscribers.remove(observer);
    }

    protected void notifySubscribers() {
        subscribers.forEach(subscriber -> subscriber.update(this));
    }

    @Override
    public void update(Stat stat) {
        forceCalculate();
    }
}
