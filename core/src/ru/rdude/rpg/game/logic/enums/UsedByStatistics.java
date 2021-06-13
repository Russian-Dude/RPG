package ru.rdude.rpg.game.logic.enums;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "StatisticType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AttackType.class, name = "AttackType"),
        @JsonSubTypes.Type(value = BeingType.class, name = "BeingType"),
        @JsonSubTypes.Type(value = BuffType.class, name = "BuffType"),
        @JsonSubTypes.Type(value = Element.class, name = "ElementType"),
        @JsonSubTypes.Type(value = ItemMainType.class, name = "ItemMainType"),
        @JsonSubTypes.Type(value = ItemType.class, name = "ItemType"),
        @JsonSubTypes.Type(value = Size.class, name = "Size"),
        @JsonSubTypes.Type(value = SkillType.class, name = "SkillType"),
})
public interface UsedByStatistics {
}
