package ru.rdude.rpg.game.logic.entities.items.simpleItems;

import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.stats.secondary.Hp;

public class SimpleItem extends Item {

    public SimpleItem(ItemData itemData, int amount) {
        super(itemData, amount);
    }

    public SimpleItem(ItemData itemData) {
        super(itemData);
    }


    @Override
    public SimpleItem copy() {
        SimpleItem copy = new SimpleItem(this.itemData, this.amount);
        Hp copyHp = new Hp(this.durability.value());
        copyHp.set(this.durability.value());
        copyHp.setMaxValue(this.durability.maxValue());
        copyHp.recovery().set(this.durability.recoveryValue());
        copy.durability = copyHp;
        copy.elements = this.elements.copy();
        return copy;
    }

}
