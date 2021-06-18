package ru.rdude.rpg.game.logic.entities.items;

import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.entities.states.StateHolder;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.enums.ItemRarity;
import ru.rdude.rpg.game.logic.stats.Stats;
import ru.rdude.rpg.game.utils.SubscribersManager;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Item extends Entity {

    public static final int MAX_AMOUNT = 99;

    private SubscribersManager<ItemCountObserver> subscribers;

    protected ItemData itemData;
    protected int amount;
    protected StateHolder<Element> elements;

    public Item(ItemData itemData, int amount) {
        this.subscribers = new SubscribersManager<>();
        this.itemData = itemData;
        this.amount = amount;
        Set<Element> elementsData;
        if ((elementsData = itemData.getElements()) != null)
            elements = new StateHolder<>(elementsData);
        else {
            elements = new StateHolder<>(new HashSet<>());
        }
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

    public ItemRarity rarity() {
        return itemData.getRarity();
    }

    public StateHolder<Element> elements() {
        return elements;
    }

    public Coefficients coefficients() {
        return itemData.getCoefficients();
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
            notifySubscribers();
            return rest;
        }
        else {
            notifySubscribers();
            return 0;
        }
    }

    @Override
    public String getName() {
        return itemData.getName();
    }

    public void decreaseAmount(int value) {
        amount -= value;
        notifySubscribers();
    }

    public void setAmount(int value) {
        amount = value;
        notifySubscribers();
    }

    public ItemData getItemData() {
        return itemData;
    }

    public Item copy() {
        Item copy = new Item(itemData, amount);
        copy.elements = elements.copy();
        return copy;
    }

    public void subscribe(ItemCountObserver subscriber) {
        subscribers.subscribe(subscriber);
    }

    public void unsubscribe(ItemCountObserver subscriber) {
        subscribers.unsubscribe(subscriber);
    }

    private void notifySubscribers() {
        subscribers.notifySubscribers(subscriber -> subscriber.update(amount, this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return Objects.equals(getItemData(), item.getItemData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItemData());
    }
}
