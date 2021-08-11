package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.game.CurrentGameObserver;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.GameStateHolder;
import ru.rdude.rpg.game.logic.gameStates.GameStateObserver;
import ru.rdude.rpg.game.logic.time.TurnChangeObserver;
import ru.rdude.rpg.game.logic.time.TurnsManager;

@JsonIgnoreType
public class EndTurnButton extends TextButton implements TurnChangeObserver, CurrentGameObserver, GameStateObserver {

    private final TurnsManager turnsManager;
    final GameStateHolder gameStateHolder;

    public EndTurnButton() {
        super("End turn", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Game.getCurrentGame().getTurnsManager().nextTurn();
            }
        });
        turnsManager = Game.getCurrentGame().getTurnsManager();
        turnsManager.subscribe(this);
        gameStateHolder = Game.getCurrentGame().getGameStateHolder();
        gameStateHolder.subscribe(this);
        Game.subscribe(this);
        setVisible(Game.getCurrentGame().getCurrentGameState() instanceof Battle);
    }

    @Override
    public void turnUpdate() {
        setVisible(!isVisible());
    }

    @Override
    public void update(Game game, Action action) {
        if (action == Action.BECOME_CURRENT) {
            Game.unsubscribe(this);
            turnsManager.unsubscribe(this);
            gameStateHolder.unsubscribe(this);
        }
    }

    @Override
    public void update(GameStateBase oldValue, GameStateBase newValue) {
        setVisible(newValue instanceof Battle);
    }
}
