package ru.rdude.rpg.game.logic.gameStates;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.fasterxml.jackson.annotation.*;
import ru.rdude.rpg.game.battleVisual.BattleVisual;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.Monster;
import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.enums.GameState;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.time.TurnChangeObserver;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;
import ru.rdude.rpg.game.visual.GameStateStage;

@JsonPolymorphicSubType("battle")
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public class Battle extends GameStateBase implements TurnChangeObserver, GameStateObserver {

    @JsonIgnore private BattleVisual battleStage;
    private final Party playerSide;
    private int cellX;
    private int cellY;
    private final Party enemySide;
    private boolean playersTurn = true;

    @JsonCreator
    private Battle(@JsonProperty("enemySide") Party enemySide,
                   @JsonProperty("playerSide") Party playerSide) {
        this.enemySide = enemySide;
        this.playerSide = playerSide;
    }

    public Battle(Party enemySide, Cell cell) {
        this.playerSide = Game.getCurrentGame().getCurrentPlayers();
        this.enemySide = enemySide;
        this.cellX = cell.getX();
        this.cellY = cell.getY();
        Game.getCurrentGame().getTurnsManager().subscribe(this);
        Game.getCurrentGame().getGameStateHolder().subscribe(this);
    }

    public Party getEnemySide() {
        return enemySide;
    }

    public Party getAllySide(Being<?> of) {
        for (Being<?> being : playerSide.getBeings()) {
            if (of == being)
                return playerSide;
        }
        for (Being<?> being : enemySide.getBeings()) {
            if (of == being)
                return enemySide;
        }
        return null;
    }

    public Party getEnemySide(Being<?> of) {
        return getAllySide(of) == playerSide ? enemySide : playerSide;
    }

    @Override
    public void turnUpdate() {
        // switch side:
        playersTurn = !playersTurn;
        if (!playersTurn) {
            aiTurn();
        }
    }

    @Override
    public GameState getEnumValue() {
        return GameState.BATTLE;
    }

    @Override
    public GameStateStage getStage() {
        if (battleStage == null) {
            battleStage = new BattleVisual(Game.getCurrentGame().getGameMap().getGameMap().cell(cellX, cellY), this);
        }
        return battleStage;
    }

    @Override
    public void update(GameStateBase oldValue, GameStateBase newValue) {
        if (newValue != this) {
            Game.getCurrentGame().getGameStateHolder().unsubscribe(this);
            Game.getCurrentGame().getTurnsManager().unsubscribe(this);
        }
    }

    private void aiTurn() {
        enemySide.forEach(enemy -> {
            if (enemy instanceof Monster) {
                final Long skillGuid = Functions.randomWithWeights(((MonsterData) enemy.getEntityData()).getSkills());
                final SkillData skill = SkillData.getSkillByGuid(skillGuid);
                Game.getSkillUser().use(skill, enemy, skill.getMainTarget());
            }
        });
        final RunnableAction startNextTurnAction = Actions.run(() -> Game.getCurrentGame().getTurnsManager().nextTurn());
        Game.getCurrentGame().getSkillsSequencer().add(startNextTurnAction);
    }
}
