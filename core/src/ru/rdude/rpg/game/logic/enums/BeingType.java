package ru.rdude.rpg.game.logic.enums;

import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("beingType")
public enum BeingType implements UsedByStatistics
{HUMAN, FISH, ANIMAL, INSECT, BIRD, PLANT, DRAGON, DEMON, UNDEAD, BOSS, CRUSH}
