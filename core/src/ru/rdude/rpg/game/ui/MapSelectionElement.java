package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MapSelectionElement {

    public final Image image;
    public final String text;

    public MapSelectionElement(String name, Image image) {
        this.image = image;
        this.text = name;
    }

    @Override
    public String toString() {
        return text;
    }
}
