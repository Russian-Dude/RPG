package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class ResizeableButton extends Button {

    public ResizeableButton(Skin skin) {
        super(skin);
    }

    public ResizeableButton(Skin skin, String styleName) {
        super(skin, styleName);
    }

    public ResizeableButton(Actor child, Skin skin, String styleName) {
        super(child, skin, styleName);
    }

    public ResizeableButton(Actor child, ButtonStyle style) {
        super(child, style);
    }

    public ResizeableButton(ButtonStyle style) {
        super(style);
    }

    public ResizeableButton() {
    }

    public ResizeableButton(Drawable up) {
        super(up);
    }

    public ResizeableButton(Drawable up, Drawable down) {
        super(up, down);
    }

    public ResizeableButton(Drawable up, Drawable down, Drawable checked) {
        super(up, down, checked);
    }

    public ResizeableButton(Actor child, Skin skin) {
        super(child, skin);
    }

    @Override
    public float getPrefWidth() {
        return getWidth();
    }

    @Override
    public float getPrefHeight() {
        return getHeight();
    }
}
