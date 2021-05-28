package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ru.rdude.rpg.game.logic.game.Game;

public class ConfirmSaveStage extends Stage {

    private static final ConfirmSaveStage instance = new ConfirmSaveStage();

    private final Label title = new Label("Save \"" + " ".repeat(20) + "\" already exists. Overwrite?", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private String saveName = "";

    private ConfirmSaveStage() {
        super();
        Table mainTable = new Table();
        mainTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));

        VerticalGroup verticalGroup = new VerticalGroup();
        verticalGroup.addActor(title);
        HorizontalGroup horizontalGroup = new HorizontalGroup();
        Button noButton = new TextButton("No", UiData.DEFAULT_SKIN, UiData.NO_BUTTON_STYLE);
        noButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Game.getGameVisual().backMenuStage();
            }
        });
        Button yesButton = new TextButton("Yes", UiData.DEFAULT_SKIN, UiData.YES_BUTTON_STYLE);
        yesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Runnable savingRunnable = () -> Game.getGameSaver().save(Game.getCurrentGame(), saveName);
                Runnable endSaving = () -> Game.getGameVisual().closeMenus();
                Game.getGameVisual().setMenuStage(LoadingStage.instance("Saving", savingRunnable, endSaving));
            }
        });
        horizontalGroup.space(10f);
        horizontalGroup.addActor(noButton);
        horizontalGroup.addActor(yesButton);
        verticalGroup.space(10f);
        verticalGroup.addActor(horizontalGroup);
        mainTable.add(verticalGroup);

        addActor(mainTable);
        mainTable.pack();
        mainTable.setY((Gdx.graphics.getHeight() - mainTable.getHeight()) / 2);
        mainTable.setX((Gdx.graphics.getWidth() - mainTable.getWidth()) / 2);
    }

    public static ConfirmSaveStage instance(String saveName) {
        instance.saveName = saveName;
        instance.title.setText("Save \"" + saveName + "\" already exists. Overwrite?");
        return instance;
    }
}
