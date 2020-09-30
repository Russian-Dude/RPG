package ru.rdude.rpg.game.logic.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Stat implements Comparable<Stat>, StatObserver {

    protected Set<StatObserver> subscribers;
    private double value;
    private Map<String, Double> buffs;

    public Stat() {
        this(0);
    }

    public Stat(double value) {
        this.value = value;
        buffs = new HashMap<>();
        subscribers = new HashSet<>();
    }

    public void addBuffClass(String className) {
        buffs.put(className, 0d);
    }

    public void addBuffClass(Class<?> clazz) {
        addBuffClass(clazz.getSimpleName());
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

    public double pureValue() { return this.value; }

    public void increaseBuffValue(Class<?> clazz, Stat stat) {
        increaseBuffValue(clazz, stat.value());
    }

    public void increaseBuffValue(Class<?> clazz, double value) {
        increaseBuffValue(clazz.getSimpleName(), value);
    }

    public void increaseBuffValue(String className, double value) {
        if (!buffs.containsKey(className))
            throw new IllegalArgumentException(" class is not presented as buff field");
        buffs.put(className, buffs.get(className) + value);
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
        if (!buffs.containsKey(className))
            throw new IllegalArgumentException(className + " class is not presented as buff field");
        buffs.put(className, value);
        notifySubscribers();
    }

    public Map<String, Double> getBuffs() {
        return buffs;
    }

    public void setBuffs(Map<String, Double> buffs) {
        this.buffs = buffs;
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

    @Override
    public int compareTo(Stat stat) {
        return (int) (value() - stat.value());
    }
}
