package ru.rdude.rpg.game.logic.entities.beings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.enums.GameState;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.logic.time.TimeChangeObserver;
import ru.rdude.rpg.game.logic.time.TurnChangeObserver;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.Optional;

@JsonPolymorphicSubType("minion")
public class Minion extends Monster implements TurnChangeObserver, TimeChangeObserver {

    private Integer turnsDuration;
    private Integer timeDuration;
    private Being<?> master;

    @JsonCreator
    protected Minion(@JsonProperty("entityData") long guid) {
        super(guid);
    }

    public Minion(MonsterData monsterData, Integer turnsDuration, Integer timeDuration, Being<?> master) {
        super(monsterData);
        this.turnsDuration = turnsDuration;
        this.timeDuration = timeDuration;
        this.master = master;
        master.addMinion(this);
        if (timeDuration != null) {
            Game.getCurrentGame().getTimeManager().subscribe(this);
        }
        if (turnsDuration != null) {
            Game.getCurrentGame().getTurnsManager().subscribe(this);
        }
    }

    public Optional<Integer> getTurnsDuration() {
        return Optional.ofNullable(turnsDuration);
    }

    public Optional<Integer> getTimeDuration() {
        return Optional.ofNullable(timeDuration);
    }

    public Being<?> getMaster() {
        return master;
    }

    @Override
    public void timeUpdate(int minutes) {
        if (timeDuration != null) {
            timeDuration -= minutes;
            if (timeDuration <= 0) {
                stats.hp().set(0d);
                Game.getCurrentGame().getTimeManager().unsubscribe(this);
                Game.getCurrentGame().getTurnsManager().unsubscribe(this);
                master.removeMinion(this);
            }
        }
    }

    @Override
    public void turnUpdate() {
        if (turnsDuration != null) {
            final GameStateBase currentGameState = Game.getCurrentGame().getCurrentGameState();
            if (currentGameState.getEnumValue() == GameState.BATTLE && !((Battle) currentGameState).getCurrentSide().getBeings().contains(this)) {
                turnsDuration -= 1;
                if (turnsDuration <= 0) {
                    alive = false;
                    ((Battle) Game.getCurrentGame().getCurrentGameState()).getAllySide(this).remove(this);
                    Game.getCurrentGame().getTimeManager().unsubscribe(this);
                    Game.getCurrentGame().getTurnsManager().unsubscribe(this);
                    master.removeMinion(this);
                }
            }
        }
    }

}
