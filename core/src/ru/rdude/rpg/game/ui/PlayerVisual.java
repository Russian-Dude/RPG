package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.data.PlayerData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.data.resources.PlayerResources;
import ru.rdude.rpg.game.logic.entities.beings.*;
import ru.rdude.rpg.game.logic.enums.Target;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.GameStateObserver;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.mapVisual.VisualConstants;
import ru.rdude.rpg.game.ui.colors.EyesColor;
import ru.rdude.rpg.game.ui.colors.HairColor;
import ru.rdude.rpg.game.ui.colors.SkinColor;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.visual.VisualBeing;

import static ru.rdude.rpg.game.ui.UiData.BIG_TEXT_STYLE;
import static ru.rdude.rpg.game.ui.UiData.DEFAULT_SKIN;

@JsonIgnoreType
public class PlayerVisual extends VerticalGroup implements GameStateObserver, PlayerReadyObserver, VisualBeing<Player> {

    private final Player player;

    private final BackpackWindow backpackWindow;
    private final EquipmentWindow equipmentWindow;

    private final PlayerInfoWindow infoWindow;

    private final SkillSelector skillSelector;

    private final PlayerAvatar avatar;
    private final HpBar hpBar;
    private final StmBar stmBar;
    private final CastBar castBar;
    private final Button attack;
    private final Button spells;
    private final Button items;
    private final HorizontalGroup buttons;
    private final Label nameLabel;
    private final BeingBuffsIcons buffsIcons;

    public PlayerVisual(Player player) {
        this(player, createAvatarFromData(player.getEntityData()));
    }

    public PlayerVisual(Player player, PlayerAvatar avatar) {
        player.subscribe(this);
        Game.getCurrentGame().getGameStateHolder().subscribe(this);
        Game.getCurrentGame().getItemsDragAndDrop().addPlayerArea(this);
        this.player = player;
        this.avatar = avatar;
        hpBar = new HpBar(player);
        stmBar = new StmBar(player);
        castBar = new CastBar(player);
        buttons = new HorizontalGroup();
        attack = new Button(DEFAULT_SKIN, "attack");
        attack.setVisible(Game.getCurrentGame().getCurrentGameState() instanceof Battle && getBeing().isReady());
        items = new Button(DEFAULT_SKIN, "items");
        spells = new Button(DEFAULT_SKIN, "spells");
        spells.setVisible(getBeing().isReady());
        nameLabel = new Label(player.getName(), DEFAULT_SKIN, BIG_TEXT_STYLE);
        nameLabel.setAlignment(Align.center);
        buffsIcons = new BeingBuffsIcons(this);

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

        // attack
        attack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                final SkillData skillData = SkillData.getSkillByGuid(player.getCurrentClass()
                        .getClassData()
                        .getDefaultAttack(player.getAttackType()));
                final Target mainTarget = skillData.getMainTarget();
                if (mainTarget != Target.ALLY
                        && mainTarget != Target.ENEMY
                        && mainTarget != Target.ANY
                        && mainTarget != Target.ANY_OTHER) {
                    player.setReady(false);
                }
                Game.getSkillUser().use(skillData, player, skillData.getMainTarget());
            }
        });

        // stats and targeting
        infoWindow = new PlayerInfoWindow(player);
        avatar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Game.getGameVisual().getSkillTargeter().isTargeting()) {
                    Game.getGameVisual().getSkillTargeter().target(player);
                }
                else {
                    infoWindow.setVisible(!infoWindow.isVisible());
                    if (!getStage().getActors().contains(infoWindow, true)) {
                        getStage().addActor(infoWindow);
                        infoWindow.setX(PlayerVisual.this.getX());
                        infoWindow.setY(Gdx.graphics.getHeight() / 2f);
                        infoWindow.setVisible(true);
                    }
                    infoWindow.toFront();
                }
            }
        });

        // skills
        Table skillSelectorTable = new Table() {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (this.isVisible() && (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT))) {
                    this.setVisible(false);
                }
            }
        };
        this.skillSelector = new SkillSelector(player, skillSelectorTable);
        ScrollPane skillSelectorScrollPane = new ScrollPane(skillSelector);
        skillSelectorScrollPane.setVariableSizeKnobs(false);
        skillSelectorTable.add(skillSelectorScrollPane)
                .width(220f)
                .center()
                .maxHeight(Gdx.graphics.getHeight() / 2f);
        skillSelectorTable.pack();
        spells.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (player.getAvailableSkills().isEmpty()) {
                    return;
                }
                if (!getStage().getActors().contains(skillSelectorTable, true)) {
                    getStage().addActor(skillSelectorTable);
                    skillSelectorTable.setVisible(false);
                }
                skillSelector.getSelection().clear();
                skillSelectorTable.setVisible(!skillSelectorTable.isVisible());
                skillSelectorTable.setHeight(skillSelectorTable.getPrefHeight());
                skillSelectorTable.setX(PlayerVisual.this.getX() + 50f);
                skillSelectorTable.setY(150f);
                skillSelectorTable.toFront();
            }
        });
        // scroll without click
        skillSelectorScrollPane.addListener(new InputListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStage().setScrollFocus(skillSelectorScrollPane);
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                getStage().setScrollFocus(null);
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
        withoutNameGroup.addActor(castBar);
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
        setHeight(getPrefHeight());
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
                        || Functions.isMouseOver(infoWindow);
    }

    @Override
    public Vector2 getCenter() {
        float x = avatar.getWidth() / 2f;
        float y = avatar.getHeight() / 2f;
        return avatar.localToStageCoordinates(new Vector2(x, y));
    }

    @Override
    public HpBar getHpBar() {
        return hpBar;
    }

    @Override
    public StmBar getStmBar() {
        return stmBar;
    }

    @Override
    public CastBar getCastBar() {
        return castBar;
    }

    @Override
    public Player getBeing() {
        return player;
    }

    @Override
    public void update(GameStateBase oldValue, GameStateBase newValue) {
        attack.setVisible(newValue instanceof Battle && getBeing().isReady());
        if (newValue instanceof Map) {
            spells.setVisible(true);
        }
    }

    @Override
    public void update(Player player, boolean ready) {
        spells.setVisible(ready);
        attack.setVisible(ready);
    }
}
