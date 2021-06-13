package ru.rdude.rpg.game.logic.enums;

public enum ItemType implements UsedByStatistics {
    // equip:
    ARMOR(ItemMainType.ARMOR),
    BOOTS(ItemMainType.ARMOR),
    GLOVES(ItemMainType.ARMOR),
    HELMET(ItemMainType.ARMOR),
    JEWELRY(ItemMainType.ARMOR),
    NECKLACE(ItemMainType.ARMOR),
    PANTS(ItemMainType.ARMOR),
    SHIELD(ItemMainType.ARMOR),
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

    private ItemMainType mainType;

    ItemType(ItemMainType mainType) {
        this.mainType = mainType;
    }

    public ItemMainType getMainType() {
        return mainType;
    }
}
