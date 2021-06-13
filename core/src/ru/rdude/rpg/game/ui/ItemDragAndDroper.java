package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.holders.Slot;

public class ItemDragAndDroper {

    private static final DragAndDrop dragAndDrop = Game.getItemsDragAndDrop();
    private static final Image currentDragImage = new Image(new SpriteDrawable(new Sprite()));

    public static void addSlot(ItemSlotVisual slotVisual) {
        dragAndDrop.addTarget(new DragAndDrop.Target(slotVisual) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float v, float v1, int i) {
                return true;
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float v, float v1, int i) {

            }
        });
    }

    public static boolean isDragging() {
        return dragAndDrop.isDragging();
    }

    public static void addItem(ItemVisual itemVisual) {
        dragAndDrop.addSource(new DragAndDrop.Source(itemVisual) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent inputEvent, float v, float v1, int i) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                ((SpriteDrawable) currentDragImage.getDrawable())
                        .getSprite()
                        .setRegion(Game
                                .getItemImageFactory()
                                .getRegion(itemVisual
                                        .getItem()
                                        .getItemData()
                                        .getResources()
                                        .getMainImage()
                                        .getGuid()));
                currentDragImage.setWidth(itemVisual.getWidth());
                currentDragImage.setHeight(itemVisual.getHeight());
                dragAndDrop.setDragActorPosition(currentDragImage.getWidth() / 2, -currentDragImage.getHeight() / 2);
                payload.setDragActor(currentDragImage);
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                super.dragStop(event, x, y, pointer, payload, target);
                if (target == null) {
                    throwItemRequest(itemVisual.getItem());
                    return;
                }
                Actor targetActor = target.getActor();
                if (targetActor instanceof ItemSlotVisual) {
                    Slot<Item> slot = ((ItemSlotVisual) targetActor).getSlot();
                    Item item = itemVisual.getItem();
                    tryToPutItemInSlot(item, slot);
                }
            }
        });
    }

    private static void tryToPutItemInSlot(Item item, Slot<Item> slot) {

        // if putting item in the same slot
        if (Slot.withEntity(item) == slot) {
            return;
        }

        // if item does not meet requirements of slot
        if (!slot.isEntityMatchRequirements(item)) {
            return;
        }

        // if slot is empty
        if (slot.isEmpty()) {
            Slot.withEntity(item).setEntity(null);
            slot.setEntity(item);
            return;
        }

        // if slot contains different item
        if (!slot.getEntity().equals(item)) {
            Slot<Item> initialSlot = (Slot<Item>) Slot.withEntity(item);
            if (initialSlot != null && initialSlot.isEntityMatchRequirements(slot.getEntity())) {
                slot.swapEntities(initialSlot);
            } else {
                return;
            }
        }

        // if slot contains same item
        else {
            if (!item.isStackable()) {
                return;
            } else {
                int rest = slot.getEntity().increaseAmountAndReturnRest(item.getAmount());
                if (rest > 0) {
                    item.setAmount(rest);
                    return;
                } else {
                    Slot.withEntity(item).setEntity(null);
                }
            }
        }
    }

    private static void throwItemRequest(Item item) {

    }
}
