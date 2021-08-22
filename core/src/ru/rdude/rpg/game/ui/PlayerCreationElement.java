package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ru.rdude.rpg.game.logic.entities.beings.Player;

public class PlayerCreationElement extends Tree.Node<PlayerCreationElement, Player, Table> {

    private final Label name = new Label("Default Player Name", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final TextButton deleteButton = new TextButton("X", UiData.DEFAULT_SKIN, UiData.NO_SQUARE_BUTTON_STYLE);
    private final PlayerCreationVisual playerCreationVisual;

    public PlayerCreationElement(PlayerCreationVisual playerCreationVisual) {
        super();
        this.setValue(playerCreationVisual.getPlayer());
        this.playerCreationVisual = playerCreationVisual;
        Table mainTable = new Table();
        mainTable.add(name).space(10f).minWidth(name.getWidth()).maxWidth(name.getWidth());
        mainTable.add(deleteButton).space(10f);
        mainTable.pack();
        setActor(mainTable);
    }

    public void onDeletePressed(ClickListener clickListener) {
        deleteButton.addListener(clickListener);
    }

    public void changeName(String name) {
        this.name.setText(name);
        this.getPlayer().getEntityData().setName(name);
        this.getPlayer().setName(name);
    }

    public PlayerCreationVisual getPlayerCreationVisual() {
        return playerCreationVisual;
    }

    public Player getPlayer() {
        return this.getValue();
    }
}
