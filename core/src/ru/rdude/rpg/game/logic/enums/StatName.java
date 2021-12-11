package ru.rdude.rpg.game.logic.enums;

import ru.rdude.rpg.game.logic.entities.skills.Damage;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.*;
import ru.rdude.rpg.game.logic.stats.secondary.*;

import java.util.Arrays;

public enum StatName {
    LVL(Lvl.class, "Level", "LVL", false),
    EXP(Lvl.Exp.class, "Experience", "EXP", false),
    STAT_POINTS(Lvl.StatPoints.class, "Stat points", "STATPOINT", false),
    DEF(Def.class, "Defence", "DEF", false),
    AGI(Agi.class, "Agility", "AGI", true),
    DEX(Dex.class, "Dexterity", "DEX", true),
    INT(Int.class, "Intelligence", "INT", true),
    LUCK(Luck.class, "Luck", "LUCK", true),
    STR(Str.class, "Strength", "STR", true),
    VIT(Vit.class, "Vitality", "VIT", true),
    STM(Stm.class, "Stamina current", "STM", false),
    STM_ATK(Stm.PerHit.class, "Stamina per hit", "STMATK", false),
    STM_MAX(Stm.Max.class, "Stamina max", "STMMAX", false),
    STM_REST(Stm.Recovery.class, "Stamina recovery", "STMREST", false),
    STM_HARDNESS(Stm.Hardness.class, "Weapon hardness", "STMH", false),
    HP(Hp.class, "Current health", "HP", false),
    HP_MAX(Hp.Max.class, "Health max", "HPMAX", false),
    HP_REST(Hp.Recovery.class, "Health recovery", "HPREST", false),
    MELEE_MIN(Dmg.Melee.MeleeMin.class, "Melee min damage", "MELEEATKMIN", false),
    MELEE_MAX(Dmg.Melee.MeleeMax.class, "Melee max damage", "MELEEATKMAX", false),
    RANGE_MIN(Dmg.Range.RangeMin.class, "Range min damage", "RANGEATKMIN", false),
    RANGE_MAX(Dmg.Range.RangeMax.class, "Range max damage", "RANGEATKMAX", false),
    MAGIC_MIN(Dmg.Magic.MagicMin.class, "Magic min damage", "MAGICATKMIN", false),
    MAGIC_MAX(Dmg.Magic.MagicMax.class, "Magic max damage", "MAGICATKMAX", false),
    CRIT(Crit.class, "Critical", "CRIT", false),
    PARRY(Parry.class, "Parry", "PARRY", false),
    HIT(Hit.class, "Hit", "HIT", false),
    BLOCK(Block.class, "Block", "BLOCK", false),
    CONCENTRATION(Concentration.class, "Concentration", "CONC", false),
    LUCKY_DODGE(Flee.LuckyDodgeChance.class, "Lucky dodge", "LKYDODGE", false),
    FLEE(Flee.class, "Flee", "FLEE", false),
    PHYSIC_RESISTANCE(PhysicResistance.class, "Physic resistance", "PRES", false),
    DAMAGE(Dmg.class, "Damage", "DMG", false),
    MAGIC_RESISTANCE(MagicResistance.class, "Magic resistance", "MRES", false);

    private Class<? extends Stat> cl;
    private String name;
    private String variableName;
    private boolean isPrimary;

    StatName(Class<? extends Stat> cl, String name, String variableName, boolean isPrimary) {
        this.cl = cl;
        this.name = name;
        this.variableName = variableName;
        this.isPrimary = isPrimary;
    }

    public static StatName get(Class<? extends Stat> statClass) {
        return Arrays.stream(values())
                .filter(statName -> statName.cl.isAssignableFrom(statClass))
                .findFirst()
                .orElse(null);
    }

    public Class<? extends Stat> getClazz() {
        return cl;
    }

    public String getName() {
        return name;
    }

    public String getVariableName() {
        return variableName;
    }

    public boolean isPrimary() {
        return isPrimary;
    }
}
