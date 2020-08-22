package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.items.holders.ItemSlotsHolder;
import ru.rdude.rpg.game.logic.holders.Slot;

import java.util.ArrayList;
import java.util.List;

public class BackpackWindow extends Window {

    private ItemSlotsHolder<Item> backpack;

    public BackpackWindow(Being being) {
        super(being.getName(), UiData.DEFAULT_SKIN);
        padTop(35);
        backpack = being.backpack();
        int i = 1;
        for (Slot<Item> slot : backpack.getSlots()) {
            add(new ItemSlotVisual<>(slot));
            if (i % 4 == 0)
                row();
            i++;
        }
        pack();
    }
}
