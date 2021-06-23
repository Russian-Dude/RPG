package ru.rdude.rpg.game.logic.entities.skills;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.entities.beings.BeingActionObserver;
import ru.rdude.rpg.game.logic.time.Duration;
import ru.rdude.rpg.game.logic.time.TimeManager;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPolymorphicSubType("skillDuration")
public class SkillDuration extends Duration implements BeingActionObserver {

    private Double hitsReceived;
    private Double hitsMade;
    private Double damageReceived;
    private Double damageMade;

    protected SkillDuration() { }

    public SkillDuration(TimeManager timeManager, Double minutes, Double turns, Double hitsReceived, Double hitsMade, Double damageReceived, Double damageMade) {
        super(timeManager, minutes, turns);
        this.hitsReceived = hitsReceived;
        this.hitsMade = hitsMade;
        this.damageReceived = damageReceived;
        this.damageMade = damageMade;
    }

    @Override
    protected void checkDurationEnds() {
        boolean endsTime = (turns == null || turns < 0) && (minutes == null || minutes < 0);
        boolean endsAction =
                (hitsReceived == null || hitsReceived <= 0)
                        && (hitsMade == null || hitsMade <= 0)
                        && (damageReceived == null || damageReceived <= 0)
                        && (damageMade == null || damageMade <= 0);
        notifySubscribers(endsTime && endsAction);
    }

    @Override
    public void update(BeingAction action, Being being) {
        switch (action.action()) {
            case DAMAGE_DEAL:
                if (damageMade != null) damageMade -= action.value();
                if (hitsMade != null) hitsMade -= 1;
                break;
            case DAMAGE_RECEIVE:
            case CRITICAL_RECEIVE:
                if (damageReceived != null) damageReceived -= action.value();
                if (hitsReceived != null) hitsReceived -= 1;
                break;
        }
        checkDurationEnds();
    }

}
