package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.items.holders.ItemSlotsHolder;
import ru.rdude.rpg.game.logic.holders.SimpleSlot;
import ru.rdude.rpg.game.logic.holders.Slot;

public class ItemSlotsHolderVisual extends Table {

    public ItemSlotsHolderVisual(ItemSlotsHolder slotsHolder) {
        this(slotsHolder, 10);
    }

    public ItemSlotsHolderVisual(ItemSlotsHolder slotsHolder, int xAmount) {
        super(UiData.DEFAULT_SKIN);
        int i = 1;
        for (Slot<Item> slot : slotsHolder.getSlots()) {
            add(new ItemSlotVisual(slot));
            if (i % xAmount == 0)
                row();
            i++;
        }
        while (i % xAmount != 0) {
            add(new ItemSlotVisual(new SimpleSlot<>(null)));
            i++;
        }
        pack();
    }
}
