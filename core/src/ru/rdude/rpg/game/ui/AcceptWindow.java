package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;

public class AcceptWindow extends Window {

    private HorizontalGroup buttons;
    private TextButton yes;
    private TextButton no;

    public AcceptWindow(String title, String text) {
        this(title, text, "YES", "NO");
    }

    public AcceptWindow(String title, String text, String yes, String no) {
        super(title, UiData.DEFAULT_SKIN);
        this.yes = new TextButton(yes, UiData.DEFAULT_SKIN, "yes");
        this.no = new TextButton(no, UiData.DEFAULT_SKIN, "no");
        buttons = new HorizontalGroup();
        buttons.addActor(this.yes);
        buttons.addActor(this.no);
        buttons.space(15f);
        buttons.setSize(buttons.getPrefWidth(), buttons.getPrefHeight());
        add(text).space(15).row();
        add(buttons).row();
        pack();
    }
}
