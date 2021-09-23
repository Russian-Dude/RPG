package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.game.Game;

public class DefeatStage extends Stage implements NonClosableMenuStage {

    private static DefeatStage instance = new DefeatStage();

    public DefeatStage() {
        super();
        Table mainTable = new Table(UiData.DEFAULT_SKIN);
        mainTable.background(UiData.SEMI_TRANSPARENT_BACKGROUND);

        // load button
        Button loadButton = new TextButton("Load game", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Game.getGameVisual().setMenuStage(LoadGameStage.getInstance());
            }
        });

        // to main menu button
        Button toMainMenuButton = new TextButton("Main menu", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        toMainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Game.getGameStateSwitcher().switchToMainMenu();
            }
        });

        Label defeatLabel = new Label("D  E  F  E  A  T", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        defeatLabel.addAction(Actions.color(Color.RED));
        defeatLabel.setAlignment(Align.center);
        mainTable.add(defeatLabel).space(10f).fillX().row();
        mainTable.add(loadButton).space(10f).fillX().row();
        mainTable.add(toMainMenuButton).space(10f).fillX().row();
        mainTable.pack();
        addActor(mainTable);

        mainTable.setY((Gdx.graphics.getHeight() - mainTable.getHeight()) / 2);
        mainTable.setX((Gdx.graphics.getWidth() - mainTable.getWidth()) / 2);
    }

    public static DefeatStage getInstance() {
        return instance;
    }
}
