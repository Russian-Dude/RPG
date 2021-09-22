package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import ru.rdude.rpg.game.logic.entities.beings.Player;

public class PlayerInfoWindow extends Window {

    public PlayerInfoWindow(Player player) {
        super(player.getName(), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        Button closeButton = new TextButton("X", UiData.DEFAULT_SKIN, UiData.NO_SQUARE_BUTTON_STYLE);
        getTitleTable().padTop(20);
        getTitleTable().columnDefaults(0).minHeight(100f);
        getTitleTable().add(closeButton).padTop(25);
        getTitleTable().pack();
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlayerInfoWindow.this.setVisible(false);
            }
        });

        Table mainTable = new Table();
        mainTable.defaults().align(Align.top).space(20f);
        mainTable.padTop(25f);

        mainTable.add(new Label("S T A T S", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        mainTable.add(new Label("C L A S S", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE)).colspan(2);
        mainTable.row();

        PlayerStatsVisual playerStatsVisual = new PlayerStatsVisual(player);
        mainTable.add(playerStatsVisual).padLeft(15f);

        PlayerClassVisualHolder playerClassVisualHolder = new PlayerClassVisualHolder(player);
        mainTable.add(playerClassVisualHolder);

        PlayerClassesSelectorVisual playerClassesSelectorVisual = new PlayerClassesSelectorVisual(player);
        mainTable.add(playerClassesSelectorVisual).fillY();

        add(mainTable).size(mainTable.getPrefWidth(), mainTable.getPrefHeight()).align(Align.top);

        mainTable.pack();
        pack();
    }
}
