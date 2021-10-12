package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.items.holders.ItemSlotsHolder;
import ru.rdude.rpg.game.logic.holders.SimpleSlot;
import ru.rdude.rpg.game.logic.holders.Slot;
import ru.rdude.rpg.game.logic.holders.SlotsHolder;

public class ItemSlotsHolderVisual extends Table {

    public ItemSlotsHolderVisual(SlotsHolder<Item> slotsHolder) {
        this(slotsHolder, 10);
    }

    public ItemSlotsHolderVisual(SlotsHolder<Item> slotsHolder, int xAmount) {
        super(UiData.DEFAULT_SKIN);
        int i = 1;
        for (Slot<Item> slot : slotsHolder.getSlots()) {
            add(new ItemSlotVisual(slot));
            if (i % xAmount == 0)
                row();
            i++;
        }
        pack();
    }
}
