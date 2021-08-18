package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ru.rdude.rpg.game.commands.CommandsInput;
import ru.rdude.rpg.game.logic.game.Game;

public class CommandsInputVisual extends Table {

    private final CommandsInput commandsInput = Game.getCommandsInput();
    TextField textField = new TextField("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);

    public CommandsInputVisual() {
        super(UiData.DEFAULT_SKIN);
        background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
        add(new Label("Enter command", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE))
                .colspan(2)
                .fillX()
                .center()
                .padBottom(25f)
                .row();
        add(textField)
                .colspan(2)
                .fillX()
                .padBottom(25f)
                .row();
        TextButton ok = new TextButton("Ok", UiData.DEFAULT_SKIN, UiData.YES_BUTTON_STYLE);
        TextButton close = new TextButton("Close", UiData.DEFAULT_SKIN, UiData.NO_BUTTON_STYLE);
        ok.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                commandsInput.input(textField.getText());
                CommandsInputVisual.this.setVisible(false);
            }
        });
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                CommandsInputVisual.this.setVisible(false);
            }
        });
        add(ok);
        add(close);
        pack();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        textField.setText("");
        if (visible) {
            final Stage stage = textField.getStage();
            if (stage != null) {
                stage.setKeyboardFocus(textField);
            }
        }
    }
}
