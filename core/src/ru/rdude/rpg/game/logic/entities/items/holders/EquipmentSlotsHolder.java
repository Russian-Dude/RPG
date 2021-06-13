package ru.rdude.rpg.game.logic.entities.items.holders;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.enums.ItemMainType;
import ru.rdude.rpg.game.logic.enums.ItemType;
import ru.rdude.rpg.game.logic.holders.Slot;
import ru.rdude.rpg.game.logic.holders.SlotsHolder;
import ru.rdude.rpg.game.logic.stats.Stats;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class EquipmentSlotsHolder extends SlotsHolder<Item> {

    @JsonProperty("being")
    private Being being;

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

    public EquipmentSlotsHolder(Being being) {
        this.being = being;
        slots = new ArrayList<>(10);
        slots.add(new Slot<>(ItemType.ARMOR.name(), t -> t.getItemData().getItemType() == ItemType.ARMOR
                && being.stats().isMatchRequirementsOf(t.requirements())));
        armor = slots.get(0);
        slots.add(new Slot<>(ItemType.BOOTS.name(), t -> t.getItemData().getItemType() == ItemType.BOOTS
                && being.stats().isMatchRequirementsOf(t.requirements())));
        boots = slots.get(1);
        slots.add(new Slot<>(ItemType.GLOVES.name(), t -> t.getItemData().getItemType() == ItemType.GLOVES
                && being.stats().isMatchRequirementsOf(t.requirements())));
        gloves = slots.get(2);
        slots.add(new Slot<>(ItemType.HELMET.name(), t -> t.getItemData().getItemType() == ItemType.HELMET
                && being.stats().isMatchRequirementsOf(t.requirements())));
        helmet = slots.get(3);
        slots.add(new Slot<>(ItemType.NECKLACE.name(), t -> t.getItemData().getItemType() == ItemType.NECKLACE
                && being.stats().isMatchRequirementsOf(t.requirements())));
        necklace = slots.get(4);
        slots.add(new Slot<>(ItemType.PANTS.name(), t -> t.getItemData().getItemType() == ItemType.PANTS
                && being.stats().isMatchRequirementsOf(t.requirements())));
        pants = slots.get(5);
        slots.add(new Slot<>(ItemType.SHIELD.name(), t -> (t.getItemData().getItemType() == ItemType.SHIELD
                || t.getItemData().getItemType().getMainType() == ItemMainType.WEAPON)
                && being.stats().isMatchRequirementsOf(t.requirements())));
        leftHand = slots.get(6);
        slots.add(new Slot<>(ItemMainType.WEAPON.name(), t -> (t.getItemData().getItemType() == ItemType.SHIELD
                || t.getItemData().getItemType().getMainType() == ItemMainType.WEAPON)
                && being.stats().isMatchRequirementsOf(t.requirements())));
        rightHand = slots.get(7);
        slots.add(new Slot<>(ItemType.JEWELRY.name(), t -> t.getItemData().getItemType() == ItemType.JEWELRY
                && being.stats().isMatchRequirementsOf(t.requirements())));
        jewelry1 = slots.get(8);
        slots.add(new Slot<>(ItemType.JEWELRY.name(), t -> t.getItemData().getItemType() == ItemType.JEWELRY
                && being.stats().isMatchRequirementsOf(t.requirements())));
        jewelry2 = slots.get(9);
    }

    @JsonCreator
    private EquipmentSlotsHolder(@JsonProperty("slots") List<Slot<Item>> slots, @JsonProperty("being") Being being) {
        this.slots = slots;
        armor = slots.get(0);
        armor.addRequirement(t -> t.getItemData().getItemType() == ItemType.ARMOR
                && being.stats().isMatchRequirementsOf(t.requirements()));
        boots = slots.get(1);
        boots.addRequirement(t -> t.getItemData().getItemType() == ItemType.BOOTS
                && being.stats().isMatchRequirementsOf(t.requirements()));
        gloves = slots.get(2);
        gloves.addRequirement(t -> t.getItemData().getItemType() == ItemType.GLOVES
                && being.stats().isMatchRequirementsOf(t.requirements()));
        helmet = slots.get(3);
        helmet.addRequirement(t -> t.getItemData().getItemType() == ItemType.HELMET
                && being.stats().isMatchRequirementsOf(t.requirements()));
        necklace = slots.get(4);
        necklace.addRequirement(t -> t.getItemData().getItemType() == ItemType.NECKLACE
                && being.stats().isMatchRequirementsOf(t.requirements()));
        pants = slots.get(5);
        pants.addRequirement(t -> t.getItemData().getItemType() == ItemType.PANTS
                && being.stats().isMatchRequirementsOf(t.requirements()));
        leftHand = slots.get(6);
        leftHand.addRequirement(t -> (t.getItemData().getItemType() == ItemType.SHIELD
                || t.getItemData().getItemType().getMainType() == ItemMainType.WEAPON)
                && being.stats().isMatchRequirementsOf(t.requirements()));
        rightHand = slots.get(7);
        rightHand.addRequirement(t -> (t.getItemData().getItemType() == ItemType.SHIELD
                || t.getItemData().getItemType().getMainType() == ItemMainType.WEAPON)
                && being.stats().isMatchRequirementsOf(t.requirements()));
        jewelry1 = slots.get(8);
        jewelry1.addRequirement(t -> t.getItemData().getItemType() == ItemType.JEWELRY
                && being.stats().isMatchRequirementsOf(t.requirements()));
        jewelry2 = slots.get(9);
        jewelry2.addRequirement(t -> t.getItemData().getItemType() == ItemType.JEWELRY
                && being.stats().isMatchRequirementsOf(t.requirements()));
    }

    public Optional<Slot<Item>> slotFor(ItemType itemType) {
        switch (itemType.getMainType()) {
            case ARMOR:
                switch (itemType) {
                    case ARMOR:
                        return Optional.of(armor);
                    case BOOTS:
                        return Optional.of(boots);
                    case GLOVES:
                        return Optional.of(gloves);
                    case HELMET:
                        return Optional.of(helmet);
                    case NECKLACE:
                        return Optional.of(necklace);
                    case PANTS:
                        return Optional.of(pants);
                    case SHIELD:
                        return Optional.of(leftHand);
                    case JEWELRY:
                        if (jewelry1.isEmpty()) {
                            return Optional.of(jewelry1);
                        }
                        else if (jewelry2.isEmpty()) {
                            return Optional.of(jewelry2);
                        }
                        else {
                            return Optional.of(jewelry1);
                        }
                    default:
                        return Optional.empty();
                }
            case WEAPON:
                return Optional.of(rightHand);
            default:
                return Optional.empty();
        }
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
            if (armor.getEntity() == null && armor.isEntityMatchRequirements(entity)) {
                armor.setEntity(entity);
                return true;
            } else return false;
        } else if (entity.getItemData().getItemType() == ItemType.BOOTS) {
            if (boots.getEntity() == null && boots.isEntityMatchRequirements(entity)) {
                boots.setEntity(entity);
                return true;
            } else return false;
        } else if (entity.getItemData().getItemType() == ItemType.GLOVES) {
            if (gloves.getEntity() == null && gloves.isEntityMatchRequirements(entity)) {
                gloves.setEntity(entity);
                return true;
            } else return false;
        } else if (entity.getItemData().getItemType() == ItemType.HELMET) {
            if (helmet.getEntity() == null && helmet.isEntityMatchRequirements(entity)) {
                helmet.setEntity(entity);
                return true;
            } else return false;
        } else if (entity.getItemData().getItemType() == ItemType.NECKLACE) {
            if (necklace.getEntity() == null && necklace.isEntityMatchRequirements(entity)) {
                necklace.setEntity(entity);
                return true;
            } else return false;
        } else if (entity.getItemData().getItemType() == ItemType.PANTS) {
            if (pants.getEntity() == null && pants.isEntityMatchRequirements(entity)) {
                pants.setEntity(entity);
                return true;
            } else return false;
        } else if (entity.getItemData().getItemType() == ItemType.JEWELRY) {
            if (jewelry1.getEntity() == null && jewelry1.isEntityMatchRequirements(entity)) {
                jewelry1.setEntity(entity);
                return true;
            } else if (jewelry2.getEntity() == null && jewelry2.isEntityMatchRequirements(entity)) {
                jewelry2.setEntity(entity);
                return true;
            } else return false;
        } else if (entity.getItemData().getItemType() == ItemType.SHIELD
                || entity.getItemData().getItemType().getMainType() == ItemMainType.WEAPON) {
            if (leftHand.getEntity() == null && leftHand.isEntityMatchRequirements(entity)) {
                leftHand.setEntity(entity);
                return true;
            } else if (rightHand.getEntity() == null && rightHand.isEntityMatchRequirements(entity)) {
                rightHand.setEntity(entity);
                return true;
            } else return false;
        } else return false;
    }

    public int weaponsAmount() {
        int amount = 0;
        if (leftHand.getEntity() != null && leftHand.getEntity().getItemData().getItemType().getMainType() == ItemMainType.WEAPON)
            amount++;
        if (rightHand.getEntity() != null && rightHand.getEntity().getItemData().getItemType().getMainType() == ItemMainType.WEAPON)
            amount++;
        return amount;
    }

    public int shieldsAmount() {
        int amount = 0;
        if (leftHand.getEntity() != null && leftHand.getEntity().getItemData().getItemType() == ItemType.SHIELD)
            amount++;
        if (rightHand.getEntity() != null && rightHand.getEntity().getItemData().getItemType() == ItemType.SHIELD)
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
