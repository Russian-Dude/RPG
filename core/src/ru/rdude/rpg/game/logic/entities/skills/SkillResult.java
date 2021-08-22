package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.Minion;

import java.util.Map;
import java.util.Optional;

public class SkillResult {

    private final SkillData skillData;
    private final Being<?> caster;
    private final Being<?> target;
    private final Damage damage;
    private final Minion summon;
    private final Buff buff;
    private final boolean isResisted;


    public SkillResult(SkillData skillData, Being<?> caster, Being<?> target, Damage damage, Minion summon, Buff buff, boolean isResisted) {
        this.skillData = skillData;
        this.caster = caster;
        this.target = target;
        this.damage = damage;
        this.summon = summon;
        this.buff = buff;
        this.isResisted = isResisted;
    }

    public SkillData getSkillData() {
        return skillData;
    }

    public Being<?> getCaster() {
        return caster;
    }

    public Being<?> getTarget() {
        return target;
    }

    public Optional<Damage> getDamage() {
        return Optional.ofNullable(damage);
    }

    public Optional<Minion> getSummon() {
        return Optional.ofNullable(summon);
    }

    public Optional<Buff> getBuff() {
        return Optional.ofNullable(buff);
    }

    public boolean isResisted() {
        return isResisted;
    }

    public Optional<Map<Long, Integer>> getReceivedItems() {
        if (skillData.getReceiveItems() == null || skillData.getReceiveItems().isEmpty()) {
            return Optional.empty();
        }
        else {
            return Optional.of(skillData.getReceiveItems());
        }
    }
}
