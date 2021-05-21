package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ru.rdude.rpg.game.logic.data.io.GameMapFileLoader;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.mapVisual.MapTilesFactory;

import java.util.Arrays;

public class MapSelectionStage extends Stage {

    private static MapSelectionStage instance = new MapSelectionStage();

    private final Image mapImage = new Image(new SpriteDrawable(new Sprite(MapTilesFactory.getEmpty().getTextureRegion())));
    private List<MapSelectionElement> mapList = new List<>(UiData.DEFAULT_SKIN, "no_background_simple");

    public MapSelectionStage() {
        super(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        Table mainTable = new Table();
        mainTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));

        // title
        mainTable.add(new Label("Select map", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE))
                .pad(25f)
                .row();

        // map image
        float mapHeight = Gdx.graphics.getHeight() / 2f;
        mainTable.add(mapImage)
                .width(mapHeight * 2f)
                .height(mapHeight)
                .row();

        // map list
        mapList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                mapImage.setDrawable(mapList.getSelected().image.getDrawable());
            }
        });
        ScrollPane listScrollPane = new ScrollPane(mapList, UiData.DEFAULT_SKIN);
        listScrollPane.setVariableSizeKnobs(false);
        mainTable.add(listScrollPane)
                .height(mapHeight / 2f)
                .width(mapHeight * 2f)
                .row();

        // bottom buttons
        HorizontalGroup bottomButtons = new HorizontalGroup();
        Button backButton = new TextButton("<< Back", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Game.getGameVisual().backMenuStage();
            }
        });
        Button selectButton = new TextButton("Select", UiData.DEFAULT_SKIN, UiData.YES_BUTTON_STYLE);
        selectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Game.getCurrentGame().setGameMap(new Map(GameMapFileLoader.load(Gdx.files.internal("maps\\" + mapList.getSelected().text + ".map"))));
                Game.getGameVisual().setMenuStage(PlayersCreationStage.instance);
            }
        });
        bottomButtons.addActor(backButton);
        bottomButtons.addActor(selectButton);
        mainTable.add(bottomButtons);

        mainTable.pack();
        addActor(mainTable);

        mainTable.setY((Gdx.graphics.getHeight() - mainTable.getHeight()) / 2);
        mainTable.setX((Gdx.graphics.getWidth() - mainTable.getWidth()) / 2);
    }

    public static MapSelectionStage getInstance() {
        MapSelectionElement[] maps = Arrays.stream(Gdx.files.internal("maps").list(".map"))
                .map(GameMapFileLoader::loadAsSelectionElement)
                .toArray(MapSelectionElement[]::new);
        instance.mapList.setItems(maps);
        if (instance.mapList.getItems().notEmpty()) {
            instance.mapList.setSelected(instance.mapList.getItems().first());
        }
        return instance;
    }
}
