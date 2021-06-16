package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class ItemRemoverArea extends Group {

    public ItemRemoverArea() {
        setTouchable(Touchable.disabled);
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
}
