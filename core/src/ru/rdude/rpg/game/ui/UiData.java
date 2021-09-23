package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import ru.rdude.rpg.game.logic.game.Game;

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
    public final static Drawable UNKNOWN_IMAGE_64X64 = DEFAULT_SKIN.getDrawable("Unknown_Image_64x64");
    public static float UNIT_SIZE = Gdx.graphics.getWidth() / 100f;


    public static class Cursor {

        public static final Cursor DEFAULT = new Cursor("Cursor_Cool", "Cursor_Cool_Select");

        public final com.badlogic.gdx.graphics.Cursor SIMPLE;
        public final com.badlogic.gdx.graphics.Cursor SELECT;

        private Cursor(String simpleImage, String selectionImage) {
            Pixmap simplePixmap = new Pixmap(Gdx.files.internal("images\\cursor\\" + simpleImage + ".png"));
            Pixmap selectionPixmap = new Pixmap(Gdx.files.internal("images\\cursor\\" + selectionImage + ".png"));
            SIMPLE = Gdx.graphics.newCursor(simplePixmap, 0, 0);
            SELECT = Gdx.graphics.newCursor(selectionPixmap, selectionPixmap.getWidth() / 2, selectionPixmap.getHeight() / 2);
            simplePixmap.dispose();
            selectionPixmap.dispose();
        }
    }

    public static class ItemBorder {
        public static Drawable BRONZE = DEFAULT_SKIN.getDrawable("Slot_Border_Bronze");
        public static Drawable SILVER = DEFAULT_SKIN.getDrawable("Slot_Border_Silver");
        public static Drawable GOLD = DEFAULT_SKIN.getDrawable("Slot_Border_Gold");
        public static Drawable QUEST = DEFAULT_SKIN.getDrawable("Slot_Border_Quest");
    }

}
