package ru.rdude.rpg.game.ui.colors;

import com.badlogic.gdx.graphics.Color;

public enum EyesColor {

    BLACK(0.1f, 0.1f, 0.1f),
    GREEN(0.2f, 0.7f, 0.2f),
    BLUE(0.2f, 0.2f, 1f);

    private Color color;

    EyesColor(float r, float g, float b) {
        color = new Color(r, g, b, 1);
    }

    public Color getColor() {
        return color;
    }
}
