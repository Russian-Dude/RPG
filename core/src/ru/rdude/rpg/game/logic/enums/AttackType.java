package ru.rdude.rpg.game.logic.enums;

import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("attackType")
public enum AttackType implements UsedByStatistics {
    MELEE, RANGE, MAGIC, WEAPON_TYPE
}
