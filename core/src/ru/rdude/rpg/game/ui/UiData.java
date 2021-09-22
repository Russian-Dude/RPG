package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class UiData {

    public final static Skin DEFAULT_SKIN = new Skin(Gdx.files.internal("main_ui.json"));
    public final static String BIG_TEXT_STYLE = "mud";
    public final static String SMALL_TEXT_STYLE = "simple";
    public final static String YES_BUTTON_STYLE = BIG_TEXT_STYLE.equals("default") ? "yes" : BIG_TEXT_STYLE + "_yes";
    public final static String NO_BUTTON_STYLE = BIG_TEXT_STYLE.equals("default") ? "no" : BIG_TEXT_STYLE + "_no";
    public final static String YES_SQUARE_BUTTON_STYLE = BIG_TEXT_STYLE.equals("default") ? "square_yes" : "square_" + BIG_TEXT_STYLE + "_yes";
    public final static String NO_SQUARE_BUTTON_STYLE = BIG_TEXT_STYLE.equals("default") ? "square_no" : "square_" + BIG_TEXT_STYLE + "_no";
    public final static String TRANSPARENT_BUTTON_STYLE = "transparent";
    public final static String RED_GREEN_CHECKBOX = "Red_Green_mud";
    public final static Drawable SEMI_TRANSPARENT_BACKGROUND = DEFAULT_SKIN.getDrawable("Window_Transparent_9p");
    public final static Drawable DEFAULT_BACKGROUND = DEFAULT_SKIN.getDrawable("Window_9p");
    public static float UNIT_SIZE = Gdx.graphics.getWidth() / 100f;

    public static class ItemBorder {
        public static Drawable BRONZE = DEFAULT_SKIN.getDrawable("Slot_Border_Bronze");
        public static Drawable SILVER = DEFAULT_SKIN.getDrawable("Slot_Border_Silver");
        public static Drawable GOLD = DEFAULT_SKIN.getDrawable("Slot_Border_Gold");
        public static Drawable QUEST = DEFAULT_SKIN.getDrawable("Slot_Border_Quest");
    }

}
