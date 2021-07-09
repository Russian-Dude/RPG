package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.entities.beings.BeingActionObserver;
import ru.rdude.rpg.game.utils.Functions;

@JsonIgnoreType
public class DamageLabel extends Label implements BeingActionObserver {

    public DamageLabel(Being<?> being) {
        super("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        being.subscribe(this);
    }

    @Override
    public void update(BeingAction action, Being<?> being) {
        if (action.action() == BeingAction.Action.DAMAGE_RECEIVE) {
            this.setText(Functions.trimDouble(action.value()));
            this.addAction(
                    Actions.sequence(
                            Actions.parallel(
                                    Actions.sequence(Actions.alpha(0f), Actions.fadeIn(2f), Actions.fadeOut(1f)),
                                    Actions.moveBy(0f, 100f, 3f)
                            ), Actions.moveBy(0f, -100f)));
        }
    }
}
