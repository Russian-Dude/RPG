package ru.rdude.rpg.game.logic.stats.primary;


import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("lvl")
public class Lvl extends Stat implements Calculatable {

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
        statPoints.increase(2);
        if (value() % 3 == 0) statPoints.increase(1);
        if (value() % 10 == 0) statPoints.increase(1);
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
    public static class StatPoints extends Stat {
        @Override
        public String getName() {
            return "Stat Points";
        }
    }


    public static abstract class Exp extends Stat implements Calculatable {

        protected Lvl lvl;

        Exp() { }

        public Exp(Lvl lvl) {
            this.lvl = lvl;
        }

        protected abstract double calculate(double lvl);
        protected double max;

        public double getMax() { return max; }
        public void setMax(double max) { this.max = max; }

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
            for (boolean isLvlUp = this.value() >= this.max; isLvlUp; isLvlUp = this.value() >= this.max) {
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
                if (value() < max) return max;
                max = 170 + lvl*180 + Math.floor(lvl/2)*180 + Math.floor(lvl/3)*240;
            }
            return max;
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
                if (value() < max) return max;
                max = 100 + lvl * 150 + Math.floor(lvl/2)*160 + Math.floor(lvl/3)*185;
            }
            return max;
        }

        @Override
        public void setCalculatable(boolean calculatable) {
        }

        @Override
        public String getName() {
            return "Experience";
        }
    }

}
