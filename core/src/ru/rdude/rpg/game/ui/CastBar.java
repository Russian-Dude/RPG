package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.actions.WaitAction;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.skills.Cast;

public class CastBar extends VerticalGroup {

    private Cast cast;

    private final ProgressBar progressBar = new ProgressBar(0f, 0f, 1f, false, UiData.DEFAULT_SKIN, "concentration");
    private final Label name = new Label("  Name of the spell  ", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label current = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label max = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label slash = new Label(" / ", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final HorizontalGroup horizontalLabelGroup = new HorizontalGroup();

    public CastBar(Being<?> being) {

        // initial values
        Cast cast = being.getCast();
        if (cast != null) {
            this.cast = cast;
            progressBar.setRange(0, (float) this.cast.getRequired());
            progressBar.setValue((float) this.cast.getCurrent());
            name.setText(cast.getSkillData().getName());
            current.setText((int) cast.getCurrent());
            max.setText((int) cast.getRequired());
        }

        // config actors
        progressBar.setAnimateDuration(0.3f);
        horizontalLabelGroup.addActor(current);
        horizontalLabelGroup.addActor(slash);
        horizontalLabelGroup.addActor(max);
        horizontalLabelGroup.setSize(progressBar.getWidth(), progressBar.getHeight());
        horizontalLabelGroup.align(Align.center);
        Group barGroup = new Group();
        barGroup.addActor(progressBar);
        barGroup.addActor(horizontalLabelGroup);
        barGroup.setWidth(progressBar.getWidth());
        barGroup.setHeight(progressBar.getHeight());
        addActor(name);
        addActor(barGroup);
        align(Align.center);
        setSize(getPrefWidth(), getPrefHeight());
        if (this.cast == null) {
            addAction(Actions.fadeOut(0f));
        }

        setTouchable(Touchable.disabled);
    }


    public Action createUpdateAction(Cast cast) {
        SequenceAction resultAction = Actions.sequence();
        // if cast set to null
        if (cast == null) {
            AlphaAction fadeOut = Actions.fadeOut(0.1f);
            fadeOut.setTarget(this);
            RunnableAction removeCast = Actions.run(() -> this.cast = null);
            RunnableAction resetValue = Actions.run(() -> progressBar.setValue(0f));
            resultAction.addAction(removeCast);
            resultAction.addAction(fadeOut);
            resultAction.addAction(resetValue);
            return resultAction;
        }
        // if cast is the same
        if (this.cast == cast) {
            RunnableAction changeValues = Actions.run(() -> {
                progressBar.setValue((float) Math.min(progressBar.getMaxValue(), cast.getCurrent()));
                current.setText((int) cast.getCurrent());
            });
            WaitAction waitAction = Actions.action(WaitAction.class);
            waitAction.setDuration(0.3f);
            resultAction.addAction(changeValues);
            resultAction.addAction(waitAction);
            // if cast is over
            if (cast.isComplete()) {
                AlphaAction fadeOut = Actions.fadeOut(0.1f);
                fadeOut.setTarget(this);
                RunnableAction removeCast = Actions.run(() -> this.cast = null);
                RunnableAction resetValue = Actions.run(() -> progressBar.setValue(0f));
                resultAction.addAction(removeCast);
                resultAction.addAction(fadeOut);
                resultAction.addAction(resetValue);
            }
        }
        // if cast is another
        else {
            // if another one was casting
            if (this.cast != null) {
                AlphaAction fadeOut = Actions.fadeOut(0.3f);
                fadeOut.setTarget(this);
                RunnableAction clearValue = Actions.run(() -> progressBar.setValue(0f));
                WaitAction wait = Actions.action(WaitAction.class);
                wait.setDuration(3f);
                resultAction.addAction(fadeOut);
                resultAction.addAction(clearValue);
                resultAction.addAction(wait);
            }

            RunnableAction setValues = Actions.run(() -> {
                name.setText(cast.getSkillData().getName());
                progressBar.setRange(0f, (float) cast.getRequired());
                current.setText((int) cast.getCurrent());
                max.setText((int) cast.getRequired());
            });
            AlphaAction fadeIn = Actions.fadeIn(0.1f);
            RunnableAction setCurrent = Actions.run(() -> progressBar.setValue((float) cast.getCurrent()));
            fadeIn.setTarget(this);
            WaitAction wait = Actions.action(WaitAction.class);
            wait.setDuration(0.2f);
            RunnableAction setCast = Actions.run(() -> this.cast = cast);
            resultAction.addAction(setCast);
            resultAction.addAction(setValues);
            resultAction.addAction(setCurrent);
            resultAction.addAction(fadeIn);
            resultAction.addAction(wait);
        }
        return resultAction;
    }


}
