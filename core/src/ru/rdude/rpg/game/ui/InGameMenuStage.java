package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ru.rdude.rpg.game.logic.game.Game;

public class InGameMenuStage extends Stage {

    private static InGameMenuStage instance = new InGameMenuStage();

    public static InGameMenuStage getInstance() {
        return instance;
    }

    private InGameMenuStage() {
        super();
        Table mainTable = new Table();
        mainTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));

        // resume
        TextButton resumeButton = new TextButton("Resume", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Game.getGameVisual().backMenuStage();
            }
        });

        TextButton saveGameButton = new TextButton("Save", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        TextButton loadGameButton = new TextButton("Load", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);

        // settings
        TextButton settingsButton = new TextButton("Settings", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Game.getGameVisual().setMenuStage(SettingsStage.getInstance());
            }
        });

        // to main menu
        TextButton quitToMainMenuButton = new TextButton("To main menu", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        quitToMainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Game.getGameVisual().backMenuStage();
                Game.getGameVisual().setJustOpenedMainMenu(true);
                Game.getGameVisual().goToMainMenu();
            }
        });

        // exit
        TextButton exitGameButton = new TextButton("Exit game", UiData.DEFAULT_SKIN, UiData.NO_BUTTON_STYLE);
        exitGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        mainTable.add(resumeButton).space(10f).fillX().row();
        mainTable.add(saveGameButton).space(10f).fillX().row();
        mainTable.add(loadGameButton).space(10f).fillX().row();
        mainTable.add(settingsButton).space(10f).fillX().row();
        mainTable.add(quitToMainMenuButton).space(10f).fillX().row();
        mainTable.add(exitGameButton).space(10f).fillX().row();

        mainTable.pack();
        addActor(mainTable);

        mainTable.setY((Gdx.graphics.getHeight() - mainTable.getHeight()) / 2);
        mainTable.setX((Gdx.graphics.getWidth() - mainTable.getWidth()) / 2);
    }
}
