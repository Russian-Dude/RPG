package ru.rdude.rpg.game.logic.entities.items.equip;

import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.stats.secondary.Hp;

public class EquippableItem extends Item {

    protected EquippableItem(ItemData itemData) {
        super(itemData);
    }

    @Override
    public void setAmount(int value) {
        throw new UnsupportedOperationException("equippable items can be only at amount of 1");
    }

    @Override
    public EquippableItem copy() {
        EquippableItem copy = new EquippableItem(this.itemData);
        Hp copyHp = new Hp(this.durability.value());
        copyHp.set(this.durability.value());
        copyHp.setMaxValue(this.durability.maxValue());
        copyHp.recovery().set(this.durability.recoveryValue());
        copy.durability = copyHp;
        copy.elements = this.elements.copy();
        return copy;
    }

}
