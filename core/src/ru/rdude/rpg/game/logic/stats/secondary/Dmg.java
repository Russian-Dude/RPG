package ru.rdude.rpg.game.logic.stats.secondary;


import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.*;

import java.util.Comparator;
import java.util.List;

public class Dmg extends Stat implements Calculatable {

    private boolean calculatable;
    private Lvl lvl;
    private Str str;
    private Agi agi;
    private Dex dex;
    private Int intel;

    private Melee melee;
    private Range range;
    private Magic magic;

    public Dmg() {
        this.calculatable = false;
        this.melee = new Melee();
        this.range = new Range();
        this.magic = new Magic();
    }

    public Dmg(Str str, Agi agi, Dex dex, Int intel, Lvl lvl) {
        this.calculatable = false;
        this.lvl = lvl;
        this.str = str;
        this.agi = agi;
        this.dex = dex;
        this.intel = intel;
        lvl.subscribe(this);
        str.subscribe(this);
        agi.subscribe(this);
        dex.subscribe(this);
        intel.subscribe(this);
        this.melee = new Melee();
        this.range = new Range();
        this.magic = new Magic();
        calculate();
    }

    public Melee melee() { return melee; }
    public Range range() { return range; }
    public Magic magic() { return magic; }

    public AtkType get(AttackType attackType) {
        switch (attackType) {
            case MAGIC:
                return magic();
            case RANGE:
                return range();
            case MELEE:
                return melee();
            default:
                return getStrongest();
        }
    }

    public AtkType getStrongest() {
        List<AtkType> types = List.of(magic, range, melee);
        return types.stream()
                .max(Comparator.naturalOrder())
                .get();
    }

    public void setCalculatable(boolean calculatable) {
        this.calculatable = calculatable;
    }

    @Override
    public double increase(Stat stat) {
        if (!(stat instanceof Dmg))
            return super.increase(stat);
        else {
            this.melee.min.increase(((Dmg) stat).melee.min.value());
            this.melee.max.increase(((Dmg) stat).melee.max.value());
            this.range.min.increase(((Dmg) stat).range.min.value());
            this.range.max.increase(((Dmg) stat).range.max.value());
            this.magic.min.increase(((Dmg) stat).magic.min.value());
            this.magic.max.increase(((Dmg) stat).magic.max.value());
            return value();
        }
    }

    @Override
    public double calculate() {
        if (!calculatable) return value();
        this.set(Math.max(melee.calculate(),
                Math.max(range.calculate(),
                        magic.calculate())));
        return value();
    }

    public class AtkType implements Comparable<AtkType> {
        public Max max;
        public Min min;

        public Max max() { return max; }
        public Min min() { return min; }
        public double maxValue() { return max.value(); }
        public double minValue() { return min.value(); }
        public double randomValue() { return Functions.random(min.value(), max.value()); }

        public abstract class Max extends Stat {}
        public abstract class Min extends Stat {}

        @Override
        public int compareTo(AtkType atkType) {
            return (int) (minValue() + maxValue() - atkType.minValue() - atkType.maxValue());
        }
    }

    public class Melee extends AtkType implements Calculatable {
        public Melee() {
            this.min = new MeleeMin();
            this.max = new MeleeMax();
        }

        // calculate min and max
        // return middle value of min and max
        @Override
        public double calculate() {
            double STR = str.value();
            double AGI = agi.value();
            double DEX = dex.value();
            double LVL = lvl.value();
            this.min().set(Math.floor(STR + Math.floor(STR/3)*1.5 + Math.floor(STR/5)*1.7 + Math.floor(AGI/3 + DEX/3)/5)*1.7
                    + Math.floor(AGI + DEX/5) + LVL + Math.floor(LVL/3)*1.5 + Math.floor(LVL/5)*1.7);
            this.max().set(Math.floor(STR + Math.floor(STR/2)*1.5 + Math.floor(STR/7)*1.8 + Math.floor(AGI/3 + DEX/3)/5)*1.8
                    + Math.floor(AGI + DEX/5) + LVL + Math.floor(LVL)/3*1.5 + Math.floor(LVL)/4*1.7 + 1);
            return (this.minValue() + this.maxValue())/2;
        }

        @Override
        public void setCalculatable(boolean calculatable) {

        }

        public class MeleeMin extends Min {}
        public class MeleeMax extends Max {}
    }

    public class Range extends AtkType implements Calculatable {

        public Range() {
            this.min = new RangeMin();
            this.max = new RangeMax();
        }

        @Override
        public double calculate() {
            double STR = str.value();
            double AGI = agi.value();
            double DEX = dex.value();
            double LVL = lvl.value();
            this.min().set(Math.floor(DEX + Math.floor(DEX/3)*1.5 + Math.floor(DEX/5)*1.7 + Math.floor(AGI/3 + STR/3)/5)*1.7
                    + Math.floor(AGI + STR/5) + LVL + Math.floor(LVL/3)*1.5 + Math.floor(LVL/5)*1.7);
            this.max.set(Math.floor(DEX + Math.floor(DEX/2)*1.5 + Math.floor(DEX/7)*1.8 + Math.floor(AGI/3 + STR/3)/5)*1.8
                    + Math.floor(AGI + STR/5) + LVL + Math.floor(LVL)/3*1.5 + Math.floor(LVL)/4*1.7 + 1);
            return (this.minValue() + this.maxValue())/2;
        }

        @Override
        public void setCalculatable(boolean calculatable) {

        }

        public class RangeMin extends Min {}
        public class RangeMax extends Max {}
    }

    public class Magic extends AtkType implements Calculatable {

        public Magic() {
            this.min = new MagicMin();
            this.max = new MagicMax();
        }

        @Override
        public double calculate() {
            double INT = intel.value();
            double AGI = agi.value();
            double DEX = dex.value();
            double LVL = lvl.value();
            this.min.set(Math.floor(INT + Math.floor(INT/3)*1.5 + Math.floor(INT/5)*1.7 + Math.floor(AGI/3 + DEX/3)/5)*1.7
                    + Math.floor(AGI + DEX/5) + LVL + Math.floor(LVL/3)*1.5 + Math.floor(LVL/5)*1.7);
            this.max.set(Math.floor(INT + Math.floor(INT/2)*1.5 + Math.floor(INT/7)*1.8 + Math.floor(AGI/3 + DEX/3)/5)*1.8
                    + Math.floor(AGI + DEX/5) + LVL + Math.floor(LVL)/3*1.5 + Math.floor(LVL)/4*1.7 + 1);
            return (this.minValue() + this.maxValue())/2;
        }

        @Override
        public void setCalculatable(boolean calculatable) {

        }

        public class MagicMin extends Min {}
        public class MagicMax extends Max {}
    }

}
