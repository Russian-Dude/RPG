package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.enums.ItemMainType;
import ru.rdude.rpg.game.logic.enums.ItemType;
import ru.rdude.rpg.game.logic.holders.Slot;
import ru.rdude.rpg.game.logic.holders.SlotObserver;

public class ItemSlotVisual<T extends Item> extends Group implements SlotObserver {

    private Slot<T> slot;

    private Image background;
    private ItemVisual item;


    public ItemSlotVisual(Slot<T> slot) {
        this.slot = slot;
        slot.subscribe(this);
        background = new Image(UiData.DEFAULT_SKIN.getDrawable("Slot_" + getDrawableBackgroundName()));
        addActor(background);
        setSize(background.getWidth(), background.getHeight());
    }

    public void setItemVisual(ItemVisual itemVisual) {
        this.item = itemVisual;
        addActor(itemVisual);
    }

    public Slot<T> getSlot() {
        return slot;
    }

    private String getDrawableBackgroundName() {
        if (slot.getMarker() == ItemType.ARMOR)
            return "Body";
        else if (slot.getMarker() == ItemType.BOOTS)
            return "Boots";
        else if (slot.getMarker() == ItemType.GLOVES)
            return "Gloves";
        else if (slot.getMarker() == ItemType.HELMET)
            return "Head";
        else if (slot.getMarker() == ItemType.JEWELRY)
            return "Ring";
        else if (slot.getMarker() == ItemType.NECKLACE)
            return "Amulet";
        else if (slot.getMarker() == ItemType.PANTS)
            return "Legs";
        else if (slot.getMarker() == ItemType.SHIELD)
            return "Shield";
        else if (slot.getMarker() == ItemMainType.WEAPON)
            return "Weapon";
        else
            return "Empty";
    }

    @Override
    public void update(Slot<?> slot, Entity entity) {
        if (this.slot == slot && entity instanceof Item) {
            setItemVisual(ItemVisual.items.get(entity));
        }
    }
}
