package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.items.holders.ItemSlotsHolder;
import ru.rdude.rpg.game.logic.holders.Slot;

public class BackpackWindow extends Window {

    private ItemSlotsHolder backpack;

    public BackpackWindow(Being being) {
        super(being.getName(), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        padTop(35);
        backpack = being.backpack();
        int i = 1;
        for (Slot<Item> slot : backpack.getSlots()) {
            add(new ItemSlotVisual(slot));
            if (i % 4 == 0)
                row();
            i++;
        }
        pack();
    }
}
