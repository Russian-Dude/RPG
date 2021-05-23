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
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.data.PlayerData;
import ru.rdude.rpg.game.logic.data.resources.PlayerResources;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.GameStateObserver;
import ru.rdude.rpg.game.ui.colors.EyesColor;
import ru.rdude.rpg.game.ui.colors.HairColor;
import ru.rdude.rpg.game.ui.colors.SkinColor;

import java.util.ArrayList;
import java.util.List;

import static ru.rdude.rpg.game.ui.UiData.DEFAULT_SKIN;

public class PlayerVisual extends VerticalGroup implements GameStateObserver {

    private final static List<PlayerVisual> playerVisualList = new ArrayList<>();

    private final Player player;

    private final BackpackWindow backpackWindow;
    private final EquipmentWindow equipmentWindow;

    private final StatsWindow statsWindow;

    private final PlayerAvatar avatar;
    private final HpBar hpBar;
    private final StmBar stmBar;
    private final Button attack;
    private final Button spells;
    private final Button items;
    private final HorizontalGroup buttons;
    private final Label nameLabel;

    public PlayerVisual(Player player) {
        this(player, createAvatarFromData(((PlayerData) player.getBeingData())));
    }

    public PlayerVisual(Player player, PlayerAvatar avatar) {
        Game.getCurrentGame().getGameStateHolder().subscribe(this);
        playerVisualList.add(this);
        this.player = player;
        this.avatar = avatar;
        hpBar = new HpBar(player);
        stmBar = new StmBar(player);
        buttons = new HorizontalGroup();
        attack = new Button(DEFAULT_SKIN, "attack");
        items = new Button(DEFAULT_SKIN, "items");
        spells = new Button(DEFAULT_SKIN, "spells");
        nameLabel = new Label(player.getName(), DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);

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
        addActor(nameLabel);
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
    }

    private static PlayerAvatar createAvatarFromData(PlayerData playerData) {
        PlayerAvatar avatar = new PlayerAvatar();
        avatar.setName(playerData.getName());
        PlayerResources resources = (PlayerResources) playerData.getResources();
        AvatarCreator creator = Game.getAvatarCreator();
        avatar.setFace(creator.faces().get((int) resources.getFaceIndex()));
        avatar.setCloth(creator.clothes().get((int) resources.getClothIndex()));
        avatar.setMouth(creator.mouths().get((int) resources.getMouthIndex()));
        avatar.setNose(creator.noses().get((int) resources.getNoseIndex()));
        avatar.setEyes(creator.eyes().get((int) resources.getEyesIndex()));
        avatar.setEyePupils(creator.eyePupils().get((int) resources.getEyePupilsIndex()));
        avatar.setEyeBrows(creator.eyeBrows().get((int) resources.getEyeBrowsIndex()));
        avatar.setBeard(creator.beards().get((int) resources.getBeardIndex()));
        avatar.setHair(creator.hairs().get((int) resources.getHairIndex()));
        avatar.setFaceColor(SkinColor.values()[(int) resources.getFaceColorIndex()].getColor());
        avatar.setEyesColor(EyesColor.values()[(int) resources.getEyesColorIndex()].getColor());
        avatar.setHairColor(HairColor.values()[(int) resources.getHairColorIndex()].getColor());
        return avatar;
    }

    @Override
    public void update(GameStateBase oldValue, GameStateBase newValue) {
        attack.setVisible(newValue instanceof Battle);
    }
}
