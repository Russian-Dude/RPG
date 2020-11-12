package ru.rdude.rpg.game.logic.stats;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import ru.rdude.rpg.game.logic.stats.primary.*;
import ru.rdude.rpg.game.logic.stats.secondary.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Stats implements StatObserver {

    private Set<StatsObserver> subscribers;
    private boolean calculatable;
    private Map<Class<? extends Stat>, Stat> stats;


    public Stats(boolean calculatable) {
        subscribers = new HashSet<>();
        stats = new HashMap<>();
        // primary:
        stats.put(Agi.class, new Agi());
        stats.put(Dex.class, new Dex());
        stats.put(Int.class, new Int());
        stats.put(Luck.class, new Luck());
        stats.put(Lvl.class, new Lvl(Lvl.BASE));
        stats.put(Str.class, new Str());
        stats.put(Vit.class, new Vit());
        // secondary:
        stats.put(Block.class, new Block(0, dex(), agi(), lvl()));
        stats.put(Concentration.class, new Concentration(0, intel(), dex(), agi(), vit(), str(), lvl()));
        stats.put(Crit.class, new Crit(0, luck()));
        stats.put(Def.class, new Def(0, vit(), lvl()));
        stats.put(Dmg.class, new Dmg(str(), agi(), dex(), intel(), lvl()));
        stats.put(Flee.class, new Flee(0, agi(), dex(), luck(), lvl()));
        stats.put(Hit.class, new Hit(0, dex(), agi(), lvl()));
        stats.put(MagicResistance.class, new MagicResistance(0, intel(), luck(), str(), dex(), agi(), vit(), lvl()));
        stats.put(Parry.class, new Parry(0, agi(), dex(), lvl()));
        stats.put(PhysicResistance.class, new PhysicResistance(0, intel(), luck(), str(), dex(), agi(), vit(), lvl()));
        stats.put(Hp.class, new Hp(0, vit(), lvl()));
        stats.put(Stm.class, new Stm(0, agi(), vit(), dex(), str(), lvl()));
        // calculatble:
        setCalculatable(calculatable);
        // subscribe:
        stats.values().forEach(stat -> stat.subscribe(this));
    }

    public Agi agi() {
        return (Agi) stats.get(Agi.class);
    }

    public Dex dex() {
        return (Dex) stats.get(Dex.class);
    }

    public Int intel() {
        return (Int) stats.get(Int.class);
    }

    public Luck luck() {
        return (Luck) stats.get(Luck.class);
    }

    public Lvl lvl() {
        return (Lvl) stats.get(Lvl.class);
    }

    public Str str() {
        return (Str) stats.get(Str.class);
    }

    public Vit vit() {
        return (Vit) stats.get(Vit.class);
    }

    public double agiValue() {
        return stats.get(Agi.class).value();
    }

    public double dexValue() {
        return stats.get(Dex.class).value();
    }

    public double intelValue() {
        return stats.get(Int.class).value();
    }

    public double luckValue() {
        return stats.get(Luck.class).value();
    }

    public double lvlValue() {
        return stats.get(Lvl.class).value();
    }

    public double strValue() {
        return stats.get(Str.class).value();
    }

    public double vitValue() {
        return stats.get(Vit.class).value();
    }

    public Block block() {
        return (Block) stats.get(Block.class);
    }

    public Concentration concentration() {
        return (Concentration) stats.get(Concentration.class);
    }

    public Crit crit() {
        return (Crit) stats.get(Crit.class);
    }

    public Def def() {
        return (Def) stats.get(Def.class);
    }

    public Dmg dmg() {
        return (Dmg) stats.get(Dmg.class);
    }

    public Flee flee() {
        return (Flee) stats.get(Flee.class);
    }

    public Hit hit() {
        return (Hit) stats.get(Hit.class);
    }

    public MagicResistance magicResistance() {
        return (MagicResistance) stats.get(MagicResistance.class);
    }

    public Parry parry() {
        return (Parry) stats.get(Parry.class);
    }

    public PhysicResistance physicResistance() {
        return (PhysicResistance) stats.get(PhysicResistance.class);
    }

    public double blockValue() {
        return stats.get(Block.class).value();
    }

    public double concentrationValue() {
        return stats.get(Concentration.class).value();
    }

    public double critValue() {
        return stats.get(Crit.class).value();
    }

    public double defValue() {
        return stats.get(Def.class).value();
    }

    public double dmgValue() {
        return stats.get(Dmg.class).value();
    }

    public double fleeValue() {
        return stats.get(Flee.class).value();
    }

    public double hitValue() {
        return stats.get(Hit.class).value();
    }

    public double magicResistanceValue() {
        return stats.get(MagicResistance.class).value();
    }

    public double parryValue() {
        return stats.get(Parry.class).value();
    }

    public double physicResistanceValue() {
        return stats.get(PhysicResistance.class).value();
    }

    public Hp hp() {
        return (Hp) stats.get(Hp.class);
    }

    public Stm stm() {
        return (Stm) stats.get(Stm.class);
    }

    public double hpValue() {
        return hp().value();
    }

    public double stmValue() {
        return stm().value();
    }

    public Stat get(Class<? extends Stat> statClass) {
        if (stats.containsKey(statClass)) {
            return stats.get(statClass);
        } else {
            AtomicReference<Stat> result = new AtomicReference<>();
            forEachWithNestedStats(stat -> {
                if (statClass.isAssignableFrom(stat.getClass())) {
                    result.set(stat);
                }
            });
            if (result.get() != null) {
                return result.get();
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    public void addBuffClass(Class<?> clazz) {
        stats.values().forEach(stat -> stat.addBuffClass(clazz));
    }

    public void increase(Stats stats) {
        this.stats.values().forEach(stat -> stat.increase(stats.stats.get(stat.getClass())));
    }

    public void decrease(Stats stats) {
        this.stats.values().forEach(stat -> stat.decrease(stats.stats.get(stat.getClass()).value()));
    }

    public void increaseBuffValues(Class<?> clazz, Stats stats) {
        this.stats.values().forEach(stat -> stat.increaseBuffValue(clazz, stats.stats.get(stat.getClass()).value()));
    }

    public void decreaseBuffValues(Class<?> clazz, Stats stats) {
        this.stats.values().forEach(stat -> stat.decreaseBuffValue(clazz, stats.stats.get(stat.getClass()).value()));
    }

    public boolean isCalculatable() {
        return calculatable;
    }

    public void setCalculatable(boolean calculatable) {
        this.calculatable = calculatable;
        stats.values().stream()
                .filter(stat -> stat instanceof Calculatable)
                .forEach(stat -> ((Calculatable) stat).setCalculatable(calculatable));
    }

    public void clear() {
        stats.values().forEach(stat -> stat.set(0d));
    }

    public void forEach(Consumer<? super Stat> action) {
        stats.values().forEach(action);
    }

    public void forEachWithNestedStats(Consumer<? super Stat> action) {
        Stream.concat(
                stats.values().stream()
                        .filter(stat -> stat.getClass() != Dmg.class),
                Stream.of(
                        lvl().exp(),
                        dmg().melee().min(),
                        dmg().melee().max(),
                        dmg().range().min(),
                        dmg().range().max(),
                        dmg().magic().min(),
                        dmg().magic().max(),
                        flee().luckyDodgeChance(),
                        stm().hardness(),
                        stm().max(),
                        stm().perHit(),
                        stm().recovery(),
                        hp().max(),
                        hp().recovery()
                )).forEach(action);
    }

    public Stream<Stat> stream() {
        return stats.values().stream();
    }

    public void subscribe(StatsObserver subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(StatsObserver subscriber) {
        subscribers.remove(subscriber);
    }

    private void notifySubscribers() {
        subscribers.forEach(subscriber -> subscriber.update(this));
    }

    @Override
    public void update(Stat stat) {
        notifySubscribers();
    }
}
