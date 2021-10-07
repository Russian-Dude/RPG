package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.data.resources.Resource;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.items.ItemCountObserver;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.game.StaticReferencesHolder;
import ru.rdude.rpg.game.logic.holders.Slot;
import ru.rdude.rpg.game.utils.Functions;

import java.util.Objects;

@JsonIgnoreType
public class ItemVisual extends Group implements ItemCountObserver {

    private Item item;

    private Image itemImage;
    private Image border;
    private Label amount;
    private ItemInfoTooltip tooltip;

    public ItemVisual(Item item) {

        Game.getStaticReferencesHolders().itemsVisuals().put(item, this);
        this.item = item;
        item.subscribe(this);

        // item image
        Resource mainImageResource = item.getEntityData().getResources().getMainImage();
        if (mainImageResource != null) {
            itemImage = new Image(Game.getImageFactory().getRegion(mainImageResource.getGuid()));
        }
        else {
            itemImage = new Image(UiData.UNKNOWN_IMAGE_64X64);
        }

        // rarity border
        switch (item.getEntityData().getRarity()) {
            case BRONZE:
                border = new Image(UiData.ItemBorder.BRONZE);
                break;
            case SILVER:
                border = new Image(UiData.ItemBorder.SILVER);
                break;
            case GOLD:
                border = new Image(UiData.ItemBorder.GOLD);
                break;
            case QUEST:
                border = new Image(UiData.ItemBorder.QUEST);
        }

        // count
        String stringAmount = item.getAmount() > 1 ? String.valueOf(item.getAmount()) : "";
        amount = new Label(stringAmount, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        amount.setWidth(border.getWidth());
        amount.setAlignment(Align.bottomRight);

        // tooltip
        tooltip = new ItemInfoTooltip(item);

        addListener(tooltip);
        itemImage.setSize(border.getWidth(), border.getHeight());
        addActor(itemImage);
        addActor(border);
        addActor(amount);
        setSize(border.getWidth(), border.getHeight());
        setBounds(getX(), getY(), getWidth(), getHeight());

        Game.getCurrentGame().getItemsDragAndDrop().addItem(this);
    }

    public static ItemVisual ofItem(Item item) {
        ItemVisual res = Game.getStaticReferencesHolders().itemsVisuals().get(item);
        return res == null ? new ItemVisual(item) : res;
    }

    public Item getItem() {
        return item;
    }

    public Image getItemImage() {
        return itemImage;
    }

    @Override
    public void update(Item item, int oldAmount, int newAmount) {
        if (Objects.equals(this.item, item)) {
            if (newAmount > 0) {
                this.amount.setText(newAmount > 1 ? String.valueOf(newAmount) : "");
            } else {
                Slot.withEntity(item).setEntity(null);
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // use item
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)
                        && !Game.getGameVisual().isInGameMenuShown()
                        && !Game.getCurrentGame().getItemsDragAndDrop().isDragging()
                        && Functions.isMouseOver(this)) {

            Game.getGameVisual().getUi().getPlayerVisuals().stream()
                    .filter(PlayerVisual::isHit)
                    .max((a, b) -> {

                        final Actor backpackA = a.getBackpackWindow();
                        final Actor backpackB = b.getBackpackWindow();
                        final Actor equipmentA = a.getEquipmentWindow();
                        final Actor equipmentB = b.getEquipmentWindow();

                        final boolean hitBackpackA = Functions.isMouseOver(backpackA);
                        final boolean hitBackpackB = Functions.isMouseOver(backpackB);
                        final boolean hitEquipmentA = Functions.isMouseOver(equipmentA);
                        final boolean hitEquipmentB = Functions.isMouseOver(equipmentB);

                        final int indexBackpackA = hitBackpackA ? backpackA.getZIndex() : -1;
                        final int indexBackpackB = hitBackpackB ? backpackB.getZIndex() : -1;
                        final int indexEquipmentA = hitEquipmentA ? equipmentA.getZIndex() : -1;
                        final int indexEquipmentB = hitEquipmentB ? equipmentB.getZIndex() : -1;

                        return Integer.compare(
                                Math.max(indexBackpackA, indexEquipmentA),
                                Math.max(indexBackpackB, indexEquipmentB));

                    })
                    .filter(playerVisual -> (playerVisual.getPlayer().backpack().hasEntity(this.item)
                            || playerVisual.getPlayer().equipment().hasEntity(this.item))
                            && playerVisual.getPlayer().isReady())
                    .ifPresent(playerVisual -> Game.getItemUser().use(this.item, playerVisual.getPlayer()));
        }
    }
}
