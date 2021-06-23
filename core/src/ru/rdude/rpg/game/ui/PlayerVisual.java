package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.data.PlayerData;
import ru.rdude.rpg.game.logic.data.resources.PlayerResources;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.GameStateObserver;
import ru.rdude.rpg.game.mapVisual.VisualConstants;
import ru.rdude.rpg.game.ui.colors.EyesColor;
import ru.rdude.rpg.game.ui.colors.HairColor;
import ru.rdude.rpg.game.ui.colors.SkinColor;
import ru.rdude.rpg.game.utils.Functions;

import static ru.rdude.rpg.game.ui.UiData.DEFAULT_SKIN;

@JsonIgnoreType
public class PlayerVisual extends VerticalGroup implements GameStateObserver {

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
    private final PlayerBuffsIcons buffsIcons;

    public PlayerVisual(Player player) {
        this(player, createAvatarFromData(((PlayerData) player.getEntityData())));
    }

    public PlayerVisual(Player player, PlayerAvatar avatar) {
        Game.getCurrentGame().getGameStateHolder().subscribe(this);
        Game.getCurrentGame().getItemsDragAndDrop().addPlayerArea(this);
        this.player = player;
        this.avatar = avatar;
        hpBar = new HpBar(player);
        stmBar = new StmBar(player);
        buttons = new HorizontalGroup();
        attack = new Button(DEFAULT_SKIN, "attack");
        attack.setVisible(Game.getCurrentGame().getCurrentGameState() instanceof Battle);
        items = new Button(DEFAULT_SKIN, "items");
        spells = new Button(DEFAULT_SKIN, "spells");
        nameLabel = new Label(player.getName(), DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        nameLabel.setAlignment(Align.center);
        buffsIcons = new PlayerBuffsIcons(this);

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
        Table table = new Table();
        VerticalGroup withoutNameGroup = new VerticalGroup();
        table.add(buffsIcons.leftSide())
                .space(4f)
                .width(VisualConstants.BUFF_ICON_SIZE)
                .fillY();
        Group topGroup = new Group();
        topGroup.setSize(avatar.getHeight(), avatar.getWidth());
        avatar.setPosition(0, 0);
        topGroup.addActor(avatar);
        topGroup.addActor(buttons);
        withoutNameGroup.addActor(topGroup);
        withoutNameGroup.addActor(hpBar);
        withoutNameGroup.addActor(stmBar);
        table.add(withoutNameGroup).space(4f);
        table.add(buffsIcons.rightSide())
                .width(VisualConstants.BUFF_ICON_SIZE)
                .fillY();
        table.pack();
        addActor(table);
        addActor(nameLabel);
        align(Align.center);
    }

    private static PlayerAvatar createAvatarFromData(PlayerData playerData) {
        PlayerAvatar avatar = new PlayerAvatar();
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

    public Player getPlayer() {
        return player;
    }

    public BackpackWindow getBackpackWindow() {
        return backpackWindow;
    }

    public EquipmentWindow getEquipmentWindow() {
        return equipmentWindow;
    }

    public boolean isHit() {
        return
                Functions.isMouseOver(this)
                        || Functions.isMouseOver(backpackWindow)
                        || Functions.isMouseOver(equipmentWindow)
                        || Functions.isMouseOver(avatar)
                        || Functions.isMouseOver(statsWindow);
    }

    @Override
    public void update(GameStateBase oldValue, GameStateBase newValue) {
        attack.setVisible(newValue instanceof Battle);
    }
}
