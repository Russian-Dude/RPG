package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ru.rdude.rpg.game.logic.entities.beings.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.rdude.rpg.game.ui.UiData.*;

public class PlayerVisual extends VerticalGroup {

    private static List<PlayerVisual> playerVisualList = new ArrayList<>();

    private Player player;

    private BackpackWindow backpackWindow;
    private EquipmentWindow equipmentWindow;

    private PlayerAvatar avatar;
    private Label name;
    private HpBar hpBar;
    private StmBar stmBar;
    private Button attack;
    private Button spells;
    private Button items;
    private HorizontalGroup buttons;

    public PlayerVisual(Player player) {
        playerVisualList.add(this);
        this.player = player;
        avatar = new PlayerAvatar();
        hpBar = new HpBar(player);
        stmBar = new StmBar(player);
        buttons = new HorizontalGroup();
        attack = new Button(DEFAULT_SKIN, "attack");
        items = new Button(DEFAULT_SKIN, "items");
        spells = new Button(DEFAULT_SKIN, "spells");
        name = new Label(player.getName(), DEFAULT_SKIN);

        backpackWindow = new BackpackWindow(player);
        equipmentWindow = new EquipmentWindow(player);
        items.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                backpackWindow.setVisible(!backpackWindow.isVisible());
                equipmentWindow.setVisible(!equipmentWindow.isVisible());
                if (!getStage().getActors().contains(backpackWindow, true)) {
                    getStage().addActor(backpackWindow);
                    backpackWindow.setX(PlayerVisual.this.getX());
                    backpackWindow.setY(Gdx.graphics.getHeight() / 2f);
                }
                if (!getStage().getActors().contains(equipmentWindow, true)) {
                    getStage().addActor(equipmentWindow);
                    equipmentWindow.setX(PlayerVisual.this.getX() - equipmentWindow.getWidth());
                    equipmentWindow.setY(Gdx.graphics.getHeight() / 2f);
                }
                equipmentWindow.toFront();
                backpackWindow.toFront();
            }
        });
        backpackWindow.setVisible(false);
        equipmentWindow.setVisible(false);

        addActor(avatar);
        addActor(name);
        buttons.addActor(spells);
        buttons.addActor(attack);
        buttons.addActor(items);
        buttons.setWidth(buttons.getPrefWidth());
        buttons.setHeight(buttons.getPrefHeight());
        addActor(buttons);
        addActor(hpBar);
        addActor(stmBar);
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
    }
}
