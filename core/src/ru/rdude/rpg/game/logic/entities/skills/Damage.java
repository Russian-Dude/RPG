package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.entities.Entity;

public class Damage extends Skill {

    private Entity interactor;
    private double value;
    private Coefficients coefficients;
    private boolean critical;
    private boolean isMiss;
    private boolean isBlock;
    private boolean isParry;

    public Damage(double value, Entity from) {
        this(value, from, false);
    }

    public Damage(double value, Entity from, boolean critical) {
        this.value = value;
        this.interactor = from;
        this.critical = critical;
        isMiss = false;
        isBlock = false;
        isParry = false;
    }

    public double value() {
        return value;
    }

    public Entity interactor() {
        return interactor;
    }

    public boolean isCritical() { return critical; }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    public boolean isMiss() {
        return isMiss;
    }

    public void setMiss(boolean miss) {
        isMiss = miss;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }

    public boolean isParry() {
        return isParry;
    }

    public void setParry(boolean parry) {
        isParry = parry;
    }

    public Coefficients getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(Coefficients coefficients) {
        this.coefficients = coefficients;
    }
}
