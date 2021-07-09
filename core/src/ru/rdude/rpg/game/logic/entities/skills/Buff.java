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

import java.util.Optional;

@JsonPolymorphicSubType("buff")
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public class Buff extends Entity<SkillData> implements TurnChangeObserver, TimeChangeObserver, BeingActionObserver, DurationObserver, StateChanger {

    private final SubscribersManager<BuffObserver> subscribers = new SubscribersManager<>();

    private SkillData skillData;
    private Being<?> caster;
    private Being<?> target;
    private Stats stats;
    private Damage damage;

    private SkillDuration duration;
    private Double actsMinutes;
    private Double actsTurns;

    @JsonCreator
    private Buff(@JsonProperty("entityData") long guid) {
        super(guid);
    }

    public Buff(SkillData skillData, Being<?> caster, Being<?> target, Damage damage) {
        super(skillData);
        this.skillData = skillData;
        this.caster = caster;
        this.target = target;
        this.damage = damage;
        this.actsMinutes = entityData.getActsEveryMinute() > 0 ?
                entityData.getActsEveryMinute() : null;
        this.actsTurns = entityData.getActsEveryTurn() > 0 ?
                entityData.getActsEveryTurn() : null;
        if (!skillData.isPermanent()) {
            Game.getCurrentGame().getTimeManager().subscribe(this);
            duration = createDuration();
            duration.subscribe(this);
        }
        if (skillData.getSkillsOnBeingAction() != null && !skillData.getSkillsOnBeingAction().isEmpty()) {
            target.subscribe(this);
        }
        if (skillData.getStats() != null && !skillData.getStats().isEmpty()) {
            stats = createStats();
        }
    }


    private SkillDuration createDuration() {
        SkillParser skillParser = Game.getSkillParser();
        TimeManager timeManager = Game.getCurrentGame().getTimeManager();
        Double turns = parsableString(entityData.getDurationInTurns()) ?
                skillParser.parse(entityData.getDurationInTurns(), caster, target) : null;
        Double minutes = parsableString(entityData.getDurationInMinutes()) ?
                skillParser.parse(entityData.getDurationInMinutes(), caster, target) : null;
        Double hitsReceived = parsableString(entityData.getHitsReceived()) ?
                skillParser.parse(entityData.getHitsReceived(), caster, target) : null;
        Double hitsMade = parsableString(entityData.getHitsMade()) ?
                skillParser.parse(entityData.getHitsMade(), caster, target) : null;
        Double damageReceived = parsableString(entityData.getDamageReceived()) ?
                skillParser.parse(entityData.getDamageReceived(), caster, target) : null;
        Double damageMade = parsableString(entityData.getDamageMade()) ?
                skillParser.parse(entityData.getDamageMade(), caster, target) : null;
        return new SkillDuration(timeManager, minutes, turns, hitsReceived, hitsMade, damageReceived, damageMade);
    }

    public void updateDuration() {
        SkillDuration newDuration = createDuration();
        newDuration.getDamageReceivedLeft().ifPresent(duration::setDamageReceivedLeft);
        newDuration.getHitsReceivedLeft().ifPresent(duration::setHitsReceivedLeft);
        newDuration.getDamageMadeLeft().ifPresent(duration::setDamageMadeLeft);
        newDuration.getHitsMadeLeft().ifPresent(duration::setHitsMadeLeft);
        newDuration.getMinutesLeft().ifPresent(duration::setMinutesLeft);
        newDuration.getTurnsLeft().ifPresent(duration::setTurnsLeft);
    }

    private boolean parsableString(String string) {
        return string != null && !string.isBlank() && !string.equals("0");
    }

    public Stats createStats() {
        SkillParser skillParser = Game.getSkillParser();
        Stats stats = new Stats(false);
        entityData.getStats().entrySet().stream()
                .filter(entry -> !entry.getValue().isBlank())
                .forEach(entry -> {
                    final StatName statName = entry.getKey();
                    final String stringValue = entry.getValue();
                    stats.get(statName).set(skillParser.parse(stringValue, caster, target) - target.stats().get(statName).value());
                });
        return stats;
    }


    public void remove() {
        Game.getCurrentGame().getTimeManager().unsubscribe(this);
        target.unsubscribe(this);
        duration.unsubscribe(this);
        notifySubscribers(true);
    }

    public Optional<Damage> getDamage() {
        return Optional.ofNullable(this.damage);
    }

    public Optional<Stats> getStats() {
        return Optional.ofNullable(this.stats);
    }

    public boolean isPermanent() {
        return entityData.isPermanent();
    }

    public Being<?> getCaster() {
        return caster;
    }

    public Being<?> getTarget() {
        return target;
    }

    public SkillDuration getDuration() {
        return duration;
    }

    private void onTimeOrTurnUpdate() {
        Game.getCurrentGame().getSkillsSequencer().add(this);
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
                    final SkillData skill = SkillData.getSkillByGuid(guid);
                    if (entityData.isOnBeingActionCastToEnemy()) {
                        Game.getCurrentGame().getSkillsSequencer()
                                .add(skill, target, Game.getSkillTargeter().get(target, (Being<?>) action.interactor(), skill.getTargets()));
                    } else {
                        Game.getCurrentGame().getSkillsSequencer()
                                .add(skill, caster, Game.getSkillTargeter().get(caster, target, skill.getTargets()));
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
