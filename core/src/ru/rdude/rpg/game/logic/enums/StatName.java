package ru.rdude.rpg.game.logic.enums;

import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.*;
import ru.rdude.rpg.game.logic.stats.secondary.*;

import java.util.Arrays;

public enum StatName {
    LVL(Lvl.class, "Level", "LVL"),
    EXP(Lvl.Exp.class, "Experience", "EXP"),
    STAT_POINTS(Lvl.StatPoints.class, "Stat points", "STATPOINT"),
    DEF(Def.class, "Defence", "DEF"),
    AGI(Agi.class, "Agility", "AGI"),
    DEX(Dex.class, "Dexterity", "DEX"),
    INT(Int.class, "Intelligence", "INT"),
    LUCK(Luck.class, "Luck", "LUCK"),
    STR(Str.class, "Strength", "STR"),
    VIT(Vit.class, "Vitality", "VIT"),
    STM(Stm.class, "Stamina current", "STM"),
    STM_ATK(Stm.PerHit.class, "Stamina per hit", "STMATK"),
    STM_MAX(Stm.Max.class, "Stamina max", "STMMAX"),
    STM_REST(Stm.Recovery.class, "Stamina recovery", "STMREST"),
    STM_HARDNESS(Stm.Hardness.class, "Weapon hardness", "STMH"),
    HP(Hp.class, "Current health", "HP"),
    HP_MAX(Hp.Max.class, "Health max", "HPMAX"),
    HP_REST(Hp.Recovery.class, "Health recovery", "HPREST"),
    MELEE_MIN(Dmg.Melee.MeleeMin.class, "Melee min damage", "MELEEATKMIN"),
    MELEE_MAX(Dmg.Melee.MeleeMax.class, "Melee max damage", "MELEEATKMAX"),
    RANGE_MIN(Dmg.Range.RangeMin.class, "Range min damage", "RANGEATKMIN"),
    RANGE_MAX(Dmg.Range.RangeMax.class, "Range max damage", "RANGEATKMAX"),
    MAGIC_MIN(Dmg.Magic.MagicMin.class, "Magic min damage", "MAGICATKMIN"),
    MAGIC_MAX(Dmg.Magic.MagicMax.class, "Magic max damage", "MAGICATKMAX"),
    CRIT(Crit.class, "Critical", "CRIT"),
    PARRY(Parry.class, "Parry", "PARRY"),
    HIT(Hit.class, "Hit", "HIT"),
    BLOCK(Block.class, "Block", "BLOCK"),
    CONCENTRATION(Concentration.class, "Concentration", "CONC"),
    LUCKY_DODGE(Flee.LuckyDodgeChance.class, "Lucky dodge", "LKYDODGE"),
    FLEE(Flee.class, "Flee", "FLEE"),
    PHYSIC_RESISTANCE(PhysicResistance.class, "Physic resistance", "PRES"),
    MAGIC_RESISTANCE(MagicResistance.class, "Magic resistance", "MRES");

    private Class<? extends Stat> cl;
    private String name;
    private String variableName;

    StatName(Class<? extends Stat> cl, String name, String variableName) {
        this.cl = cl;
        this.name = name;
        this.variableName = variableName;
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
}
