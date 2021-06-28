package ru.rdude.rpg.game.logic.entities.skills;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
import ru.rdude.rpg.game.utils.SubscribersManager;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("buff")
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public class Buff extends Entity<SkillData> implements TurnChangeObserver, TimeChangeObserver, BeingActionObserver, DurationObserver, StateChanger {

    private final SubscribersManager<BuffObserver> subscribers = new SubscribersManager<>();

    private SkillApplier skillApplier;
    private SkillDuration duration;
    private Double damage;

    private Double actsMinutes;
    private Double actsTurns;

    private Stats stats;

    @JsonCreator
    private Buff(@JsonProperty("entityData") long guid) {
        super(guid);
    }

    public Buff(SkillApplier skillApplier) {
        super(skillApplier.skillData);
        this.skillApplier = skillApplier;
        this.actsMinutes = entityData.getActsEveryMinute() > 0 ? entityData.getActsEveryMinute() : null;
        this.actsTurns = entityData.getActsEveryTurn() > 0 ? entityData.getActsEveryTurn() : null;
        if (!entityData.isPermanent()) {
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
        Double turns = parsableString(entityData.getDurationInTurns()) ?
                skillParser.parse(entityData.getDurationInTurns()) : null;
        Double minutes = parsableString(entityData.getDurationInMinutes()) ?
                skillParser.parse(entityData.getDurationInMinutes()) : null;
        Double hitsReceived = parsableString(entityData.getHitsReceived()) ?
                skillParser.parse(entityData.getHitsReceived()) : null;
        Double hitsMade = parsableString(entityData.getHitsMade()) ?
                skillParser.parse(entityData.getHitsMade()) : null;
        Double damageReceived = parsableString(entityData.getDamageReceived()) ?
                skillParser.parse(entityData.getDamageReceived()) : null;
        Double damageMade = parsableString(entityData.getDamageMade()) ?
                skillParser.parse(entityData.getDamageMade()) : null;
        return new SkillDuration(timeManager, minutes, turns, hitsReceived, hitsMade, damageReceived, damageMade);
    }

    private boolean parsableString(String string) {
        return string != null && !string.isBlank() && !string.equals("0");
    }

    private Stats createStats() {
        Stats stats = new Stats(false);
        entityData.getStats().entrySet().stream()
                .filter(entry -> !entry.getValue().isBlank())
                .forEach(entry -> {
                    final StatName statName = entry.getKey();
                    final String stringValue = entry.getValue();
                    stats.get(statName).set(skillApplier.skillParser.parse(stringValue) - skillApplier.target.stats().get(statName).value());
                });
        return stats;
    }


    public void remove() {
        Game.getCurrentGame().getTimeManager().unsubscribe(this);
        skillApplier.target.unsubscribe(this);
        duration.unsubscribe(this);
        notifySubscribers(true);
    }

    public Double getDamage() {
        return damage;
    }

    public void setDamage(Double damage) {
        this.damage = damage;
    }

    public Stats getStats() {
        return stats;
    }

    public boolean isPermanent() {
        return entityData.isPermanent();
    }

    public Being<?> getCaster() {
        return skillApplier.caster;
    }

    public SkillDuration getDuration() {
        return duration;
    }

    private void onTimeOrTurnUpdate() {
        if (damage != null) {
            if (entityData.isRecalculateStatsEveryIteration())
                skillApplier.target.receive(new Damage(skillApplier.skillParser.parse(entityData.getDamage()), skillApplier.caster, entityData));
            else skillApplier.target.receive(new Damage(damage, skillApplier.caster, entityData));
        }
        stats.increase(entityData.isRecalculateStatsEveryIteration() ? createStats() : stats);
        if (entityData.getTimeChange() != 0)
            Game.getCurrentGame().getTimeManager().increaseTime((int) entityData.getTimeChange());
    }

    @Override
    public void turnUpdate() {
        duration.turnUpdate();
        if (actsTurns != null) {
            actsTurns--;
            if (actsTurns <= 0) {
                actsTurns = entityData.getActsEveryTurn();
                onTimeOrTurnUpdate();
            }
        }
    }

    @Override
    public void timeUpdate(int minutes) {
        duration.timeUpdate(minutes);
        if (actsMinutes != null) {
            actsMinutes -= minutes;
            if (actsMinutes <= 0) {
                int remainder = (int) (actsMinutes * (-1));
                actsMinutes = entityData.getActsEveryMinute();
                onTimeOrTurnUpdate();
                if (remainder > 0) {
                    timeUpdate(remainder);
                }
            }
        }
    }

    public void subscribe(BuffObserver subscriber) {
        subscribers.subscribe(subscriber);
    }

    public void unsubscribe(BuffObserver subscriber) {
        subscribers.unsubscribe(subscriber);
    }

    private void notifySubscribers(boolean ends) {
        subscribers.notifySubscribers(subscriber -> subscriber.update(this, ends));
    }

    @Override
    public void update(BeingAction action, Being<?> being) {
        if (entityData.getSkillsOnBeingAction().containsKey(action.action())) {
            entityData.getSkillsOnBeingAction().get(action.action()).forEach((guid, chance) -> {
                if (chance >= Functions.random(100f)) {
                    if (entityData.isOnBeingActionCastToEnemy()) {
                        SkillApplier.apply(SkillData.getSkillByGuid(guid), being, (Being<?>) action.interactor());
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
        return entityData.getTransformation().isOverride();
    }

    @Override
    public String getName() {
        return entityData.getName();
    }

    @Override
    protected SkillData getDataByGuid(long guid) {
        return SkillData.getSkillByGuid(guid);
    }

    @Override
    public boolean sameAs(Entity<?> entity) {
        return entity instanceof Buff && entityData.equals(((Buff) entity).entityData);
    }
}
