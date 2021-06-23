package ru.rdude.rpg.game.logic.enums;

import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("skillType")
public enum SkillType implements UsedByStatistics {
    NO_TYPE, DISEASE, CURSE, BLESS, POISON
}
