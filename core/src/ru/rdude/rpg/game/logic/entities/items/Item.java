package ru.rdude.rpg.game.logic.entities.items;

import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.entities.states.StateHolder;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.enums.ItemRarity;
import ru.rdude.rpg.game.logic.stats.Stats;
import ru.rdude.rpg.game.logic.stats.secondary.Hp;

import java.util.Set;

public abstract class Item extends Entity {

    public static final int MAX_AMOUNT = 99;

    protected ItemData itemData;
    protected int amount;
    protected Hp durability;
    protected StateHolder<Element> elements;
    protected Stats stats;


    public Item(ItemData itemData, int amount) {
        this.itemData = itemData;
        this.amount = amount;
        Double durabilityData;
        if ((durabilityData = itemData.getDurability()) != null)
            durability = new Hp(durabilityData);
        Set<Element> elementsData;
        if ((elementsData = itemData.getElements()) != null)
            elements = new StateHolder<>(elementsData);
        stats = new Stats(false);
        stats.increase(itemData.getStats());
    }

    public Item(ItemData itemData) {
        this(itemData, 1);
    }



    public boolean isStackable() {
        return itemData.isStackable();
    }

    public Stats requirements() {
        return itemData.getRequirements();
    }

    public Stats stats() {
        return stats;
    }

    public ItemRarity rarity() {
        return itemData.getRarity();
    }

    public StateHolder<Element> elements() {
        return elements;
    }

    public Coefficients coefficients() {
        return itemData.getCoefficients();
    }

    public Hp durability() {
        return durability;
    }

    public double price() {
        return itemData.getPrice();
    }

    public int getAmount() { return amount; }

    public int increaseAmountAndReturnRest(int value) {
        amount += value;
        if (amount > MAX_AMOUNT) {
            int rest = amount - MAX_AMOUNT;
            amount = MAX_AMOUNT;
            return rest;
        }
        else return 0;
    }

    @Override
    public String getName() {
        return itemData.getName();
    }

    public void decreaseAmount(int value) {
        amount -= value;
    }

    public void setAmount(int value) {
        amount = value;
    }

    public abstract Item copy();

    public ItemData getItemData() {
        return itemData;
    }
}
