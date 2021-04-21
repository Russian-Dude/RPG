package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.GameStateObserver;

import java.util.ArrayList;
import java.util.List;

import static ru.rdude.rpg.game.ui.UiData.DEFAULT_SKIN;

public class PlayerVisual extends VerticalGroup implements GameStateObserver {

    private static List<PlayerVisual> playerVisualList = new ArrayList<>();

    private Player player;

    private BackpackWindow backpackWindow;
    private EquipmentWindow equipmentWindow;

    private StatsWindow statsWindow;

    private PlayerAvatar avatar;
    private HpBar hpBar;
    private StmBar stmBar;
    private Button attack;
    private Button spells;
    private Button items;
    private HorizontalGroup buttons;

    public PlayerVisual(Player player) {
        Game.getCurrentGame().getGameStateHolder().subscribe(this);
        playerVisualList.add(this);
        this.player = player;
        avatar = new PlayerAvatar();
        hpBar = new HpBar(player);
        stmBar = new StmBar(player);
        buttons = new HorizontalGroup();
        attack = new Button(DEFAULT_SKIN, "attack");
        items = new Button(DEFAULT_SKIN, "items");
        spells = new Button(DEFAULT_SKIN, "spells");

        // items
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

        // stats
        statsWindow = new StatsWindow(player);
        avatar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                statsWindow.setVisible(!statsWindow.isVisible());
                if (!getStage().getActors().contains(statsWindow, true)) {
                    getStage().addActor(statsWindow);
                    statsWindow.setX(PlayerVisual.this.getX());
                    statsWindow.setY(Gdx.graphics.getHeight() / 2f);
                    statsWindow.setVisible(true);
                }
                statsWindow.toFront();
            }
        });

        // add
        buttons.addActor(spells);
        buttons.addActor(attack);
        buttons.addActor(items);
        buttons.setWidth(buttons.getPrefWidth());
        buttons.setHeight(buttons.getPrefHeight());
        Group topGroup = new Group();
        avatar.setPosition(avatar.getWidth() * (-1) / 2f, 0);
        buttons.setPosition(buttons.getWidth() * (-1) / 2f, 0);
        topGroup.addActor(avatar);
        topGroup.addActor(buttons);
        addActor(topGroup);
        addActor(hpBar);
        addActor(stmBar);
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
    }

    @Override
    public void update(GameStateBase oldValue, GameStateBase newValue) {
        attack.setVisible(newValue instanceof Battle);
    }
}
