package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.settings.GameSettings;

public class SettingsStage extends Stage {

    private static SettingsStage instance = new SettingsStage();

    public SettingsStage() {
        super();
        Table mainTable = new Table();
        mainTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));

        // title
        Label title = new Label("Settings", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        mainTable.add(title).space(10f).row();

        Table settingsTable = new Table();

        // camera follow players
        Label cameraFollowPlayersLabel = new Label("Camera follow players", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        settingsTable.add(cameraFollowPlayersLabel).space(10f);
        CheckBox cameraFollowPlayers = new CheckBox("", UiData.DEFAULT_SKIN, UiData.RED_GREEN_CHECKBOX);
        cameraFollowPlayers.setChecked(GameSettings.isCameraFollowPlayers());
        cameraFollowPlayers.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                GameSettings.setCameraFollowPlayers(cameraFollowPlayers.isChecked());
            }
        });
        settingsTable.add(cameraFollowPlayers).space(15f).row();

        // players speed
        Label playersSpeedLabel = new Label("Players speed", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        settingsTable.add(playersSpeedLabel).space(10f);
        SelectBox<GameSettings.MovingSpeed> playersSpeed = new SelectBox<>(UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        playersSpeed.setItems(GameSettings.MovingSpeed.values());
        playersSpeed.setSelected(GameSettings.getPlayersMovingSpeedOnMap());
        playersSpeed.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                GameSettings.setPlayersMovingSpeedOnMap(playersSpeed.getSelected());
            }
        });
        settingsTable.add(playersSpeed).row();

        mainTable.add(settingsTable).space(10f).row();


        // back button
        TextButton backButton = new TextButton("<< Back", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Game.getGameVisual().backMenuStage();
            }
        });
        mainTable.add(backButton).space(10f).row();

        addActor(mainTable);
        mainTable.pack();
        mainTable.setY((Gdx.graphics.getHeight() - mainTable.getHeight()) / 2);
        mainTable.setX((Gdx.graphics.getWidth() - mainTable.getWidth()) / 2);
    }

    public static SettingsStage getInstance() {
        return instance;
    }
}
