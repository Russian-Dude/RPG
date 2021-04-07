package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.entities.beings.BeingActionObserver;
import ru.rdude.rpg.game.logic.entities.states.StateChanger;
import ru.rdude.rpg.game.logic.enums.StatName;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.stats.Stats;
import ru.rdude.rpg.game.logic.time.*;
import ru.rdude.rpg.game.utils.Functions;

import java.util.Set;


public class Buff extends Entity implements TurnChangeObserver, TimeChangeObserver, BeingActionObserver, DurationObserver, StateChanger {

    private Set<BuffObserver> subscribers;

    private SkillApplier skillApplier;
    private SkillData skillData;
    private SkillDuration duration;
    private Double damage;
    private boolean permanent;



    private Double actsMinutes;
    private Double actsTurns;

    private Stats stats;


    public Buff(SkillApplier skillApplier) {
        this.skillApplier = skillApplier;
        this.skillData = skillApplier.skillData;
        this.actsMinutes = skillData.getActsEveryMinute() > 0 ? skillData.getActsEveryMinute() : null;
        this.actsTurns = skillData.getActsEveryTurn() > 0 ? skillData.getActsEveryTurn() : null;
        this.permanent = skillData.isPermanent();
        if (!permanent) {
            Game.getCurrentGame().getTimeManager().subscribe(this);
            duration = createDuration();
            duration.subscribe(this);
            skillApplier.target.subscribe(this);
        }
        stats = createStats();
    }


    private SkillDuration createDuration() {
        SkillParser skillParser = skillApplier.skillParser;
        TimeManager timeManager = Game.getCurrentGame().getTimeManager();
        Double turns = skillData.getDurationInTurns() == null || skillData.getDurationInTurns().equals("0") ?
                null : skillParser.parse(skillData.getDurationInTurns());
        Double minutes = skillData.getDurationInMinutes() == null || skillData.getDurationInMinutes().equals("0") ?
                null : skillParser.parse(skillData.getDurationInTurns());
        Double hitsReceived = skillData.getHitsReceived() == null || skillData.getHitsReceived().equals("0") ?
                null : skillParser.parse(skillData.getHitsReceived());
        Double hitsMade = skillData.getHitsMade() == null || skillData.getHitsMade().equals("0") ?
                null : skillParser.parse(skillData.getHitsMade());
        Double damageReceived = skillData.getDamageReceived() == null || skillData.getDamageReceived().equals("0") ?
                null : skillParser.parse(skillData.getDamageReceived());
        Double damageMade = skillData.getDamageMade() == null || skillData.getDamageMade().equals("0") ?
                null : skillParser.parse(skillData.getDamageMade());
        return new SkillDuration(timeManager, minutes, turns, hitsReceived, hitsMade, damageReceived, damageMade);
    }

    private Stats createStats() {
        Stats stats = new Stats(false);
        stats.forEachWithNestedStats(stat ->
                stat.set(skillData.getStats().containsKey(StatName.get(stat.getClass())) ? skillApplier.skillParser.parse(skillData.getStats().get(StatName.get(stat.getClass()))) - skillApplier.target.stats().get(stat.getClass()).value() : 0));
        return stats;
    }


    public void remove() {
        Game.getCurrentGame().getTimeManager().unsubscribe(this);
        skillApplier.target.unsubscribe(this);
        notifySubscribers(true);
    }

    public void setDamage(Double damage) {
        this.damage = damage;
    }

    public Stats getStats() {
        return stats;
    }

    public SkillData getSkillData() {
        return skillData;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public Being getCaster() {
        return skillApplier.caster;
    }

    private void onTimeOrTurnUpdate() {
        if (damage != null) {
            if (skillData.isRecalculateStatsEveryIteration())
                skillApplier.target.receive(new Damage(skillApplier.skillParser.parse(skillData.getDamage()), skillApplier.caster, skillData));
            else skillApplier.target.receive(new Damage(damage, skillApplier.caster, skillData));
        }
        stats.increase(skillData.isRecalculateStatsEveryIteration() ? createStats() : stats);
        if (skillData.getTimeChange() != 0)
            Game.getCurrentGame().getTimeManager().increaseTime((int) skillData.getTimeChange());
    }

    @Override
    public void turnUpdate() {
        duration.turnUpdate();
        if (actsTurns != null) {
            actsTurns--;
            if (actsTurns == 0) {
                actsTurns = skillData.getActsEveryTurn();
                onTimeOrTurnUpdate();
            }
        }
    }

    @Override
    public void timeUpdate(int minutes) {
        if (actsMinutes != null) {
            actsMinutes--;
            if (actsMinutes == 0) {
                actsMinutes = skillData.getActsEveryMinute();
                onTimeOrTurnUpdate();
            }
        }
    }

    public void subscribe(BuffObserver subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(BuffObserver subscriber) {
        subscribers.remove(subscriber);
    }

    private void notifySubscribers(boolean ends) {
        subscribers.forEach(subscriber -> subscriber.update(this, ends));
    }

    @Override
    public void update(BeingAction action, Being being) {
        if (skillData.getSkillsOnBeingAction().containsKey(action.action())) {
            skillData.getSkillsOnBeingAction().get(action.action()).forEach((guid, chance) -> {
                if (chance >= Functions.random(100f)) {
                    if (skillData.isOnBeingActionCastToEnemy()) {
                        SkillApplier.apply(SkillData.getSkillByGuid(guid), being, (Being) action.interactor());
                    }
                    else {
                        SkillApplier.apply(SkillData.getSkillByGuid(guid), skillApplier.caster, skillApplier.target);
                    }
                }
            });
        }
    }

    @Override
    public void update(Duration duration, boolean ends) {
        if (ends) remove();
    }

    @Override
    public boolean isStateOverlay() {
        return skillData.getTransformation().isOverride();
    }

    @Override
    public String getName() {
        return skillData.getName();
    }
}
