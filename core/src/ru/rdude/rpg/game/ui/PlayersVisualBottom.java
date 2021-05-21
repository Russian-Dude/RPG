package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.utils.Align;

public class PlayersVisualBottom extends HorizontalGroup {

    public PlayersVisualBottom(PlayerVisual... playersVisual) {
        space(60);
        align(Align.center);
        for (PlayerVisual player : playersVisual) {
            addActor(player);
        }
        setWidth(Gdx.graphics.getWidth());
        setHeight(getPrefHeight());
    }
}
