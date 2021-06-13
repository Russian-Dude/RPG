package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.items.ItemCountObserver;
import ru.rdude.rpg.game.logic.game.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ItemVisual extends Group implements ItemCountObserver {

    public static Map<Item, ItemVisual> items;

    private Item item;

    private Image itemImage;
    private Image border;
    private Label count;
    private ItemInfoTooltip tooltip;

    public ItemVisual(Item item) {

        if (items == null)
            items = new HashMap<>();
        items.put(item, this);

        this.item = item;
        itemImage = new Image(Game.getItemImageFactory().getRegion(item.getItemData().getResources().getMainImage().getGuid()));
        border = UiData.ItemBorder.BRONZE;
        count = new Label(String.valueOf(item.getAmount()), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        tooltip = new ItemInfoTooltip(item);
        addListener(tooltip);
        itemImage.setSize(border.getWidth(), border.getHeight());
        addActor(itemImage);
        addActor(border);
        addActor(count);
        setSize(border.getWidth(), border.getHeight());
        setBounds(getX(), getY(), getWidth(), getHeight());

        ItemDragAndDroper.addItem(this);
    }

    public Item getItem() {
        return item;
    }

    public Image getItemImage() {
        return itemImage;
    }

    @Override
    public void update(int amount, Item item) {
        if (Objects.equals(this.item, item)) {
            this.count.setText(amount);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT) && !Game.getItemsDragAndDrop().isDragging()) {
            //Game.getItemUser().use(this.item, );
            // TODO: 14.06.2021 find being who use an item
        }
    }
}
