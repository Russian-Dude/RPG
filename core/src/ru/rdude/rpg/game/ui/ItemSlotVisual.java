package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.enums.ItemMainType;
import ru.rdude.rpg.game.logic.enums.ItemType;
import ru.rdude.rpg.game.logic.holders.Slot;
import ru.rdude.rpg.game.logic.holders.SlotObserver;

public class ItemSlotVisual extends Group implements SlotObserver {

    private Slot<Item> slot;

    private Image background;
    private ItemVisual item;


    public ItemSlotVisual(Slot<Item> slot) {
        this.slot = slot;
        slot.subscribe(this);
        background = new Image(UiData.DEFAULT_SKIN.getDrawable("Slot_" + getDrawableBackgroundName()));
        addActor(background);
        setSize(background.getWidth(), background.getHeight());
        ItemDragAndDroper.addSlot(this);

        Item inside = slot.getEntity();
        if (inside != null) {
            ItemVisual itemVisual = ItemVisual.items.get(inside);
            setItemVisual(itemVisual != null ? itemVisual : new ItemVisual(inside));
        }
    }

    public void setItemVisual(ItemVisual itemVisual) {
        this.item = itemVisual;
        addActor(itemVisual);
    }

    public Slot<Item> getSlot() {
        return slot;
    }

    private String getDrawableBackgroundName() {
        if (slot.getMarker() == null) {
            return "Empty";
        }
        if (slot.getMarker().equals(ItemType.ARMOR.name()))
            return "Body";
        else if (slot.getMarker().equals(ItemType.BOOTS.name()))
            return "Boots";
        else if (slot.getMarker().equals(ItemType.GLOVES.name()))
            return "Gloves";
        else if (slot.getMarker().equals(ItemType.HELMET.name()))
            return "Head";
        else if (slot.getMarker().equals(ItemType.JEWELRY.name()))
            return "Ring";
        else if (slot.getMarker().equals(ItemType.NECKLACE.name()))
            return "Amulet";
        else if (slot.getMarker().equals(ItemType.PANTS.name()))
            return "Legs";
        else if (slot.getMarker().equals(ItemType.SHIELD.name()))
            return "Shield";
        else if (slot.getMarker().equals(ItemMainType.WEAPON.name()))
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
