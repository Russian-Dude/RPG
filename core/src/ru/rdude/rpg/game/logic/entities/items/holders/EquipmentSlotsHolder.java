package ru.rdude.rpg.game.logic.entities.items.holders;

import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.entities.items.equip.*;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.holders.Slot;
import ru.rdude.rpg.game.logic.holders.SlotsHolder;
import ru.rdude.rpg.game.logic.stats.Stats;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;


public class EquipmentSlotsHolder extends SlotsHolder<EquippableItem> {

    private Slot<EquippableItem> armor;
    private Slot<EquippableItem> boots;
    private Slot<EquippableItem> gloves;
    private Slot<EquippableItem> helmet;
    private Slot<EquippableItem> necklace;
    private Slot<EquippableItem> pants;
    private Slot<EquippableItem> leftHand;
    private Slot<EquippableItem> rightHand;
    private Slot<EquippableItem> jewelry1;
    private Slot<EquippableItem> jewelry2;


    public EquipmentSlotsHolder() {
        slots = new ArrayList<>(10);
        slots.add(new Slot<>(Armor.class));
        armor = slots.get(0);
        slots.add(new Slot<>(Boots.class));
        boots = slots.get(1);
        slots.add(new Slot<>(Gloves.class));
        gloves = slots.get(2);
        slots.add(new Slot<>(Helmet.class));
        helmet = slots.get(3);
        slots.add(new Slot<>(Necklace.class));
        necklace = slots.get(4);
        slots.add(new Slot<>(Pants.class));
        pants = slots.get(5);
        slots.add(new Slot<>(EquipInHandItem.class));
        leftHand = slots.get(6);
        slots.add(new Slot<>(EquipInHandItem.class));
        rightHand = slots.get(7);
        slots.add(new Slot<>(Jewelry.class));
        jewelry1 = slots.get(8);
        slots.add(new Slot<>(Jewelry.class));
        jewelry2 = slots.get(9);
    }

    public Slot<EquippableItem> armor() {
        return armor;
    }

    public Slot<EquippableItem> boots() {
        return boots;
    }

    public Slot<EquippableItem> gloves() {
        return gloves;
    }

    public Slot<EquippableItem> helmet() {
        return helmet;
    }

    public Slot<EquippableItem> necklace() {
        return necklace;
    }

    public Slot<EquippableItem> pants() {
        return pants;
    }

    public Slot<EquippableItem> jewelry1() {
        return jewelry1;
    }

    public Slot<EquippableItem> jewelry2() {
        return jewelry2;
    }

    public Slot<EquippableItem> leftHand() {
        return leftHand;
    }

    public Slot<EquippableItem> rightHand() {
        return rightHand;
    }


    @Override
    public boolean receiveEntity(EquippableItem entity) {
        if (entity instanceof Armor) {
            if (armor.getEntity() == null) {
                armor.setEntity((Armor) entity);
                return true;
            } else return false;
        } else if (entity instanceof Boots) {
            if (boots.getEntity() == null) {
                boots.setEntity((Boots) entity);
                return true;
            } else return false;
        } else if (entity instanceof Gloves) {
            if (gloves.getEntity() == null) {
                gloves.setEntity((Gloves) entity);
                return true;
            } else return false;
        } else if (entity instanceof Helmet) {
            if (helmet.getEntity() == null) {
                helmet.setEntity((Helmet) entity);
                return true;
            } else return false;
        } else if (entity instanceof Necklace) {
            if (necklace.getEntity() == null) {
                necklace.setEntity((Necklace) entity);
                return true;
            } else return false;
        } else if (entity instanceof Pants) {
            if (pants.getEntity() == null) {
                pants.setEntity((Pants) entity);
                return true;
            } else return false;
        } else if (entity instanceof Jewelry) {
            if (jewelry1.getEntity() == null) {
                jewelry1.setEntity((Jewelry) entity);
                return true;
            } else if (jewelry2.getEntity() == null) {
                jewelry2.setEntity((Jewelry) entity);
                return true;
            } else return false;
        } else if (entity instanceof Shield || entity instanceof Weapon) {
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
        if (leftHand.getEntity() instanceof Weapon)
            amount++;
        if (rightHand.getEntity() instanceof Weapon)
            amount++;
        return amount;
    }

    public int shieldsAmount() {
        int amount = 0;
        if (leftHand.getEntity() instanceof Shield)
            amount++;
        if (rightHand.getEntity() instanceof Shield)
            amount++;
        return amount;
    }

    public AttackType attackType() {
        int weaponsAmount = weaponsAmount();
        if (weaponsAmount == 0) return AttackType.MELEE;
        else if (weaponsAmount == 1) {
            if (leftHand.getEntity() instanceof Weapon)
                return ((Weapon) leftHand.getEntity()).getAttackType();
            else if (rightHand.getEntity() instanceof Weapon)
                return ((Weapon) rightHand.getEntity()).getAttackType();
        } else if (weaponsAmount == 2) {
            return ((Weapon) leftHand.getEntity()).compareTo((Weapon) rightHand.getEntity()) > 0 ?
                    ((Weapon) leftHand.getEntity()).getAttackType() : ((Weapon) rightHand.getEntity()).getAttackType();
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
                .filter(entity -> !(entity instanceof Weapon))
                .flatMap(entity -> entity.elements().getCurrent().stream())
                .collect(Collectors.toSet());
    }

    public Set<Element> getWeaponElements() {
        return slots.stream()
                .filter(slot -> !slot.isEmpty())
                .map(Slot::getEntity)
                .filter(entity -> entity instanceof Weapon)
                .flatMap(weapon -> weapon.elements().getCurrent().stream())
                .collect(Collectors.toSet());
    }

    public Stats getStats() {
        Stats result = new Stats(false);
        slots.stream()
                .filter(slot -> !slot.isEmpty())
                .map(slot -> slot.getEntity().stats())
                .forEach(result::increase);
        return result;
    }
}
