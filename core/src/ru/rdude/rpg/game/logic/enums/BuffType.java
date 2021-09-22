package ru.rdude.rpg.game.logic.enums;

import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("buffType")
public enum BuffType implements UsedByStatistics{
    PHYSICAL, MAGICAL
}
