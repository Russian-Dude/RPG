package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("damage")
public class Damage extends Skill {

    private Entity<?> interactor;
    private double value;
    private SkillData bySkill;
    private Coefficients coefficients;
    private boolean critical;
    private boolean isMiss;
    private boolean isDodge;
    private boolean isBlock;
    private boolean isParry;

    private Damage() {
    }

    public Damage(double value, Entity<?> from, SkillData bySkill) {
        this(value, from, bySkill, false);
    }

    public Damage(double value, Entity<?> from, SkillData bySkill, boolean critical) {
        this.value = value;
        this.interactor = from;
        this.bySkill = bySkill;
        this.critical = critical;
        isMiss = false;
        isBlock = false;
        isParry = false;
        isDodge = false;
    }

    public double value() {
        return value;
    }

    public Entity<?> interactor() {
        return interactor;
    }

    public SkillData bySkill() {
        return bySkill;
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

    public boolean isDodge() {
        return isDodge;
    }

    public void setDodge(boolean dodge) {
        isDodge = dodge;
    }

    public boolean isHit() {
        return critical || (!isMiss && !isDodge && !isParry && !isBlock);
    }

    public Coefficients getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(Coefficients coefficients) {
        this.coefficients = coefficients;
    }
}
