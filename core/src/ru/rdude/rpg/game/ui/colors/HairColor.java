package ru.rdude.rpg.game.ui.colors;

import com.badlogic.gdx.graphics.Color;

public enum HairColor {
    BLACK(0.1f, 0.1f, 0.1f),
    LIGHT_BLACK(0.2f, 0.2f, 0.2f),
    GREY_BLACK(0.3f, 0.3f, 0.3f),
    GREY(0.7f, 0.7f, 0.7f),
    OLD_GREY(1f, 1f, 1f),
    GOLD_BLOND(0.8f, 0.8f, 0.6f),
    DARK_BLOND(0.5f, 0.5f, 0.3f),
    GINGER(0.8f, 0.3f, 0.1f);

    private Color color;

    HairColor(float r, float g, float b) {
        color = new Color(r, g, b, 1);
    }

    public Color getColor() {
        return color;
    }
}
