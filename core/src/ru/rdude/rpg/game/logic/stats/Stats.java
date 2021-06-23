package ru.rdude.rpg.game.logic.stats;


import ru.rdude.rpg.game.logic.enums.StatName;
import ru.rdude.rpg.game.logic.stats.primary.*;
import ru.rdude.rpg.game.logic.stats.secondary.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Stats {

    private boolean calculatable;
    private Map<StatName, Stat> stats;

    private Stats() { }

    public Stats(boolean calculatable) {
        stats = new HashMap<>();
        // primary:
        stats.put(StatName.AGI, new Agi());
        stats.put(StatName.DEX, new Dex());
        stats.put(StatName.INT, new Int());
        stats.put(StatName.LUCK, new Luck());
        stats.put(StatName.LVL, new Lvl(Lvl.BASE));
        stats.put(StatName.STR, new Str());
        stats.put(StatName.VIT, new Vit());
        // secondary:
        stats.put(StatName.BLOCK, new Block(0, dex(), agi(), lvl()));
        stats.put(StatName.CONCENTRATION, new Concentration(0, intel(), dex(), agi(), vit(), str(), lvl()));
        stats.put(StatName.CRIT, new Crit(0, luck()));
        stats.put(StatName.DEF, new Def(0, vit(), lvl()));
        stats.put(StatName.DAMAGE, new Dmg(str(), agi(), dex(), intel(), lvl()));
        stats.put(StatName.FLEE, new Flee(0, agi(), dex(), luck(), lvl()));
        stats.put(StatName.HIT, new Hit(0, dex(), agi(), lvl()));
        stats.put(StatName.MAGIC_RESISTANCE, new MagicResistance(0, intel(), luck(), str(), dex(), agi(), vit(), lvl()));
        stats.put(StatName.PARRY, new Parry(0, agi(), dex(), lvl()));
        stats.put(StatName.PHYSIC_RESISTANCE, new PhysicResistance(0, intel(), luck(), str(), dex(), agi(), vit(), lvl()));
        stats.put(StatName.HP, new Hp(0, vit(), lvl()));
        stats.put(StatName.STM, new Stm(0, agi(), vit(), dex(), str(), lvl()));
        // calculatble:
        setCalculatable(calculatable);
        // subscribe:
        if (calculatable) {
            stats.values().forEach(Stat::forceCalculate);
            hp().set(hp().max().value());
            stm().set(stm().maxValue());
        }
    }

    public Agi agi() {
        return (Agi) stats.get(StatName.AGI);
    }

    public Dex dex() {
        return (Dex) stats.get(StatName.DEX);
    }

    public Int intel() {
        return (Int) stats.get(StatName.INT);
    }

    public Luck luck() {
        return (Luck) stats.get(StatName.LUCK);
    }

    public Lvl lvl() {
        return (Lvl) stats.get(StatName.LVL);
    }

    public Str str() {
        return (Str) stats.get(StatName.STR);
    }

    public Vit vit() {
        return (Vit) stats.get(StatName.VIT);
    }

    public double agiValue() {
        return stats.get(StatName.AGI).value();
    }

    public double dexValue() {
        return stats.get(StatName.DEX).value();
    }

    public double intelValue() {
        return stats.get(StatName.INT).value();
    }

    public double luckValue() {
        return stats.get(StatName.LUCK).value();
    }

    public double lvlValue() {
        return stats.get(StatName.LVL).value();
    }

    public double strValue() {
        return stats.get(StatName.STR).value();
    }

    public double vitValue() {
        return stats.get(StatName.VIT).value();
    }

    public Block block() {
        return (Block) stats.get(StatName.BLOCK);
    }

    public Concentration concentration() {
        return (Concentration) stats.get(StatName.CONCENTRATION);
    }

    public Crit crit() {
        return (Crit) stats.get(StatName.CRIT);
    }

    public Def def() {
        return (Def) stats.get(StatName.DEF);
    }

    public Dmg dmg() {
        return (Dmg) stats.get(StatName.DAMAGE);
    }

    public Flee flee() {
        return (Flee) stats.get(StatName.FLEE);
    }

    public Hit hit() {
        return (Hit) stats.get(StatName.HIT);
    }

    public MagicResistance magicResistance() {
        return (MagicResistance) stats.get(StatName.MAGIC_RESISTANCE);
    }

    public Parry parry() {
        return (Parry) stats.get(StatName.PARRY);
    }

    public PhysicResistance physicResistance() {
        return (PhysicResistance) stats.get(StatName.PHYSIC_RESISTANCE);
    }

    public double blockValue() {
        return stats.get(StatName.BLOCK).value();
    }

    public double concentrationValue() {
        return stats.get(StatName.CONCENTRATION).value();
    }

    public double critValue() {
        return stats.get(StatName.CRIT).value();
    }

    public double defValue() {
        return stats.get(StatName.DEF).value();
    }

    public double dmgValue() {
        return stats.get(StatName.DAMAGE).value();
    }

    public double fleeValue() {
        return stats.get(StatName.FLEE).value();
    }

    public double hitValue() {
        return stats.get(StatName.HIT).value();
    }

    public double magicResistanceValue() {
        return stats.get(StatName.MAGIC_RESISTANCE).value();
    }

    public double parryValue() {
        return stats.get(StatName.PARRY).value();
    }

    public double physicResistanceValue() {
        return stats.get(StatName.PHYSIC_RESISTANCE).value();
    }

    public Hp hp() {
        return (Hp) stats.get(StatName.HP);
    }

    public Stm stm() {
        return (Stm) stats.get(StatName.STM);
    }

    public double hpValue() {
        return hp().value();
    }

    public double stmValue() {
        return stm().value();
    }

    public Stat get(StatName statName) {
        final Stat stat = stats.get(statName);
        return stat != null ? stat : get(statName.getClazz());
    }

    public Stat get(Class<? extends Stat> statClass) {
        return streamWithNestedStats()
                .filter(stat -> stat.getClass().equals(statClass))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no stat of " + statClass));
    }

    public void increase(Stats stats) {
        this.stats.values().forEach(stat -> stat.increase(stats.get(stat.getClass())));
    }

    public void decrease(Stats stats) {
        this.stats.values().forEach(stat -> stat.decrease(stats.get(stat.getClass()).value()));
    }

    public void increaseBuffValues(Class<?> clazz, Stats stats) {
        this.stats.values().forEach(stat -> stat.increaseBuffValue(clazz, stats.get(stat.getClass()).value()));
    }

    public void decreaseBuffValues(Class<?> clazz, Stats stats) {
        this.stats.values().forEach(stat -> stat.decreaseBuffValue(clazz, stats.get(stat.getClass()).value()));
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

    public boolean isMatchRequirementsOf(Stats stats) {
        return streamWithNestedStats()
                .allMatch(stat -> stat.value() >= stats.get(stat.getClass()).value());
    }

    public void forEachWithNestedStats(Consumer<? super Stat> action) {
        streamWithNestedStats().forEach(action);
    }

    public Stream<Stat> stream() {
        return stats.values().stream();
    }

    public Stream<Stat> streamWithNestedStats() {
        return Stream.concat(
                stats.values().stream(),
                Stream.of(
                        lvl().statPoints(),
                        lvl().exp(),
                        flee().luckyDodgeChance(),
                        stm().hardness(),
                        stm().max(),
                        stm().perHit(),
                        stm().recovery(),
                        hp().max(),
                        hp().recovery()
                ));
    }

    public Stats copy(boolean calculatable) {
        Stats stats = new Stats(calculatable);
        copyTo(stats);
        return stats;
    }

    public void copyTo(Stats stats) {
        boolean calculatable = stats.isCalculatable();
        stats.setCalculatable(false);
        forEachWithNestedStats(stat -> {
            Stat copyStat = stats.get(stat.getClass());
            copyStat.set(stat.pureValue());
            stat.getBuffs().forEach(copyStat::setBuffValue);
        });
        stats.setCalculatable(calculatable);
    }
}
