package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.entities.beings.Player;

public class PlayersVisualBottom extends HorizontalGroup {

    public PlayersVisualBottom(Player... players) {
        space(60);
        align(Align.center);
        for (Player player : players) {
            addActor(new PlayerVisual(player));
        }
        setWidth(Gdx.graphics.getWidth());
        setHeight(getPrefHeight());
    }
}
