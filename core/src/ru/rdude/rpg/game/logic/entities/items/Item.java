package ru.rdude.rpg.game.logic.entities.items;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.entities.skills.SkillsProvider;
import ru.rdude.rpg.game.logic.entities.states.StateHolder;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.enums.ItemRarity;
import ru.rdude.rpg.game.logic.stats.Stats;
import ru.rdude.rpg.game.utils.SubscribersManager;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@JsonPolymorphicSubType("item")
public class Item extends Entity<ItemData> implements SkillsProvider {

    public static final int MAX_AMOUNT = 99;

    private SubscribersManager<ItemCountObserver> subscribers;

    protected int amount;
    protected StateHolder<Element> elements;

    @JsonCreator
    private Item(@JsonProperty("entityData") long guid) {
        super(guid);
    }

    public Item(ItemData entityData, int amount) {
        super(entityData);
        this.subscribers = new SubscribersManager<>();
        this.amount = amount;
        Set<Element> elementsData;
        if ((elementsData = entityData.getElements()) != null)
            elements = new StateHolder<>(elementsData);
        else {
            elements = new StateHolder<>(new HashSet<>());
        }
    }

    public Item(ItemData entityData) {
        this(entityData, 1);
    }

    public boolean isStackable() {
        return entityData.isStackable();
    }

    public Stats requirements() {
        return entityData.getRequirements();
    }

    public ItemRarity rarity() {
        return entityData.getRarity();
    }

    public StateHolder<Element> elements() {
        return elements;
    }

    public Coefficients coefficients() {
        return entityData.getCoefficients();
    }

    public double price() {
        return entityData.getPrice();
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
        return entityData.getName();
    }

    @Override
    protected ItemData getDataByGuid(long guid) {
        return ItemData.getItemDataByGuid(guid);
    }

    public void decreaseAmount(int value) {
        amount -= value;
        notifySubscribers();
    }

    public void setAmount(int value) {
        amount = value;
        notifySubscribers();
    }

    public Item copy() {
        Item copy = new Item(entityData, amount);
        copy.elements = elements.copy();
        return copy;
    }

    @Override
    public Collection<Long> getAvailableSkills() {
        return entityData.getSkillsEquip();
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
}
