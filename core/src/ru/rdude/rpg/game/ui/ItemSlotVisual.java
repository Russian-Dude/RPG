package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.items.equip.*;
import ru.rdude.rpg.game.logic.holders.Slot;

public class ItemSlotVisual<T extends Item> extends Group {

    private Slot<T> slot;

    private Image background;
    private ItemVisual item;


    public ItemSlotVisual(Slot<T> slot) {
        this.slot = slot;
        background = new Image(UiData.DEFAULT_SKIN.getDrawable("Slot_" + getDrawableBackgroundName()));
        addActor(background);
        setSize(background.getWidth(), background.getHeight());
    }

    public void setItemVisual(ItemVisual itemVisual) {
        addActor(itemVisual);
    }

    public Slot<T> getSlot() {
        return slot;
    }

    private String getDrawableBackgroundName() {
        if (Armor.class.isAssignableFrom(slot.getRequiredClass()))
            return "Body";
        else if (Boots.class.isAssignableFrom(slot.getRequiredClass()))
            return "Boots";
        else if (Gloves.class.isAssignableFrom(slot.getRequiredClass()))
            return "Gloves";
        else if (Helmet.class.isAssignableFrom(slot.getRequiredClass()))
            return "Head";
        else if (Jewelry.class.isAssignableFrom(slot.getRequiredClass()))
            return "Ring";
        else if (Necklace.class.isAssignableFrom(slot.getRequiredClass()))
            return "Amulet";
        else if (Pants.class.isAssignableFrom(slot.getRequiredClass()))
            return "Legs";
        else if (Shield.class.isAssignableFrom(slot.getRequiredClass()))
            return "Shield";
        else if (Weapon.class.isAssignableFrom(slot.getRequiredClass()))
            return "Weapon";
        else
            return "Empty";
    }

}
