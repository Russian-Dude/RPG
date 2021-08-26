package ru.rdude.rpg.game.visual;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Pool;
import ru.rdude.rpg.game.ui.UiData;

public class DamageLabel extends Container<Label> implements Pool.Poolable {

    private final Label label = new Label("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);

    public DamageLabel() {
        super();
        setActor(label);
        setTransform(true);
    }

    public void setText(String text) {
        label.setText(text);
    }

    @Override
    public void reset() {
        //label.setText("");
    }
}
