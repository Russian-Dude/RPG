package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.enums.BuffType;
import ru.rdude.rpg.game.logic.enums.Size;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.enums.AttackType;

import java.util.Set;
import java.util.stream.Collectors;

public class SkillApplier {

    SkillData skillData;
    Being caster;
    Being target;
    SkillParser skillParser;

    private SkillApplier(SkillData skillData, Being caster, Being target) {
        this.skillData = skillData;
        this.caster = caster;
        this.target = target;
        this.skillParser = new SkillParser(skillData, caster, target);
    }

    public static boolean apply(SkillData skillData, Being caster, Being target) {
        return apply(new SkillApplier(skillData, caster, target), true);
    }

    public static boolean apply(SkillApplier skillApplier) {
        return apply(skillApplier, false);
    }

    private static boolean apply(SkillApplier skillApplier, boolean applyBuff) {
        // damage part:
        Damage damage = skillApplier.getDamage();
        boolean isReceivedDamage = true;
        if (applyBuff && damage != null)
            isReceivedDamage = skillApplier.target.receive(damage);
        // non damage parts:
        if (!isReceivedDamage)
            return false;
        // buff part:
        if (applyBuff) {
            if (skillApplier.isResisted()) {
                skillApplier.target.notifySubscribers(new BeingAction(BeingAction.Action.RESIST, skillApplier.target, skillApplier.skillData, 0d), skillApplier.target);
                return false;
            }
            Buff buff = new Buff(skillApplier);
            buff.setDamage(damage != null ? damage.value() : null);
            skillApplier.target.receive(buff);
        }
        // items part:
        skillApplier.getReceivedItems().forEach(item -> skillApplier.target.receive(item));
        return true;
    }


    private boolean isMiss() {
        if (!skillData.isCanBeDodged()) return false;
        double HIT = caster.stats().hitValue();
        double SIZE = target.size().getCurrent().stream().findFirst().orElse(Size.SMALL).getSizeNumber();
        double LUCK = caster.stats().luckValue();
        double LVL = caster.stats().lvlValue();
        double chanceMiss = (float) Math.floor(45 - SIZE - Math.floor(HIT / 5) - Math.floor(HIT / 10 - (HIT / SIZE) * 0.5 + Math.floor(LVL * (SIZE / 5) * 0.4) - Math.floor(LUCK / 7) - Math.floor(LUCK / 4) * 0.2 - LUCK * 0.1));
        double chance = Functions.random(100f);
        return chanceMiss >= chance;
    }

    private boolean isDodge() {
        if (!skillData.isCanBeDodged())
            return false;
        double chance = Functions.random(100d);
        double FLEE = target.stats().fleeValue();
        double HIT = caster.stats().hitValue();
        double chanceDodge = Math.floor((FLEE / HIT) + (FLEE - Math.floor(HIT / 6)));
        return chanceDodge >= chance;
    }

    private boolean isBlock() {
        if (!skillData.isCanBeBlocked())
            return false;
        return target.canBlock() && target.stats().blockValue() >= Functions.random(100d);
    }

    private boolean isParry() {
        if (!skillData.isCanBeBlocked() || skillData.getAttackType() != AttackType.MELEE)
            return false;
        return target.canParry() && target.stats().parryValue() >= Functions.random(100d);
    }

    private boolean isCritical() {
        return caster.stats().critValue() >= Functions.random(100d);
    }

    private boolean isResisted() {
        double randomValue = Functions.random(100d);
        double resistanceValue = 0;
        if (skillData.getBuffType() == BuffType.MAGIC)
            resistanceValue = target.stats().magicResistanceValue();
        else if (skillData.getBuffType() == BuffType.PHYSIC)
            resistanceValue = target.stats().physicResistanceValue();
        return resistanceValue >= randomValue;
    }

    private Damage getDamage() {
        if (skillData.getDamage() == null || skillData.getDamage().equals("0")) {
            return null;
        }
        double pureDamageValue = skillParser.parse(skillData.getDamage());
        double damageValue = Math.floor(pureDamageValue * getDamageCoefficient());
        Damage damage = new Damage(damageValue, caster, skillData);
        if (isCritical()) {
            damage.setCritical(true);
        }
        if (!damage.isCritical() && (isMiss() || isDodge())) {
            damage.setMiss(true);
        } else if (!damage.isCritical() && isBlock())
            damage.setBlock(true);
        else if (!damage.isCritical() && isParry())
            damage.setParry(true);
        return damage;
    }

    private double getDamageCoefficient() {
        Coefficients atkCfs = Coefficients.getSumOf(skillData.getCoefficients(), caster.coefficients());
        Coefficients defCfs = target.coefficients();
        double elementsAtk = atkCfs.atk().element().getValue(target.elements().getCurrent()) - 1;
        double elementsDef = defCfs.def().element().getValue(skillData.getElements()) - 1;
        double beingTypeAtk = atkCfs.atk().beingType().getValue(target.beingTypes().getCurrent()) - 1;
        double beingTypeDef = defCfs.def().beingType().getValue(caster.beingTypes().getCurrent()) - 1;
        double sizeAtk = atkCfs.atk().size().getValue(target.size().getCurrent()) - 1;
        AttackType skillAttackType = skillData.getAttackType() == AttackType.WEAPON_TYPE ?
                caster.equipment().attackType() : skillData.getAttackType();
        double attackTypeDef = defCfs.def().attackType().getValue(skillAttackType) - 1;
        return elementsAtk + elementsDef + beingTypeAtk + beingTypeDef + sizeAtk + attackTypeDef + 1;
    }

    private Set<Item> getReceivedItems() {
        return skillData.getReceiveItems().entrySet().stream()
                .map(entry -> new Item(ItemData.getItemDataByGuid(entry.getKey()), entry.getValue()))
                .collect(Collectors.toSet());
    }
}
