package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ru.rdude.rpg.game.mapVisual.VisualConstants;

public class MorePlayerBuffsWindow extends Window {

    private HorizontalGroup icons = new HorizontalGroup();

    public MorePlayerBuffsWindow() {
        super("More buffs" + " ".repeat(10), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        getTitleTable().padTop(20);
        // close button
        Button closeButton = new TextButton("X", UiData.DEFAULT_SKIN, UiData.NO_SQUARE_BUTTON_STYLE);
        getTitleTable().add(closeButton).padTop(25);
        getTitleTable().pack();
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MorePlayerBuffsWindow.this.setVisible(false);
            }
        });

        icons.space(15);
        add(icons).padTop(VisualConstants.BUFF_ICON_SIZE + 20f);
        pack();
    }

    public void addIcon(BuffIcon icon) {
        icons.addActor(icon);
        pack();
    }

    public void removeIcon(BuffIcon icon) {
        icons.removeActor(icon);
        pack();
    }

    public boolean isEmpty() {
        return !icons.hasChildren();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public BuffIcon getFirst() {
        return (BuffIcon) icons.getChild(0);
    }

    public int iconsAmount() {
        return icons.getChildren().size;
    }
}
