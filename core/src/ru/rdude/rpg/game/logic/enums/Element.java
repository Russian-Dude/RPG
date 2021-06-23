package ru.rdude.rpg.game.logic.enums;

import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("element")
public enum Element implements UsedByStatistics
{NEUTRAL, FIRE, WIND, EARTH, WATER, LIGHT, DARK, ENERGY}
