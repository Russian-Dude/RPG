package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.entities.items.Item;

public class ItemVisual extends Group {

    private Item item;

    private Image itemImage;
    private Image border;
    private Label count;
    private ItemInfoTooltip tooltip;

    public ItemVisual(Item item) {
        this.item = item;
        itemImage = UiData.getItemImage("simple_sword");
        border = UiData.ItemBorder.BRONZE;
        count = new Label("", UiData.DEFAULT_SKIN);
        tooltip = new ItemInfoTooltip(item);
        addListener(tooltip);
        itemImage.setSize(border.getWidth(), border.getHeight());
        addActor(itemImage);
        addActor(border);
        addActor(count);
        setSize(border.getWidth(), border.getHeight());
        setBounds(getX(), getY(), getWidth(), getHeight());
    }

}
