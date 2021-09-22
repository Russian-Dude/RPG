package ru.rdude.rpg.game.logic.enums;

import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("itemMainType")
public enum ItemMainType implements UsedByStatistics, ItemMainOrConcreteType {
    SIMPLE,
    USABLE,
    EQUIPMENT,
    WEAPON
}
