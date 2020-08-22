package ru.rdude.rpg.game.logic.entities.items.equip;

import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.enums.AttackType;

public class Weapon extends EquipInHandItem implements Comparable<Weapon> {

    public enum WeaponType {
        DAGGER, SWORD, MACE, AXE, STAFF, HUMMER, SPEAR, BOW, CROSSBOW, THROWING_KNIFE, THROWING_AXE
    }

    private final WeaponType weaponType;
    private boolean doubleHanded;

    public Weapon(ItemData itemData, WeaponType weaponType, boolean doubleHanded) {
        super(itemData);
        this.weaponType = weaponType;
        this.doubleHanded = doubleHanded;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public boolean isDoubleHanded() {
        return doubleHanded;
    }

    public AttackType getAttackType() {
        return itemData.getAttackType();
    }

    @Override
    public int compareTo(Weapon o) {
        return (int) ((this.itemData.getMinDmg() + this.itemData.getMaxDmg()) -
                        (o.itemData.getMinDmg() + o.itemData.getMaxDmg()));
    }

}
