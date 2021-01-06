package ru.rdude.rpg.game.logic.entities.items.holders;

import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.enums.ItemMainType;
import ru.rdude.rpg.game.logic.enums.ItemType;
import ru.rdude.rpg.game.logic.holders.Slot;
import ru.rdude.rpg.game.logic.holders.SlotsHolder;
import ru.rdude.rpg.game.logic.stats.Stats;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;


public class EquipmentSlotsHolder extends SlotsHolder<Item> {

    private Slot<Item> armor;
    private Slot<Item> boots;
    private Slot<Item> gloves;
    private Slot<Item> helmet;
    private Slot<Item> necklace;
    private Slot<Item> pants;
    private Slot<Item> leftHand;
    private Slot<Item> rightHand;
    private Slot<Item> jewelry1;
    private Slot<Item> jewelry2;


    public EquipmentSlotsHolder() {
        slots = new ArrayList<>(10);
        slots.add(new Slot<>(ItemType.ARMOR, t -> t.getItemData().getItemType() == ItemType.ARMOR));
        armor = slots.get(0);
        slots.add(new Slot<>(ItemType.BOOTS, t -> t.getItemData().getItemType() == ItemType.BOOTS));
        boots = slots.get(1);
        slots.add(new Slot<>(ItemType.GLOVES, t -> t.getItemData().getItemType() == ItemType.GLOVES));
        gloves = slots.get(2);
        slots.add(new Slot<>(ItemType.HELMET, t -> t.getItemData().getItemType() == ItemType.HELMET));
        helmet = slots.get(3);
        slots.add(new Slot<>(ItemType.NECKLACE, t -> t.getItemData().getItemType() == ItemType.NECKLACE));
        necklace = slots.get(4);
        slots.add(new Slot<>(ItemType.PANTS, t -> t.getItemData().getItemType() == ItemType.PANTS));
        pants = slots.get(5);
        slots.add(new Slot<>(ItemType.SHIELD, t -> t.getItemData().getItemType() == ItemType.SHIELD
                || t.getItemData().getItemType().getMainType() == ItemMainType.WEAPON));
        leftHand = slots.get(6);
        slots.add(new Slot<>(ItemMainType.WEAPON, t -> t.getItemData().getItemType() == ItemType.SHIELD
                || t.getItemData().getItemType().getMainType() == ItemMainType.WEAPON));
        rightHand = slots.get(7);
        slots.add(new Slot<>(ItemType.JEWELRY, t -> t.getItemData().getItemType() == ItemType.JEWELRY));
        jewelry1 = slots.get(8);
        slots.add(new Slot<>(ItemType.JEWELRY, t -> t.getItemData().getItemType() == ItemType.JEWELRY));
        jewelry2 = slots.get(9);
    }

    public Slot<Item> armor() {
        return armor;
    }

    public Slot<Item> boots() {
        return boots;
    }

    public Slot<Item> gloves() {
        return gloves;
    }

    public Slot<Item> helmet() {
        return helmet;
    }

    public Slot<Item> necklace() {
        return necklace;
    }

    public Slot<Item> pants() {
        return pants;
    }

    public Slot<Item> jewelry1() {
        return jewelry1;
    }

    public Slot<Item> jewelry2() {
        return jewelry2;
    }

    public Slot<Item> leftHand() {
        return leftHand;
    }

    public Slot<Item> rightHand() {
        return rightHand;
    }


    @Override
    public boolean receiveEntity(Item entity) {
        if (entity.getItemData().getItemType() == ItemType.ARMOR) {
            if (armor.getEntity() == null) {
                armor.setEntity(entity);
                return true;
            } else return false;
        } else if (entity.getItemData().getItemType() == ItemType.BOOTS) {
            if (boots.getEntity() == null) {
                boots.setEntity(entity);
                return true;
            } else return false;
        } else if (entity.getItemData().getItemType() == ItemType.GLOVES) {
            if (gloves.getEntity() == null) {
                gloves.setEntity(entity);
                return true;
            } else return false;
        } else if (entity.getItemData().getItemType() == ItemType.HELMET) {
            if (helmet.getEntity() == null) {
                helmet.setEntity(entity);
                return true;
            } else return false;
        } else if (entity.getItemData().getItemType() == ItemType.NECKLACE) {
            if (necklace.getEntity() == null) {
                necklace.setEntity(entity);
                return true;
            } else return false;
        } else if (entity.getItemData().getItemType() == ItemType.PANTS) {
            if (pants.getEntity() == null) {
                pants.setEntity(entity);
                return true;
            } else return false;
        } else if (entity.getItemData().getItemType() == ItemType.JEWELRY) {
            if (jewelry1.getEntity() == null) {
                jewelry1.setEntity(entity);
                return true;
            } else if (jewelry2.getEntity() == null) {
                jewelry2.setEntity(entity);
                return true;
            } else return false;
        } else if (entity.getItemData().getItemType() == ItemType.SHIELD
                || entity.getItemData().getItemType().getMainType() == ItemMainType.WEAPON) {
            if (leftHand.getEntity() == null) {
                leftHand.setEntity(entity);
                return true;
            }
            if (rightHand.getEntity() == null) {
                rightHand.setEntity(entity);
                return true;
            } else return false;
        } else throw new UnsupportedOperationException("this type of equippable does not defined");
    }

    public int weaponsAmount() {
        int amount = 0;
        if (leftHand.getEntity().getItemData().getItemType().getMainType() == ItemMainType.WEAPON)
            amount++;
        if (rightHand.getEntity().getItemData().getItemType().getMainType() == ItemMainType.WEAPON)
            amount++;
        return amount;
    }

    public int shieldsAmount() {
        int amount = 0;
        if (leftHand.getEntity().getItemData().getItemType() == ItemType.SHIELD)
            amount++;
        if (rightHand.getEntity().getItemData().getItemType() == ItemType.SHIELD)
            amount++;
        return amount;
    }

    public AttackType attackType() {
        int weaponsAmount = weaponsAmount();
        if (weaponsAmount == 0) return AttackType.MELEE;
        else if (weaponsAmount == 1) {
            if (leftHand.getEntity().getItemData().getItemType().getMainType() == ItemMainType.WEAPON)
                return leftHand.getEntity().getItemData().getWeaponData().getAttackType();
            else if (rightHand.getEntity().getItemData().getItemType().getMainType() == ItemMainType.WEAPON)
                return rightHand.getEntity().getItemData().getWeaponData().getAttackType();
        } else if (weaponsAmount == 2) {
            return leftHand.getEntity().getItemData().getWeaponData().compareTo(rightHand.getEntity().getItemData().getWeaponData()) > 0 ?
                    leftHand.getEntity().getItemData().getWeaponData().getAttackType()
                    : rightHand.getEntity().getItemData().getWeaponData().getAttackType();
        } else {
            throw new IllegalStateException("there must be 0-2 weapons. Something wrong");
        }
        return AttackType.MELEE;
    }

    public Coefficients getCoefficients() {
        Coefficients result = new Coefficients();
        slots.stream()
                .filter(slot -> !slot.isEmpty())
                .map(slot -> slot.getEntity().coefficients())
                .forEach(result::addSumOf);
        return result;
    }

    public Set<Element> getArmorElements() {
        return slots.stream()
                .filter(slot -> !slot.isEmpty())
                .map(Slot::getEntity)
                .filter(entity -> entity.getItemData().getItemType().getMainType() == ItemMainType.ARMOR)
                .flatMap(entity -> entity.elements().getCurrent().stream())
                .collect(Collectors.toSet());
    }

    public Set<Element> getWeaponElements() {
        return slots.stream()
                .filter(slot -> !slot.isEmpty())
                .map(Slot::getEntity)
                .filter(entity -> entity.getItemData().getItemType().getMainType() == ItemMainType.WEAPON)
                .flatMap(weapon -> weapon.elements().getCurrent().stream())
                .collect(Collectors.toSet());
    }

    public Stats getStats() {
        Stats result = new Stats(false);
        slots.stream()
                .filter(slot -> !slot.isEmpty())
                .map(slot -> slot.getEntity().getItemData().getStats())
                .forEach(result::increase);
        return result;
    }
}
