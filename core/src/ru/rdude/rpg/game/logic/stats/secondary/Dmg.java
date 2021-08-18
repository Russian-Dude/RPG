package ru.rdude.rpg.game.logic.stats.secondary;


import com.fasterxml.jackson.annotation.*;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.stats.RoundStat;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.*;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import static java.lang.Math.*;

import java.util.Comparator;
import java.util.List;

@JsonPolymorphicSubType("dmg")
public class Dmg extends Stat implements Calculatable, RoundStat {

    private boolean calculatable;
    @JsonIdentityReference(alwaysAsId = true)
    private Lvl lvl;
    @JsonIdentityReference(alwaysAsId = true)
    private Str str;
    @JsonIdentityReference(alwaysAsId = true)
    private Agi agi;
    @JsonIdentityReference(alwaysAsId = true)
    private Dex dex;
    @JsonIdentityReference(alwaysAsId = true)
    private Int intel;

    private Melee melee;
    private Range range;
    private Magic magic;

    public Dmg() {
        this.calculatable = false;
        this.melee = new Melee(this);
        this.range = new Range(this);
        this.magic = new Magic(this);
    }

    @Override
    public String getName() {
        return "Damage";
    }

    public Dmg(Str str, Agi agi, Dex dex, Int intel, Lvl lvl) {
        this.calculatable = false;
        this.lvl = lvl;
        this.str = str;
        this.agi = agi;
        this.dex = dex;
        this.intel = intel;
        this.melee = new Melee(this);
        this.range = new Range(this);
        this.magic = new Magic(this);
        lvl.subscribe(this);
        str.subscribe(this);
        agi.subscribe(this);
        dex.subscribe(this);
        intel.subscribe(this);
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
            this.melee.min.increase(Math.round(((Dmg) stat).melee.min.value()));
            this.melee.max.increase(Math.round(((Dmg) stat).melee.max.value()));
            this.range.min.increase(Math.round(((Dmg) stat).range.min.value()));
            this.range.max.increase(Math.round(((Dmg) stat).range.max.value()));
            this.magic.min.increase(Math.round(((Dmg) stat).magic.min.value()));
            this.magic.max.increase(Math.round(((Dmg) stat).magic.max.value()));
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

    @JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
    public static class AtkType implements Comparable<AtkType> {

        public Max max;
        public Min min;

        public Max max() { return max; }
        public Min min() { return min; }
        public double maxValue() { return max.value(); }
        public double minValue() { return min.value(); }
        public double randomValue() { return Functions.random(min.value(), max.value()); }

        public static abstract class Max extends Stat {}
        public static abstract class Min extends Stat {}

        @Override
        public int compareTo(AtkType atkType) {
            return (int) (minValue() + maxValue() - atkType.minValue() - atkType.maxValue());
        }
    }

    @JsonPolymorphicSubType("melee")
    public static class Melee extends AtkType implements Calculatable {

        @JsonIdentityReference(alwaysAsId = true)
        private Dmg dmg;

        private Melee() { }

        public Melee(Dmg dmg) {
            this.dmg = dmg;
            this.min = new MeleeMin();
            this.max = new MeleeMax();
        }

        // calculate min and max
        // return middle value of min and max
        @Override
        public double calculate() {
            if (dmg.calculatable) {
                double STR = dmg.str.value();
                double AGI = dmg.agi.value();
                double DEX = dmg.dex.value();
                double LVL = dmg.lvl.value();
                this.min.set(LVL + floor(LVL / 3) + STR + floor(STR / 5) + floor(STR / 7) + floor(AGI / 5) + floor(DEX / 5));
                this.max.set(LVL + floor(LVL / 3) + floor(LVL / 5) + STR + floor(STR / 3) + floor(STR / 5) + floor(STR / 7) + floor(STR / 10)
                        + floor(AGI / 3) + floor(DEX / 3) + 1);
            }
/*            this.min().set(Math.floor(STR + Math.floor(STR/3)*1.5 + Math.floor(STR/5)*1.7 + Math.floor(AGI/3 + DEX/3)/5)*1.7
                    + Math.floor(AGI + DEX/5) + LVL + Math.floor(LVL/3)*1.5 + Math.floor(LVL/5)*1.7);
            this.max().set(Math.floor(STR + Math.floor(STR/2)*1.5 + Math.floor(STR/7)*1.8 + Math.floor(AGI/3 + DEX/3)/5)*1.8
                    + Math.floor(AGI + DEX/5) + LVL + Math.floor(LVL)/3*1.5 + Math.floor(LVL)/4*1.7 + 1);*/
            return (this.minValue() + this.maxValue())/2;
        }

        @Override
        public void setCalculatable(boolean calculatable) {
        }

        @JsonPolymorphicSubType("meleeMin")
        public static class MeleeMin extends Min {
            @Override
            public String getName() {
                return "Melee minimum damage";
            }
        }
        @JsonPolymorphicSubType("meleeMax")
        public static class MeleeMax extends Max {
            @Override
            public String getName() {
                return "Melee maximum damage";
            }
        }
    }

    @JsonPolymorphicSubType("range")
    public static class Range extends AtkType implements Calculatable {

        private Dmg dmg;

        private Range() { }

        public Range(Dmg dmg) {
            this.dmg = dmg;
            this.min = new RangeMin();
            this.max = new RangeMax();
        }

        @Override
        public double calculate() {
            if (dmg.calculatable) {
                double STR = dmg.str.value();
                double AGI = dmg.agi.value();
                double DEX = dmg.dex.value();
                double LVL = dmg.lvl.value();
                this.min.set(LVL + floor(LVL / 3) + DEX + floor(DEX / 5) + floor(DEX / 7) + floor(AGI / 5) + floor(STR / 5));
                this.max.set(LVL + floor(LVL / 3) + floor(LVL / 5) + DEX + floor(DEX / 3) + floor(DEX / 5) + floor(DEX / 7) + floor(DEX / 10)
                        + floor(AGI / 3) + floor(STR / 3) + 1);
            }
/*            this.min().set(Math.floor(DEX + Math.floor(DEX/3)*1.5 + Math.floor(DEX/5)*1.7 + Math.floor(AGI/3 + STR/3)/5)*1.7
                    + Math.floor(AGI + STR/5) + LVL + Math.floor(LVL/3)*1.5 + Math.floor(LVL/5)*1.7);
            this.max.set(Math.floor(DEX + Math.floor(DEX/2)*1.5 + Math.floor(DEX/7)*1.8 + Math.floor(AGI/3 + STR/3)/5)*1.8
                    + Math.floor(AGI + STR/5) + LVL + Math.floor(LVL)/3*1.5 + Math.floor(LVL)/4*1.7 + 1);*/
            return (this.minValue() + this.maxValue())/2;
        }

        @Override
        public void setCalculatable(boolean calculatable) {
        }

        @JsonPolymorphicSubType("rangeMin")
        public static class RangeMin extends Min {
            @Override
            public String getName() {
                return "Range minimum damage";
            }
        }
        @JsonPolymorphicSubType("rangeMax")
        public static class RangeMax extends Max {
            @Override
            public String getName() {
                return "Range maximum damage";
            }
        }
    }

    @JsonPolymorphicSubType("magic")
    public static class Magic extends AtkType implements Calculatable {

        private Dmg dmg;

        private Magic() { }

        public Magic(Dmg dmg) {
            this.dmg = dmg;
            this.min = new MagicMin();
            this.max = new MagicMax();
        }

        @Override
        public double calculate() {
            if (dmg.calculatable) {
                double INT = dmg.intel.value();
                double AGI = dmg.agi.value();
                double DEX = dmg.dex.value();
                double LVL = dmg.lvl.value();
                this.min.set(LVL + floor(LVL / 3) + INT + floor(INT / 5) + floor(INT / 7) + floor(AGI / 5) + floor(DEX / 5));
                this.max.set(LVL + floor(LVL / 3) + floor(LVL / 5) + INT + floor(INT / 3) + floor(INT / 5) + floor(INT / 7) + floor(INT / 10)
                        + floor(AGI / 3) + floor(DEX / 3) + 1);
            }
/*            this.min.set(Math.floor(INT + Math.floor(INT/3)*1.5 + Math.floor(INT/5)*1.7 + Math.floor(AGI/3 + DEX/3)/5)*1.7
                    + Math.floor(AGI + DEX/5) + LVL + Math.floor(LVL/3)*1.5 + Math.floor(LVL/5)*1.7);
            this.max.set(Math.floor(INT + Math.floor(INT/2)*1.5 + Math.floor(INT/7)*1.8 + Math.floor(AGI/3 + DEX/3)/5)*1.8
                    + Math.floor(AGI + DEX/5) + LVL + Math.floor(LVL)/3*1.5 + Math.floor(LVL)/4*1.7 + 1);*/
            return (this.minValue() + this.maxValue())/2;
        }

        @Override
        public void setCalculatable(boolean calculatable) {
        }

        @JsonPolymorphicSubType("magicMin")
        public static class MagicMin extends Min {
            @Override
            public String getName() {
                return "Magic minimum damage";
            }
        }
        @JsonPolymorphicSubType("magicMax")
        public static class MagicMax extends Max {
            @Override
            public String getName() {
                return "Magic maximum damage";
            }
        }
    }

}
