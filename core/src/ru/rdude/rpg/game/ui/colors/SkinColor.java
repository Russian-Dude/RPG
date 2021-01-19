package ru.rdude.rpg.game.ui.colors;

import com.badlogic.gdx.graphics.Color;

public enum SkinColor {
    DARK(0.8f, 0.6549f, 0.6078f),
    TANNED(0.9f, 0.6549f, 0.6078f),
    LIGHT(1f, 0.7049f, 0.6078f),
    LIGHT_TANNED(1, 0.7549f, 0.7078f);

    private Color color;

    SkinColor(float r, float g, float b) {
        color = new Color(r, g, b, 1);
    }

    public Color getColor() {
        return color;
    }
}