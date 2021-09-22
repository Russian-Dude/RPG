package ru.rdude.rpg.game.logic.stats.primary;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.RoundStat;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("lvl")
public class Lvl extends Stat implements Calculatable, RoundStat {

    public enum Type {BASE, CLASS}
    public static final Type BASE = Type.BASE;
    public static final Type CLASS = Type.CLASS;

    private boolean calculatable;

    private Type type;
    private Exp exp;
    private StatPoints statPoints;

    private Lvl() { }

    public Lvl(Type type) {
        this.type = type;
        if (type == BASE) exp = new ExpBase(this);
        else if (type == CLASS) exp = new ExpClass(this);
        statPoints = new StatPoints();
    }

    public Exp exp() { return exp; }
    public StatPoints statPoints() { return statPoints; }

    public double expValue() { return exp.value(); }
    public double statPointsValue() { return statPoints.value(); }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    @Override
    public String getName() {
        return "Level";
    }

    @Override
    public double increase(double value) {
        for (int i = 0; i < (int) value; i++) {
            super.increase(1);
            lvlUp();
        }
        return value;
    }

    private void lvlUp() {
        statPoints.increase(type == BASE ? 2 : 1);
        if (value() % 3 == 0 && type == BASE) statPoints.increase(1);
        if (value() % 10 == 0 && type == BASE) statPoints.increase(1);
    }

    @Override
    public double calculate() {
        if (calculatable) {
            exp.calculate();
        }
        return value();
    }

    @Override
    public void setCalculatable(boolean calculatable) {
        this.calculatable = calculatable;
    }


    @JsonPolymorphicSubType("statPoints")
    public static class StatPoints extends Stat implements RoundStat {
        @Override
        public String getName() {
            return "Stat Points";
        }
    }


    @JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
    public static abstract class Exp extends Stat implements Calculatable, RoundStat {

        protected Lvl lvl;

        Exp() { }

        public Exp(Lvl lvl) {
            this.lvl = lvl;
            this.max = new ExpMax();
        }

        protected abstract double calculate(double lvl);
        protected ExpMax max;

        public ExpMax getMax() { return max; }
        public void setMax(double max) { this.max.set(max); }

        @Override
        public double calculate() {
            if (lvl.calculatable) {
                calculate(lvl.value());
            }
            return lvl.value();
        }

        // 'max' works like bound exceeding which lvl up
        // if bound is exceeded - calculate new bound
        @Override
        public double increase(double value) {
            this.set(value() + value);
            for (boolean isLvlUp = this.value() >= this.max.value(); isLvlUp; isLvlUp = this.value() >= this.max.value()) {
                lvl.increase(1);
                calculate();
            }
            return value;
        }
    }

    @JsonPolymorphicSubType("expClass")
    public static class ExpClass extends Exp {

        private ExpClass() { }

        public ExpClass(Lvl lvl) {
            super(lvl);
        }

        @Override
        protected double calculate(double lvl) {
            if (super.lvl.calculatable) {
                if (value() < max.value()) {
                    return max.value();
                }
                set(value - max.value());
                max.set(170 + lvl*180 + Math.floor(lvl/2)*180 + Math.floor(lvl/3)*240);
            }
            return max.value();
        }

        @Override
        public void setCalculatable(boolean calculatable) {
        }

        @Override
        public String getName() {
            return "Class experience";
        }

    }

    @JsonPolymorphicSubType("expBase")
    public static class ExpBase extends Exp {

        private ExpBase() { }

        public ExpBase(Lvl lvl) {
            super(lvl);
        }

        @Override
        protected double calculate(double lvl) {
            if (super.lvl.calculatable) {
                if (value() < max.value()) {
                    return max.value();
                }
                set(value - max.value());
                max.set(100 + lvl * 150 + Math.floor(lvl/2)*160 + Math.floor(lvl/3)*185);
            }
            return max.value();
        }

        @Override
        public void setCalculatable(boolean calculatable) {
        }

        @Override
        public String getName() {
            return "Experience";
        }
    }

    public static class ExpMax extends Stat implements RoundStat {

        @Override
        public String getName() {
            return "Exp for next lvl";
        }
    }


}
