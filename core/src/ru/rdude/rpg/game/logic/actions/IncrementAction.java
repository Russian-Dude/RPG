package ru.rdude.rpg.game.logic.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import java.util.function.Consumer;

public class IncrementAction extends TemporalAction {

    private float from;
    private float to;
    private Consumer<Float> valueSetter;

    public IncrementAction() {
    }

    public IncrementAction(float duration, float from, float to, Consumer<Float> valueSetter) {
        super(duration);
        this.from = from;
        this.to = to;
        this.valueSetter = valueSetter;
    }

    @Override
    protected void update(float percent) {
        final float value;
        if (percent == 0.0F) {
            value = from;
        } else if (percent == 1.0F) {
            value = to;
        } else {
            value = from + (to - from) * percent;
        }
        valueSetter.accept(value);
    }

    public float getFrom() {
        return from;
    }

    public void setFrom(float from) {
        this.from = from;
    }

    public float getTo() {
        return to;
    }

    public void setTo(float to) {
        this.to = to;
    }

    public Consumer<Float> getValueSetter() {
        return valueSetter;
    }

    public void setValueSetter(Consumer<Float> valueSetter) {
        this.valueSetter = valueSetter;
    }
}
