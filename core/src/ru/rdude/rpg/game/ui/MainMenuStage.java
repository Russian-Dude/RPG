package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ru.rdude.rpg.game.logic.game.Game;

public class MainMenuStage extends Stage {

    public static MainMenuStage instance = new MainMenuStage();

    public MainMenuStage() {
        super(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Table mainTable = new Table();
        mainTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));

        // title
        mainTable.add(new Label("RPG", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE))
                .padTop(10f)
                .padBottom(20f)
                .row();

        // new game button
        Button newGameButton = new TextButton("New game", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Game.getGameVisual().setMenuStage(MapSelectionStage.getInstance());
            }
        });
        mainTable.add(newGameButton);
        mainTable.row().pad(25f);

        // load game button
        Button loadGameButton = new TextButton("Load game", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        loadGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: 18.05.2021 load game
            }
        });
        mainTable.add(loadGameButton);
        mainTable.row();

        // map generator button
        Button mapGeneratorButton = new TextButton("Map generator", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        mapGeneratorButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Game.getGameVisual().setMenuStage(MapGeneratorStage.instance);
            }
        });
        mainTable.add(mapGeneratorButton);
        mainTable.row();

        // settings
        Button settingsButton = new TextButton("Settings", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Game.getGameVisual().setMenuStage(SettingsStage.getInstance());
            }
        });
        mainTable.add(settingsButton).pad(25f);
        mainTable.row();

        // exit game button
        Button exitButton = new TextButton("Exit game", UiData.DEFAULT_SKIN, UiData.NO_BUTTON_STYLE);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();

            }
        });
        mainTable.add(exitButton);
        mainTable.row();

        mainTable.pack();
        addActor(mainTable);

        mainTable.setY((Gdx.graphics.getHeight() - mainTable.getHeight()) / 2);
        mainTable.setX((Gdx.graphics.getWidth() - mainTable.getWidth()) / 2);
    }
}
