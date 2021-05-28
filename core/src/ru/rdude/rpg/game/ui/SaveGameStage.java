package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import ru.rdude.rpg.game.logic.game.Game;

import java.util.Arrays;

public class SaveGameStage extends Stage {

    private static SaveGameStage instance = new SaveGameStage();

    private List<SaveEntry> savesList = new List<>(UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private TextField textField = new TextField("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);

    public SaveGameStage() {
        super();
        Table mainTable = new Table();
        mainTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));

        // title
        Label title = new Label("Save game", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        mainTable.add(title);
        mainTable.row().space(10f);

        // new save name
        textField.setMaxLength(20);
        mainTable.add(textField).fillX();
        mainTable.row().space(10f);

        // saves list
        ScrollPane scrollPane = new ScrollPane(savesList);
        savesList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (savesList.getSelected() == null) {
                    return;
                }
                textField.setText(savesList.getSelected().name);
            }
        });
        savesList.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SaveEntry saveEntry = savesList.getOverItem();
                if (saveEntry != null && getTapCount() >= 2) {
                    save();
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

        // save button
        Button saveButton = new TextButton("Save", UiData.DEFAULT_SKIN, UiData.YES_BUTTON_STYLE);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                save();
            }
        });

        // place buttons to ui
        HorizontalGroup buttons = new HorizontalGroup();
        buttons.addActor(backButton);
        buttons.addActor(saveButton);
        buttons.align(Align.center);
        buttons.space(10f);
        mainTable.add(buttons);

        addActor(mainTable);
        mainTable.pack();
        mainTable.setY((Gdx.graphics.getHeight() - mainTable.getHeight()) / 2);
        mainTable.setX((Gdx.graphics.getWidth() - mainTable.getWidth()) / 2);
    }

    private void save() {
        String saveName = textField.getText();
        if (saveName.isBlank()) {
            return;
        }
        boolean overwrite = Arrays.stream(savesList.getItems().toArray(SaveEntry.class)).anyMatch(entry -> entry.name.equals(saveName));
        if (overwrite) {
            Game.getGameVisual().setMenuStage(ConfirmSaveStage.instance(saveName));
        }
        else {
            Runnable savingRunnable = () -> Game.getGameSaver().save(Game.getCurrentGame(), saveName);
            Runnable endSaving = () -> Game.getGameVisual().closeMenus();
            Game.getGameVisual().setMenuStage(LoadingStage.instance("Saving", savingRunnable, endSaving));
        }
    }

    public static SaveGameStage instance() {
        SaveEntry[] saves = Arrays.stream(Gdx.files.local("saves").list(".save"))
                .map(SaveEntry::new)
                .toArray(SaveEntry[]::new);
        Array<SaveEntry> savesArray = new Array<>(saves);
        savesArray.sort();
        savesArray.reverse();
        instance.savesList.setItems(savesArray);
        instance.textField.setText("");
        return instance;
    }

}
