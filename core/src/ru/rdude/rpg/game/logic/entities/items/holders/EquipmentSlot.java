package ru.rdude.rpg.game.logic.entities.items.holders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.enums.ItemMainOrConcreteType;
import ru.rdude.rpg.game.logic.enums.StatName;
import ru.rdude.rpg.game.logic.holders.Slot;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@JsonPolymorphicSubType("equipmentSlot")
public class EquipmentSlot extends Slot<Item> {

    private final Being<?> being;

    public EquipmentSlot(Being<?> being, ItemMainOrConcreteType itemType, ItemMainOrConcreteType... itemTypes) {
        super(((Enum<?>) itemType).name(), createExtraRequirements(being, itemType, itemTypes));
        this.being = being;
    }

    private static SlotPredicate<Item> createExtraRequirements(Being<?> being, ItemMainOrConcreteType itemType, ItemMainOrConcreteType... itemTypes) {
        Set<ItemMainOrConcreteType> types = new HashSet<>();
        types.add(itemType);
        if (itemTypes != null) {
            types.addAll(Arrays.asList(itemTypes));
        }
        return new EquipmentSlotPredicate(types, being);
    }

    @JsonCreator
    private EquipmentSlot(@JsonProperty("requirements") Set<EquipmentSlotPredicate> requirements, @JsonProperty("being") Being<?> being) {
        super(requirements);
        this.being = being;
    }

    @JsonProperty("requirements")
    private Set<EquipmentSlotPredicate> getExtraRequirements() {
        return (Set<EquipmentSlotPredicate>) extraRequirements;
    }

    @Override
    public void setEntity(Item newEntity) {
        if (entity != null) {
            // stats
            being.stats().decreaseBuffValues(Item.class, entity.getEntityData().getStats());
            // coefficients
            if (entity.coefficients() != null) {
                being.coefficients().removeSumOf(entity.coefficients());
            }
            // available skills
            being.getAvailableSkills().removeAllByGuid(entity.getEntityData().getSkillsEquip());
            // weapon
            if (entity.getEntityData().isWeapon()) {
                switch (entity.getEntityData().getWeaponData().getAttackType()) {
                    case MAGIC:
                        being.stats().get(StatName.MAGIC_MIN).decreaseBuffValue(Item.class, entity.getEntityData().getWeaponData().getMinDmg());
                        being.stats().get(StatName.MAGIC_MAX).decreaseBuffValue(Item.class, entity.getEntityData().getWeaponData().getMaxDmg());
                        break;
                    case MELEE:
                        being.stats().get(StatName.MELEE_MIN).decreaseBuffValue(Item.class, entity.getEntityData().getWeaponData().getMinDmg());
                        being.stats().get(StatName.MELEE_MAX).decreaseBuffValue(Item.class, entity.getEntityData().getWeaponData().getMaxDmg());
                        break;
                    case RANGE:
                        being.stats().get(StatName.RANGE_MIN).decreaseBuffValue(Item.class, entity.getEntityData().getWeaponData().getMinDmg());
                        being.stats().get(StatName.RANGE_MAX).decreaseBuffValue(Item.class, entity.getEntityData().getWeaponData().getMaxDmg());
                        break;
                }
            }
        }
        super.setEntity(newEntity);
        if (newEntity != null) {
            // stats
            being.stats().increaseBuffValues(Item.class, entity.getEntityData().getStats());
            // coefficients
            if (newEntity.coefficients() != null) {
                being.coefficients().addSumOf(newEntity.coefficients());
            }
            // available skills
            being.getAvailableSkills().addAllByGuid(entity.getEntityData().getSkillsEquip());
            // weapon
            if (newEntity.getEntityData().isWeapon()) {
                switch (newEntity.getEntityData().getWeaponData().getAttackType()) {
                    case MAGIC:
                        being.stats().get(StatName.MAGIC_MIN).increaseBuffValue(Item.class, newEntity.getEntityData().getWeaponData().getMinDmg());
                        being.stats().get(StatName.MAGIC_MAX).increaseBuffValue(Item.class, newEntity.getEntityData().getWeaponData().getMaxDmg());
                        break;
                    case MELEE:
                        being.stats().get(StatName.MELEE_MIN).increaseBuffValue(Item.class, newEntity.getEntityData().getWeaponData().getMinDmg());
                        being.stats().get(StatName.MELEE_MAX).increaseBuffValue(Item.class, newEntity.getEntityData().getWeaponData().getMaxDmg());
                        break;
                    case RANGE:
                        being.stats().get(StatName.RANGE_MIN).increaseBuffValue(Item.class, newEntity.getEntityData().getWeaponData().getMinDmg());
                        being.stats().get(StatName.RANGE_MAX).increaseBuffValue(Item.class, newEntity.getEntityData().getWeaponData().getMaxDmg());
                        break;
                }
            }
        }
    }
}
