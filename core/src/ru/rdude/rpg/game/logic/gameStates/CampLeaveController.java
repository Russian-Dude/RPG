package ru.rdude.rpg.game.logic.gameStates;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.time.Time;
import ru.rdude.rpg.game.logic.time.TimeManager;

@JsonIgnoreType
public class CampLeaveController implements GameStateObserver {

    private final TimeManager timeManager;

    public CampLeaveController(TimeManager timeManager, GameStateHolder gameStateHolder) {
        this.timeManager = timeManager;
        gameStateHolder.subscribe(this);
    }

    @Override
    public void update(GameStateBase oldValue, GameStateBase newValue) {
        if (oldValue instanceof Camp) {
            timeManager.increaseTime(Time.Hours.toMinutes(8));
            Game.getCurrentGame().getCurrentPlayers().stream()
                    .filter(Being::isReady)
                    .forEach(being -> {
                        being.stats().hp().set(being.stats().hp().maxValue());
                        being.stats().stm().set(being.stats().stm().maxValue());
                    });
        }
    }
}
