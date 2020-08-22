package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import ru.rdude.rpg.game.logic.entities.items.Item;

public class ItemInfoTooltip extends Tooltip<Table> {

    private Table mainTable;
    private Label name;

    public ItemInfoTooltip(Item item) {
        super(new Table(UiData.DEFAULT_SKIN));
        mainTable = getActor();
        mainTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
        setInstant(true);
        name = new Label("Item name", UiData.DEFAULT_SKIN);
        mainTable.add(name);
        mainTable.setSize(mainTable.getPrefWidth(), mainTable.getPrefHeight());
    }
}
