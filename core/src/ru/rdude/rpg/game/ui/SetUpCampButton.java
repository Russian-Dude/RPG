package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.*;

@JsonIgnoreType
public class SetUpCampButton extends TextButton implements GameStateObserver, CampSetupObserver {

    public SetUpCampButton() {
        super("  3 more battles before you can set up a camp  ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        final Map map = Game.getCurrentGame().getGameMap();
        Game.getCurrentGame().getGameStateHolder().subscribe(this);
        final CampSetup campSetup = Game.getCurrentGame().getCampSetup();
        campSetup.subscribe(this);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isDisabled()) {
                    return;
                }

                if (Game.getCurrentGame().getCurrentGameState() instanceof Camp) {
                    Game.getGameStateSwitcher().switchToMap();
                }
                else {
                    Game.getGameStateSwitcher().switchToCamp();
                }
            }
        });

        update(null, Game.getCurrentGame().getCurrentGameState());
        updateBeforeSetUpCampAllowed(campSetup, 0, campSetup.getBeforeNextCampAllowed());
    }

    @Override
    public void update(GameStateBase oldValue, GameStateBase newValue) {
        if (newValue instanceof Camp) {
            setText("Leave camp");
        }
        if (newValue instanceof Map && oldValue instanceof Camp) {
            CampSetup campSetup = Game.getCurrentGame().getCampSetup();
            updateBeforeSetUpCampAllowed(campSetup, 0, campSetup.getBeforeNextCampAllowed());
        }
        setVisible(newValue instanceof Map || newValue instanceof Camp);
        updateDisabledState();
    }

    @Override
    public void updateBeforeSetUpCampAllowed(CampSetup campSetup, int oldValue, int newValue) {
        boolean canSetUp = Game.getCurrentGame().getCampSetup().canSetUpCamp();
        if (Game.getCurrentGame().getCurrentGameState() instanceof Map) {
            setText(canSetUp ? "Set up a camp"
                    : "  " + newValue + " more battles before you can set up a camp  ");
        }
        updateDisabledState();
    }

    private void updateDisabledState() {
        boolean canSetUp = Game.getCurrentGame().getCampSetup().canSetUpCamp();
        boolean enabled = canSetUp || Game.getCurrentGame().getCurrentGameState() instanceof Camp;
        setDisabled(!enabled);
    }
}
