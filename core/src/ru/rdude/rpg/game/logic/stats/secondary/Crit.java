package ru.rdude.rpg.game.logic.stats.secondary;


import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.Luck;

public class Crit extends Stat implements Calculatable {

    private boolean calculatable;
    private Luck luck;

    public Crit(double value) {
        super(value);
        this.calculatable = false;
    }

    public Crit(double value, Luck luck) {
        super(value);
        this.calculatable = false;
        this.luck = luck;
        luck.subscribe(this);
    }

    public void setCalculatable(boolean calculatable) {
        this.calculatable = calculatable;
    }

    @Override
    public double calculate() {
        if (!calculatable) return value();
        double LUCK = luck.value();
        set(0.5 + LUCK*0.2 + Math.floor(LUCK/7) + Math.floor(LUCK/12) + Math.floor(LUCK/2)*0.1 + Math.floor(LUCK/3)*0.1 + Math.floor(LUCK/4)*0.1 + Math.floor(LUCK/5)*0.2);
        return value();
    }
}
