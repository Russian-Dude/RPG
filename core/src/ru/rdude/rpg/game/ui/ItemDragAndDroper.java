package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import ru.rdude.rpg.game.logic.data.resources.Resource;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.items.holders.EquipmentSlot;
import ru.rdude.rpg.game.logic.entities.items.holders.ShopItemSlot;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.holders.Slot;

public class ItemDragAndDroper {

    private final DragAndDrop dragAndDrop = new DragAndDrop();
    private final Image currentDragImage = new Image(new SpriteDrawable(new Sprite()));

    public void addSlot(ItemSlotVisual slotVisual) {
        addTarget(slotVisual);
    }

    public boolean isDragging() {
        return dragAndDrop.isDragging();
    }

    public void addRemoverArea(ItemRemoverArea area) {
        addTarget(area);
    }

    public void addPlayerArea(PlayerVisual playerVisual) {
        addTarget(playerVisual);
    }

    public void addItem(ItemVisual itemVisual) {
        dragAndDrop.addSource(new DragAndDrop.Source(itemVisual) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent inputEvent, float v, float v1, int i) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                Resource mainImage = itemVisual
                        .getItem()
                        .getEntityData()
                        .getResources()
                        .getMainImage();
                TextureRegion region = mainImage != null ?
                        Game.getImageFactory().getRegion(mainImage.getGuid())
                        : UiData.UNKNOWN_IMAGE_64X64_REGION;
                ((SpriteDrawable) currentDragImage.getDrawable())
                        .getSprite()
                        .setRegion(region);
                currentDragImage.setWidth(itemVisual.getWidth());
                currentDragImage.setHeight(itemVisual.getHeight());
                dragAndDrop.setDragActorPosition(currentDragImage.getWidth() / 2, -currentDragImage.getHeight() / 2);
                payload.setDragActor(currentDragImage);
                payload.setObject(itemVisual.getItem());
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                super.dragStop(event, x, y, pointer, payload, target);
                if (target == null) {
                    return;
                }
                Actor targetActor = target.getActor();
                Item item = itemVisual.getItem();
                if (targetActor instanceof ItemSlotVisual) {
                    Slot<Item> slot = ((ItemSlotVisual) targetActor).getSlot();
                    tryToPutItemInSlot(item, slot);
                }
                else if (targetActor instanceof PlayerVisual) {
                    final Slot<Item> initialSlot = Slot.withEntity(item);
                    if (!(initialSlot instanceof ShopItemSlot) && ((PlayerVisual) targetActor).getPlayer().receive(item)) {
                        initialSlot.setEntity(null);
                    }
                }
                else if (targetActor instanceof ItemRemoverArea) {
                    final Object object = payload.getObject();
                    final Slot<Item> initialSlot = Slot.withEntity(item);
                    if (object instanceof Item && ("backpack".equals(initialSlot.getMarker()) || initialSlot instanceof EquipmentSlot)) {
                        Game.getGameVisual().setMenuStage(ThrowItemRequestStage
                                .instance(Slot.withEntity((Item) payload.getObject())));
                    }
                }
            }
        });
    }

    private void addTarget(Actor actor) {
        dragAndDrop.addTarget(new DragAndDrop.Target(actor) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float v, float v1, int i) {
                return true;
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float v, float v1, int i) {

            }
        });
    }

    private void tryToPutItemInSlot(Item item, Slot<Item> slot) {

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
            Slot<Item> initialSlot = Slot.withEntity(item);
            if (initialSlot instanceof ShopItemSlot && !(slot instanceof ShopItemSlot)) {
                Game.getCurrentGame().getGold().decrease((int) (initialSlot.getEntity().getAmount() * initialSlot.getEntity().price()));
            }
            else if (!(initialSlot instanceof ShopItemSlot) && slot instanceof ShopItemSlot) {
                Game.getCurrentGame().getGold().increase((int) ((initialSlot.getEntity().getAmount() * initialSlot.getEntity().price()) / 2));
            }
            initialSlot.setEntity(null);
            slot.setEntity(item);
            return;
        }

        // if slot contains different item
        if (!slot.getEntity().sameAs(item)) {
            Slot<Item> initialSlot = Slot.withEntity(item);
            if (initialSlot instanceof ShopItemSlot && !(slot instanceof ShopItemSlot)) {
                return;
            }
            else if (!(initialSlot instanceof ShopItemSlot) && slot instanceof ShopItemSlot) {
                return;
            }
            else if (initialSlot != null && initialSlot.isEntityMatchRequirements(slot.getEntity())) {
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
                Slot<Item> initialSlot = Slot.withEntity(item);
                int rest = slot.getEntity().increaseAmountAndReturnRest(item.getAmount());
                if (initialSlot instanceof ShopItemSlot && !(slot instanceof ShopItemSlot)) {
                    Game.getCurrentGame().getGold().decrease((int) ((initialSlot.getEntity().getAmount() - rest) * initialSlot.getEntity().price()));
                }
                else if (!(initialSlot instanceof ShopItemSlot) && slot instanceof ShopItemSlot) {
                    Game.getCurrentGame().getGold().increase((int) (((initialSlot.getEntity().getAmount() - rest) * initialSlot.getEntity().price()) / 2));
                }
                if (rest > 0) {
                    item.setAmount(rest);
                    return;
                } else {
                    Slot.withEntity(item).setEntity(null);
                }
            }
        }
    }
}
