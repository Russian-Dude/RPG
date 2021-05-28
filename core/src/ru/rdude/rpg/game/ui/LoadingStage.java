package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class LoadingStage extends Stage {

    private static final LoadingStage instance = new LoadingStage();

    private Label label = new Label("Loading", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);

    private Runnable runnable;
    private Runnable onEndLoading;
    private boolean loadingEnded = false;
    private boolean startLoading = false;

    private LoadingStage() {
        super();
        Table mainTable = new Table();
        mainTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));

        mainTable.add(label).align(Align.center);

        addActor(mainTable);
        mainTable.pack();
        mainTable.setY((Gdx.graphics.getHeight() - mainTable.getHeight()) / 2);
        mainTable.setX((Gdx.graphics.getWidth() - mainTable.getWidth()) / 2);
    }

    @Override
    public void draw() {
        super.draw();
        if (startLoading) {
            startLoading = false;
            Gdx.app.postRunnable(runnable);
            loadingEnded = true;
        }
        if (loadingEnded) {
            loadingEnded = false;
            Gdx.app.postRunnable(onEndLoading);
        }
    }


    public static LoadingStage instance(String text, Runnable startLoading, Runnable endLoading) {
        instance.label.setText(text);
        instance.runnable = startLoading;
        instance.onEndLoading = endLoading;
        instance.startLoading = true;
        return instance;
    }
}
