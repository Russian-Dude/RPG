package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class UiData {

    public static Skin DEFAULT_SKIN = new Skin(Gdx.files.internal("main_ui.json"));
    public static float UNIT_SIZE = Gdx.graphics.getWidth() / 100f;

    public static Image getItemImage(String name) {
        return new Image(new Texture(Gdx.files.internal("images\\item\\items\\" + name + ".png")));
    }

    public static class ItemBorder {
        public static Image BRONZE = new Image(DEFAULT_SKIN.getDrawable("Slot_Border_Bronze"));
        public static Image SILVER = new Image(DEFAULT_SKIN.getDrawable("Slot_Border_Silver"));
        public static Image GOLD = new Image(DEFAULT_SKIN.getDrawable("Slot_Border_Gold"));
    }

}
