package ru.rdude.rpg.game.logic.enums;

import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@JsonPolymorphicSubType("itemType")
public enum ItemType implements UsedByStatistics, ItemMainOrConcreteType {
    // equip:
    ARMOR(ItemMainType.EQUIPMENT),
    BOOTS(ItemMainType.EQUIPMENT),
    GLOVES(ItemMainType.EQUIPMENT),
    HELMET(ItemMainType.EQUIPMENT),
    JEWELRY(ItemMainType.EQUIPMENT),
    NECKLACE(ItemMainType.EQUIPMENT),
    PANTS(ItemMainType.EQUIPMENT),
    SHIELD(ItemMainType.EQUIPMENT),
    // weapon types:
    DAGGER(ItemMainType.WEAPON),
    SWORD(ItemMainType.WEAPON),
    MACE(ItemMainType.WEAPON),
    AXE(ItemMainType.WEAPON),
    STAFF(ItemMainType.WEAPON),
    HUMMER(ItemMainType.WEAPON),
    SPEAR(ItemMainType.WEAPON),
    BOW(ItemMainType.WEAPON),
    CROSSBOW(ItemMainType.WEAPON),
    THROWING_KNIFE(ItemMainType.WEAPON),
    THROWING_AXE(ItemMainType.WEAPON),
    // usable:
    POTION(ItemMainType.USABLE),
    SCROLL(ItemMainType.USABLE),
    // simple:
    GEM(ItemMainType.SIMPLE),
    HERB(ItemMainType.SIMPLE),
    LEATHER(ItemMainType.SIMPLE),
    STONE(ItemMainType.SIMPLE),
    WOOD(ItemMainType.SIMPLE)
    ;

    public static Set<ItemType> allOf(ItemMainType itemMainType) {
        return Arrays.stream(values())
                .filter(type -> type.mainType == itemMainType)
                .collect(Collectors.toSet());
    }

    private ItemMainType mainType;

    ItemType(ItemMainType mainType) {
        this.mainType = mainType;
    }

    public ItemMainType getMainType() {
        return mainType;
    }
}
