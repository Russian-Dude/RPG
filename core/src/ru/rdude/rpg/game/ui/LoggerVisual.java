package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.GameLogger;
import ru.rdude.rpg.game.logic.game.Game;

public class LoggerVisual extends Window {

    private Label text;
    private GameLogger gameLogger;
    boolean mouseStillOnScrollPane;

    public LoggerVisual() {
        super("", UiData.DEFAULT_SKIN, "simple_transparent");
        align(Align.topLeft);
        this.gameLogger = Game.getCurrentGame().getGameLogger();
        text = new Label("Game has been started!", UiData.DEFAULT_SKIN, "simple");
        text.setWrap(true);
        ScrollPane scrollPane = new ScrollPane(text);
        scrollPane.setScrollbarsVisible(true);
        scrollPane.setFlickScroll(false);
        add(scrollPane).width(490);
        gameLogger.addListener(log -> {
            text.setText(text.getText() + "\r\n" + log);
            scrollPane.scrollTo(0, 0, scrollPane.getWidth(), scrollPane.getHeight());
        });
        setSize(500, 250);
        text.setSize(490, 240);

        // scrollPane stops scrolling if window is clicked. So next code is needed
        mouseStillOnScrollPane = false;
        text.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mouseStillOnScrollPane = true;
            }
        });
        scrollPane.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mouseStillOnScrollPane = true;
            }
        });

        // scroll without clicking on scroll pane
        scrollPane.addListener(new InputListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStage().setScrollFocus(scrollPane);
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (!mouseStillOnScrollPane) {
                    getStage().setScrollFocus(null);
                }
                mouseStillOnScrollPane = false;
            }
        });
    }
}
