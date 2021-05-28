package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.game.Game;

import java.util.Arrays;

public class LoadGameStage extends Stage {

    private static final LoadGameStage instance = new LoadGameStage();

    private List<SaveEntry> savesList = new List<>(UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);


    private LoadGameStage() {
        super();
        Table mainTable = new Table();
        mainTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));

        // title
        Label title = new Label("Load game", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        mainTable.add(title);
        mainTable.row().space(10f);

        // saves list
        ScrollPane scrollPane = new ScrollPane(savesList);
        savesList.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SaveEntry saveEntry = savesList.getOverItem();
                if (saveEntry != null && getTapCount() >= 2) {
                    load();
                }
            }
        });
        mainTable.add(scrollPane)
                .fillX()
                .height(Gdx.graphics.getHeight() / 2f);
        mainTable.row().space(10f);

        // back button
        Button backButton = new TextButton("<< Back", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Game.getGameVisual().backMenuStage();
            }
        });

        // load button
        Button loadButton = new TextButton("Load", UiData.DEFAULT_SKIN, UiData.YES_BUTTON_STYLE);
        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                load();
            }
        });

        // place buttons to ui
        HorizontalGroup buttons = new HorizontalGroup();
        buttons.addActor(backButton);
        buttons.addActor(loadButton);
        buttons.align(Align.center);
        buttons.space(10f);
        mainTable.add(buttons);

        addActor(mainTable);
        mainTable.pack();
        mainTable.setY((Gdx.graphics.getHeight() - mainTable.getHeight()) / 2);
        mainTable.setX((Gdx.graphics.getWidth() - mainTable.getWidth()) / 2);
    }

    private void load() {
        SaveEntry saveEntry = savesList.getSelected();
        if (saveEntry == null) {
            return;
        }
        Runnable loadingRunnable = () -> {
            Game.getGameVisual().clear();
            Game game = Game.getGameLoader().load(saveEntry.file);
            Game.setCurrentGame(game);
            game.getGameMap().createStage();
            Game.getGameVisual().setUi(new UIStage(game.getCurrentPlayers().getBeings().stream()
                    .filter(being -> being instanceof Player)
                    .map(being -> new PlayerVisual((Player) being))
                    .toArray(PlayerVisual[]::new)));
            Game.getGameVisual().addStage(Game.getCurrentGame().getGameMap().getMapStage());
        };
        Runnable onEndLoading = () -> Game.getGameVisual().closeMenus();
        Game.getGameVisual().setMenuStage(LoadingStage.instance("Loading", loadingRunnable, onEndLoading));
    }

    public static LoadGameStage getInstance() {
        SaveEntry[] saves = Arrays.stream(Gdx.files.local("saves").list(".save"))
                .map(SaveEntry::new)
                .toArray(SaveEntry[]::new);
        Array<SaveEntry> savesArray = new Array<>(saves);
        savesArray.sort();
        savesArray.reverse();
        instance.savesList.setItems(savesArray);
        return instance;
    }
}
