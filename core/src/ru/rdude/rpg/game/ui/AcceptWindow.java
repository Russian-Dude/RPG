package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
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
        super(title, UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        setModal(true);
        this.yes = new TextButton(yes, UiData.DEFAULT_SKIN, UiData.YES_BUTTON_STYLE);
        this.no = new TextButton(no, UiData.DEFAULT_SKIN, UiData.NO_BUTTON_STYLE);
        buttons = new HorizontalGroup();
        buttons.addActor(this.yes);
        buttons.addActor(this.no);
        buttons.space(15f);
        buttons.setSize(buttons.getPrefWidth(), buttons.getPrefHeight());
        add(text).space(15).row();
        add(buttons).row();
        pack();
        setModal(true);
    }

    public void addAcceptListener(EventListener listener) {
        yes.addListener(listener);
    }

    public void removeAcceptListener(EventListener listener) {
        yes.removeListener(listener);
    }

    public void addDeclineListener(EventListener listener) {
        no.addListener(listener);
    }

    public void removeDeclineListener(EventListener listener) {
        no.removeListener(listener);
    }
}
