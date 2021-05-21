package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.ui.colors.EyesColor;
import ru.rdude.rpg.game.ui.colors.HairColor;
import ru.rdude.rpg.game.ui.colors.SkinColor;
import ru.rdude.rpg.game.utils.ui.SelectorWithArrows;

import java.util.Arrays;

public class PlayerCreationVisual extends Table {

    private float defaultAvatarWidth;
    private float defaultAvatarHeight;

    private VerticalGroup leftVerticalGroup;
    private TextField name;
    private PlayerAvatar avatar;
    private TextButton randomizeButton;

    private Player player = new Player();

    private SelectorWithArrows<TextureAtlas.AtlasRegion> faceSelector = new SelectorWithArrows<>(Game.getCurrentGame().getAvatarCreator().faces());
    private SelectorWithArrows<TextureAtlas.AtlasRegion> mouthSelector = new SelectorWithArrows<>(Game.getCurrentGame().getAvatarCreator().mouths());
    private SelectorWithArrows<TextureAtlas.AtlasRegion> noseSelector = new SelectorWithArrows<>(Game.getCurrentGame().getAvatarCreator().noses());
    private SelectorWithArrows<TextureAtlas.AtlasRegion> eyesSelector = new SelectorWithArrows<>(Game.getCurrentGame().getAvatarCreator().eyes());
    private SelectorWithArrows<TextureAtlas.AtlasRegion> eyeBrowsSelector = new SelectorWithArrows<>(Game.getCurrentGame().getAvatarCreator().eyeBrows());
    private SelectorWithArrows<TextureAtlas.AtlasRegion> beardSelector = new SelectorWithArrows<>(Game.getCurrentGame().getAvatarCreator().beards());
    private SelectorWithArrows<TextureAtlas.AtlasRegion> hairSelector = new SelectorWithArrows<>(Game.getCurrentGame().getAvatarCreator().hairs());

    private SelectorWithArrows<SkinColor> faceColorSelector = new SelectorWithArrows<>(Arrays.asList(SkinColor.values()));
    private SelectorWithArrows<HairColor> hairColorSelector = new SelectorWithArrows<>(Arrays.asList(HairColor.values()));
    private SelectorWithArrows<EyesColor> eyesColorSelector = new SelectorWithArrows<>(Arrays.asList(EyesColor.values()));

    public PlayerCreationVisual() {
        // add points to player
        player.stats().lvl().statPoints().increase(3);
        // background
        background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
        // create
        leftVerticalGroup = new VerticalGroup();
        leftVerticalGroup.space(15);
        name = new TextField("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        name.setMessageText("enter name here");
        name.setMaxLength("Default player name".length());
        avatar = new PlayerAvatar();
        randomizeButton = new TextButton("randomize", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        name.setAlignment(Align.center);

        // listeners
        // randomize button
        randomizeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                randomizeFace();
            }
        });
        // face
        faceSelector.addListener((region, index) -> {
            avatar.setFace(region);
            avatar.setCloth(Game.getCurrentGame().getAvatarCreator().clothes().get(index));
        });
        // mouth
        mouthSelector.addListener((region, index) -> avatar.setMouth(region));
        // nose
        noseSelector.addListener((region, index) -> avatar.setNose(region));
        // eyes
        eyesSelector.addListener((region, index) -> {
            avatar.setEyes(region);
            avatar.setEyePupils(Game.getCurrentGame().getAvatarCreator().eyePupils().get(index));
        });
        // eyeBrows
        eyeBrowsSelector.addListener((region, index) -> avatar.setEyeBrows(region));
        // beard
        beardSelector.addListener((region, index) -> avatar.setBeard(region));
        // hair
        hairSelector.addListener((region, index) -> avatar.setHair(region));
        // faceColor
        faceColorSelector.addListener((skinColor, index) -> avatar.setFaceColor(skinColor.getColor()));
        // hair color
        hairColorSelector.addListener((hairColor, index) -> avatar.setHairColor(hairColor.getColor()));
        // eyes color
        eyesColorSelector.addListener((eyesColor, index) -> avatar.setEyesColor(eyesColor.getColor()));

        // add
        //left vertical group
        Table faceCreationTable = new Table();
        leftVerticalGroup.addActor(avatar);
        Table nameTable = new Table(); // text area size can be changed only inside cell. So create this table
        nameTable.add(name).width(avatar.getWidth() * 2);
        nameTable.pack();
        leftVerticalGroup.addActor(nameTable);
        leftVerticalGroup.addActor(randomizeButton);
        // face types
        faceCreationTable.add(new Label("Face", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        faceCreationTable.add(new Label("Mouth", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        faceCreationTable.row();
        faceCreationTable.add(faceSelector).padRight(15);
        faceCreationTable.add(mouthSelector);
        faceCreationTable.row();
        faceCreationTable.add(new Label("Nose", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        faceCreationTable.add(new Label("Eyes", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        faceCreationTable.row();
        faceCreationTable.add(noseSelector).padRight(15);
        faceCreationTable.add(eyesSelector);
        faceCreationTable.row();
        faceCreationTable.add(new Label("Eyebrows", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        faceCreationTable.add(new Label("Beard", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        faceCreationTable.row();
        faceCreationTable.add(eyeBrowsSelector).padRight(15);
        faceCreationTable.add(beardSelector);
        faceCreationTable.row();
        faceCreationTable.add(new Label("Hair", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        faceCreationTable.add(new Label("Hair color", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        faceCreationTable.row();
        faceCreationTable.add(hairSelector).padRight(15);
        faceCreationTable.add(hairColorSelector);
        faceCreationTable.row();
        faceCreationTable.add(new Label("Face color", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        faceCreationTable.add(new Label("Eyes color", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        faceCreationTable.row();
        faceCreationTable.add(faceColorSelector).padRight(15);
        faceCreationTable.add(eyesColorSelector);
        faceCreationTable.pack();
        leftVerticalGroup.addActor(faceCreationTable);

        // central table with stats
        Table centralVerticalTable = new Table();
        // stats
        centralVerticalTable.align(Align.topRight);
        centralVerticalTable.add(new StatVisual(player, player.stats().lvl().statPoints(), false)).align(Align.right);
        centralVerticalTable.row().padTop(5);
        centralVerticalTable.add(new StatVisual(player, player.stats().agi(), true, false, true)).align(Align.right);
        centralVerticalTable.row().padTop(5);
        centralVerticalTable.add(new StatVisual(player, player.stats().dex(), true, false, true)).align(Align.right);
        centralVerticalTable.row().padTop(5);
        centralVerticalTable.add(new StatVisual(player, player.stats().intel(), true, false, true)).align(Align.right);
        centralVerticalTable.row().padTop(5);
        centralVerticalTable.add(new StatVisual(player, player.stats().luck(), true, false, true)).align(Align.right);
        centralVerticalTable.row().padTop(5);
        centralVerticalTable.add(new StatVisual(player, player.stats().str(), true, false, true)).align(Align.right);
        centralVerticalTable.row().padTop(5);
        centralVerticalTable.add(new StatVisual(player, player.stats().vit(), true, false, true)).align(Align.right);


        randomizeFace();
        defaultAvatarWidth = avatar.getWidth();
        defaultAvatarHeight = avatar.getHeight();
        avatar.setSize(avatar.getWidth() * 2, avatar.getHeight() * 2);

        add(leftVerticalGroup).padRight(30);
        add(centralVerticalTable).align(Align.top).padTop(avatar.getHeight() * 0.1f);
        pack();
    }

    private void randomizeFace() {
        faceSelector.selectRandom();
        mouthSelector.selectRandom();
        noseSelector.selectRandom();
        eyesSelector.selectRandom();
        eyeBrowsSelector.selectRandom();
        beardSelector.selectRandom();
        hairSelector.selectRandom();
        eyesColorSelector.selectRandom();
        hairColorSelector.selectRandom();
        faceColorSelector.selectRandom();
    }

    public void defaultAvatarSize() {
        avatar.setSize(defaultAvatarWidth, defaultAvatarHeight);
    }

    public PlayerAvatar getAvatar() {
        return avatar;
    }

    public Player getPlayer() {
        return player;
    }

    public TextField getNameTextField() {
        return name;
    }
}
