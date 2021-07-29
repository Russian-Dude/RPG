package ru.rdude.rpg.game.logic.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomParallelAction extends Action {

    private final List<Action> actions = new ArrayList<>();
    private final List<Action> actionsToClear = new ArrayList<>();
    private boolean complete = false;

    public CustomParallelAction() {
    }

    public CustomParallelAction(Action action) {
        actions.add(action);
    }

    public CustomParallelAction(Action action, Action... actions) {
        this.actions.add(action);
        this.actions.addAll(Arrays.asList(actions));
    }

    public void clear() {
        actions.clear();
        actionsToClear.clear();
    }

    public void addAction(Action action) {
        actions.add(action);
        final Actor actor = getActor();
        if (actor != null) {
            action.setActor(actor);
        }
    }

    @Override
    public void restart() {
        super.restart();
        complete = false;
        actions.forEach(Action::restart);
    }

    @Override
    public void setActor(Actor actor) {
        super.setActor(actor);
        actions.forEach(action -> action.setActor(actor));
    }

    @Override
    public void reset() {
        super.reset();
        actions.clear();
    }

    @Override
    public boolean act(float v) {
        if (complete) {
            complete = false;
            return true;
        }
        for (Action action : actions) {
            boolean currentComplete = action.act(v);
            if (currentComplete) {
                actionsToClear.add(action);
            }
        }
        for (Action action : actionsToClear) {
            actions.remove(action);
        }
        actionsToClear.clear();
        if (actions.isEmpty()) {
            complete = true;
        }
        return complete;
    }
}
