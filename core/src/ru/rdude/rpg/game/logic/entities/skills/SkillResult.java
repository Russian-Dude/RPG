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
    private final int staminaUsed;
    private final Cast cast;

    public SkillResult(Cast cast) {
        this(cast, true);
    }

    public SkillResult(Cast cast, boolean justStarted) {
        this(cast.getSkillData(), cast.getCaster(), cast.getTarget(), null, null, null, false, justStarted ? cast.getStaminaUsed() : 0, cast);
    }
    public SkillResult(SkillData skillData,
                       Being<?> caster,
                       Being<?> target,
                       Damage damage,
                       Minion summon,
                       Buff buff,
                       boolean isResisted,
                       int staminaUsed,
                       Cast cast) {

        this.skillData = skillData;
        this.caster = caster;
        this.target = target;
        this.damage = damage;
        this.summon = summon;
        this.buff = buff;
        this.isResisted = isResisted;
        this.staminaUsed = staminaUsed;
        this.cast = cast;
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

    public Optional<Cast> getCast() {
        return Optional.ofNullable(cast);
    }

    public int getStaminaUsed() {
        return staminaUsed;
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
