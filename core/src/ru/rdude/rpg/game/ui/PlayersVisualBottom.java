package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.entities.beings.Player;

public class PlayersVisualBottom extends HorizontalGroup {
    public PlayersVisualBottom() {
        space(30);
        align(Align.center);
        for (int i = 0; i < 3; i++) {
            addActor(new PlayerVisual(new Player()));
        }
        setWidth(Gdx.graphics.getWidth());
        setHeight(getPrefHeight());
    }
}
