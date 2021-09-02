package ru.rdude.rpg.game.logic.entities.skills;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.utils.SubscribersManager;

public class Cast {

    private SubscribersManager<CastObserver> subscribers;

    private SkillData skillData;
    private Being<?> caster;
    private Being<?> target;
    private double required;
    private double current;
    private int staminaUsed;

    private Cast() { }

    public Cast(SkillData skillData, Being<?> caster, Being<?> target, int staminaUsed) {
        this.subscribers = new SubscribersManager<>();
        this.skillData = skillData;
        this.caster = caster;
        this.target = target;
        this.required = skillData.getConcentrationReq();
        this.current = caster.stats().concentrationValue();
        this.staminaUsed = staminaUsed;
    }

    @JsonProperty("skillData")
    private long getSkillDataGuid() {
        return skillData.getGuid();
    }

    @JsonProperty("skillData")
    private void setSkillDataByGuid(long guid) {
        this.skillData = SkillData.getSkillByGuid(guid);
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

    public double getRequired() {
        return required;
    }

    public double getCurrent() {
        return current;
    }

    public int getStaminaUsed() {
        return staminaUsed;
    }

    public void update() {
        current += caster.stats().concentrationValue();
        subscribers.notifySubscribers(subscriber -> subscriber.castUpdate(this));
    }

    public boolean isComplete() {
        return current >= required;
    }

    public void subscribe(CastObserver subscriber) {
        subscribers.subscribe(subscriber);
    }

    public void unsubscribe(CastObserver subscriber) {
        subscribers.unsubscribe(subscriber);
    }
}
