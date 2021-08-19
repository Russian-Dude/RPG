package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.enums.BuffType;
import ru.rdude.rpg.game.logic.enums.Size;
import ru.rdude.rpg.game.logic.enums.Target;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.utils.Functions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SkillResultsCreator {

    public List<SkillResult> createFromBuff(Buff buff) {

        List<SkillResult> results = new ArrayList<>();
        SkillData skillData = buff.getEntityData();
        final boolean recalculate = skillData.isRecalculateStatsEveryIteration();

        // damage
        Damage damage = null;
        if (skillData.hasDamage()) {
            damage = recalculate ? getDamage(skillData, buff.getCaster(), buff.getTarget()) : buff.getDamage().orElse(null);
        }

        // summon
        final Long summon = getSummon(skillData);

        // resisted
        final boolean resisted = isResisted(skillData, buff.getTarget());

        results.add(new SkillResult(skillData, buff.getCaster(), buff.getTarget(), damage, summon, null, resisted));

        // skills chaining
        if (!resisted) {
            for (Long castSkill : getCastSkills(skillData)) {
                final SkillData skill = Game.getEntityFactory().skills().describerToReal(castSkill);
                final SkillTargets targets = Game.getSkillTargeter().get(skill, buff.getTarget(), Target.SELF);
                results.addAll(createFromSkill(skill, buff.getTarget(), targets));
            }
        }

        return results;
    }


    public List<SkillResult> createFromSkill(SkillData data, Being<?> caster, SkillTargets targets) {

        SkillData skillData = Game.getEntityFactory().skills().describerToReal(data);

        List<SkillResult> results = new ArrayList<>();
        Damage mainTargetDamage = getDamage(skillData, caster, targets.getMainTarget());

        // create buff and/or receive items and summon only if damage does not exists or if it hits
        boolean canContinue = mainTargetDamage == null || mainTargetDamage.isHit();

        // return damage that does not hit
        if (!canContinue) {
            results.add(new SkillResult(skillData, caster, targets.getMainTarget(), mainTargetDamage, null, null, false));
            return results;
        }

        // main target skill result
        boolean isMainTargetResisted = isResisted(skillData, targets.getMainTarget());
        Buff mainTargetBuff = isBuff(skillData) && !isMainTargetResisted ?
                new Buff(skillData, caster, targets.getMainTarget(), mainTargetDamage) : null;
        SkillResult mainTargetSkillResult = new SkillResult(
                skillData, caster, targets.getMainTarget(), mainTargetDamage, getSummon(skillData), mainTargetBuff, isMainTargetResisted);
        results.add(mainTargetSkillResult);

        // ignore other targets and skill chaining if skill did not hit and resisted
        if ((mainTargetDamage == null || !mainTargetDamage.isHit()) && isMainTargetResisted) {
            return results;
        }

        List<Being<?>> applySkillChaining = new ArrayList<>();
        applySkillChaining.add(targets.getMainTarget());

        // other targets
        targets.getSubTargets().forEach(target -> {
            Damage damage = getDamage(skillData, caster, target);
            if (damage == null || damage.isHit()) {
                boolean isResisted = isResisted(skillData, target);
                Buff buff = isBuff(skillData) && !isResisted ?
                        new Buff(skillData, caster, target, damage) : null;
                results.add(new SkillResult(skillData, caster, target, damage, getSummon(skillData), buff, isResisted));
                applySkillChaining.add(target);
            }
            else {
                results.add(new SkillResult(skillData, caster, target, damage, null, null, false));
            }
        });

        // skill chaining
        for (Being<?> being : applySkillChaining) {
            for (Long guid : getCastSkills(skillData)) {
                SkillData castSkill = Game.getEntityFactory().skills().describerToReal(guid);
                SkillTargets skillTargets = Game.getSkillTargeter().get(caster, being, skillData.getTargets());
                results.addAll(createFromSkill(castSkill, caster, skillTargets));
            }
        }

        return results;
    }


    private boolean isBuff(SkillData skillData) {
        final boolean isPermanent = skillData.isPermanent();
        final String turns = skillData.getDurationInTurns();
        final boolean isTurns = turns != null && !turns.isBlank() && !turns.equals("0");
        final String minutes = skillData.getDurationInMinutes();
        final boolean isMinutes = minutes != null && !minutes.isBlank() && !minutes.equals("0");
        return isPermanent || isTurns || isMinutes;
    }

    private boolean isMiss(SkillData skillData, Being<?> caster, Being<?> target) {
        if (caster == null || target == null) {
            return false;
        }
        if (!skillData.isCanBeDodged()) return false;
        double HIT = caster.stats().hitValue();
        double SIZE = target.size().getCurrent().stream().findFirst().orElse(Size.SMALL).getSizeNumber();
        double LUCK = caster.stats().luckValue();
        double LVL = caster.stats().lvlValue();
        double chanceMiss = (float) Math.floor(45 - SIZE - Math.floor(HIT / 5) - Math.floor(HIT / 10 - (HIT / SIZE) * 0.5 + Math.floor(LVL * (SIZE / 5) * 0.4) - Math.floor(LUCK / 7) - Math.floor(LUCK / 4) * 0.2 - LUCK * 0.1));
        double chance = Functions.random(100f);
        return chanceMiss >= chance;
    }

    private boolean isDodge(SkillData skillData, Being<?> caster, Being<?> target) {
        if (!skillData.isCanBeDodged() || caster == null || target == null)
            return false;
        double chance = Functions.random(100d);
        double FLEE = target.stats().fleeValue();
        double HIT = caster.stats().hitValue();
        double chanceDodge = Math.floor((FLEE / HIT) + (FLEE - Math.floor(HIT / 6)));
        return chanceDodge >= chance;
    }

    private boolean isBlock(SkillData skillData, Being<?> target) {
        if (!skillData.isCanBeBlocked() || target == null)
            return false;
        return target.canBlock() && target.stats().blockValue() >= Functions.random(100d);
    }

    private boolean isParry(SkillData skillData, Being<?> target) {
        if (!skillData.isCanBeBlocked() || skillData.getAttackType() != AttackType.MELEE || target == null)
            return false;
        return target.canParry() && target.stats().parryValue() >= Functions.random(100d);
    }

    private boolean isCritical(Being<?> caster) {
        if (caster == null) {
            return false;
        }
        return caster.stats().critValue() >= Functions.random(100d);
    }

    private boolean isResisted(SkillData skillData, Being<?> target) {
        if (target == null) {
            return false;
        }
        double randomValue = Functions.random(100d);
        double resistanceValue = 0;
        if (skillData.getBuffType() == BuffType.MAGIC)
            resistanceValue = target.stats().magicResistanceValue();
        else if (skillData.getBuffType() == BuffType.PHYSIC)
            resistanceValue = target.stats().physicResistanceValue();
        return resistanceValue >= randomValue;
    }

    private Damage getDamage(SkillData skillData, Being<?> caster, Being<?> target) {
        if (skillData.getDamage() == null || target == null || caster == null || skillData.getDamage().isEmpty() || skillData.getDamage().matches("0+")) {
            return null;
        }
        double damageValue = Game.getSkillParser().parse(skillData.getDamage(), caster, target);
        boolean isHeal = damageValue < 0;
        // increase damage if critical
        boolean isCritical = !isHeal && isCritical(caster);
        if (isCritical) {
            damageValue *= 1.2;
        }
        // apply coefficients and subtract target def only to damage not heal
        if (!isHeal) {
            damageValue *= getDamageCoefficient(skillData, caster, target);
            damageValue -= target.stats().defValue();
            // damage can not be less than 1
            if (damageValue < 1) {
                damageValue = 1;
            }
        }
        damageValue = Math.floor(damageValue);
        Damage damage = new Damage(damageValue, caster, skillData);
        if (isCritical) {
            damage.setCritical(true);
        }
        if (!damage.isCritical() && isMiss(skillData, caster, target)) {
            damage.setMiss(true);
        }
        else if (!damage.isCritical() && isDodge(skillData, caster, target)) {
            damage.setDodge(true);
        }
        else if (!damage.isCritical() && isBlock(skillData, target))
            damage.setBlock(true);
        else if (!damage.isCritical() && isParry(skillData, target))
            damage.setParry(true);
        return damage;
    }

    private double getDamageCoefficient(SkillData skillData, Being<?> caster, Being<?> target) {
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

    private List<Long> getCastSkills(SkillData skillData) {
        List<Long> list = new ArrayList<>();
        // must cast
        if (skillData.getSkillsMustCast() != null && !skillData.getSkillsMustCast().isEmpty()) {
            final Long mustCastSkill = Functions.randomWithWeights(Functions.normalizePercentsMap(skillData.getSkillsMustCast()));
            list.add(mustCastSkill);
        }
        // can cast
        if (skillData.getSkillsCouldCast() != null && !skillData.getSkillsCouldCast().isEmpty()) {
            skillData.getSkillsCouldCast().forEach((guid, chance) -> {
                double randomValue = Functions.random(100d);
                if (chance >= randomValue) {
                    list.add(guid);
                }
            });
        }
        return list;
    }

    private Long getSummon(SkillData skillData) {
        if (skillData.getSummon() != null && !skillData.getSummon().isEmpty()) {
            return Functions.randomWithWeights(Functions.normalizePercentsMap(skillData.getSummon()));
        }
        else {
            return null;
        }
    }

    private List<Item> getReceivedItems(SkillData skillData) {
        return skillData.getReceiveItems().entrySet().stream()
                .map(entry -> Game.getEntityFactory().items().get(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }


}
