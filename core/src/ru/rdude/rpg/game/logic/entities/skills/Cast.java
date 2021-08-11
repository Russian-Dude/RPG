package ru.rdude.rpg.game.logic.entities.skills;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.time.TimeChangeObserver;
import ru.rdude.rpg.game.logic.time.TurnChangeObserver;
import ru.rdude.rpg.game.utils.SubscribersManager;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("cast")
public class Cast implements TurnChangeObserver, TimeChangeObserver {

    private SubscribersManager<CastObserver> subscribers;

    private SkillData skillData;
    private Being<?> caster;
    private Being<?> target;
    private double required;
    private double current;

    private Cast() { }

    public Cast(SkillData skillData, Being<?> caster, Being<?> target) {
        this.subscribers = new SubscribersManager<>();
        this.skillData = skillData;
        this.caster = caster;
        this.target = target;
        this.required = skillData.getConcentrationReq();
        this.current = caster.stats().concentrationValue();
        Game.getCurrentGame().getTimeManager().subscribe(this);
        Game.getCurrentGame().getTurnsManager().subscribe(this);
    }

    @JsonProperty("skillData")
    private long getSkillDataGuid() {
        return skillData.getGuid();
    }

    @JsonProperty("skillData")
    private void setSkillDataByGuid(long guid) {
        this.skillData = SkillData.getSkillByGuid(guid);
    }

    public void subscribe(CastObserver subscriber) {
        subscribers.subscribe(subscriber);
    }

    public void unsubscribe(CastObserver subscriber) {
        subscribers.unsubscribe(subscriber);
    }

    @Override
    public void timeUpdate(int minutes) {
        if (!(Game.getCurrentGame().getCurrentGameState() instanceof Battle)){
            current += caster.stats().concentrationValue() * minutes * 0.2;
            if (current >= required) {

            }
            subscribers.notifySubscribers(castObserver -> {}); // TODO: 30.06.2021 notify cast observers don't forget to unsubscribe THIS
        }
    }

    @Override
    public void turnUpdate() {
        current += caster.stats().concentrationValue();
        if (current >= required) {

        }
        subscribers.notifySubscribers(castObserver -> {}); // TODO: 30.06.2021 notify cast observers dont't forget to unsubscribe THIS
    }
}
