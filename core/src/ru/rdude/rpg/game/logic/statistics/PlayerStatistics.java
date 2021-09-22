package ru.rdude.rpg.game.logic.statistics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.data.BeingData;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.entities.beings.BeingActionObserver;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.enums.*;
import ru.rdude.rpg.game.logic.holders.Slot;
import ru.rdude.rpg.game.utils.SubscribersManager;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.*;

@JsonPolymorphicSubType("playerStatistics")
public class PlayerStatistics implements BeingActionObserver, StatisticValueObserver {

    private SubscribersManager<StatisticValueObserver> subscribers;

    @JsonProperty("values")
    private final Set<Values> values;

    @JsonCreator
    PlayerStatistics(@JsonProperty("values") Set<Values> values) {
        this.values = values;
    }

    public PlayerStatistics() {
        subscribers = new SubscribersManager<>();
        values = new HashSet<>();
    }

    public void subscribe(StatisticValueObserver subscriber) {
        subscribers.subscribe(subscriber);
    }

    public void unsubscribe(StatisticValueObserver subscriber) {
        subscribers.unsubscribe(subscriber);
    }

    private Values getOrCreateAndGetValue(BeingAction.Action action, UsedByStatistics statisticType) {
        Optional<Values> storedValue = values.stream()
                .filter(value -> value.beingAction.equals(action) && value.statisticType.equals(statisticType))
                .findAny();
        if (storedValue.isPresent()) {
            return storedValue.get();
        }
        else {
            Values createdValue = new  Values(action, statisticType);
            values.add(createdValue);
            createdValue.subscribe(this);
            return createdValue;
        }
    }

    @Override
    public void update(BeingAction action, Being<?> being) {
        Entity<?> interactor = action.interactor();
        double value = action.value();
        if (interactor instanceof Being) {
            ((Being<?>) interactor).beingTypes().getCurrent().forEach(type -> getOrCreateAndGetValue(action.action(), type).increase(value));
            ((Being<?>) interactor).size().getCurrent().forEach(size -> getOrCreateAndGetValue(action.action(), size).increase(value));
        }
        if (interactor instanceof Buff) {
            getOrCreateAndGetValue(action.action(), ((Buff) interactor).getEntityData().getBuffType()).increase(value);
        }
        if (action.withSkill() != null) {
            action.withSkill().getElements().forEach(element -> getOrCreateAndGetValue(action.action(), element).increase(value));
            getOrCreateAndGetValue(action.action(), action.withSkill().getType()).increase(value);
            if (action.withSkill().getAttackType() != AttackType.WEAPON_TYPE) {
                getOrCreateAndGetValue(action.action(), action.withSkill().getAttackType()).increase(value);
            } else {
                if (action.action() == BeingAction.Action.DAMAGE_DEAL
                        || action.action() == BeingAction.Action.HEAL_DEAL
                        || action.action() == BeingAction.Action.BUFF_DEAL
                        || action.action() == BeingAction.Action.KILL) {
                    getOrCreateAndGetValue(action.action(), being.getAttackType()).increase(value);
                } else if (interactor instanceof Being) {
                    getOrCreateAndGetValue(action.action(), ((Being<?>) interactor).getAttackType()).increase(value);
                }
            }
        }
        Slot<Item> leftHandSlot = being.equipment().leftHand();
        Slot<Item> rightHandSlot = being.equipment().rightHand();
        if (!leftHandSlot.isEmpty()) {
            getOrCreateAndGetValue(action.action(), leftHandSlot.getEntity().getEntityData().getItemType()).increase(value);
        }
        if (!rightHandSlot.isEmpty()) {
            getOrCreateAndGetValue(action.action(), rightHandSlot.getEntity().getEntityData().getItemType()).increase(value);
        }
        if (interactor instanceof Item) {
            getOrCreateAndGetValue(action.action(), ((Item) interactor).getEntityData().getItemType().getMainType()).increase(value);
            getOrCreateAndGetValue(action.action(), ((Item) interactor).getEntityData().getItemType()).increase(value);
        }
    }

    @Override
    public void update(BeingAction.Action action, UsedByStatistics statisticType, int times, double value) {
        subscribers.notifySubscribers(subscriber -> subscriber.update(action, statisticType, times, value));
    }

    public static class Values {

        @JsonProperty("beingAction")
        private final BeingAction.Action beingAction;
        @JsonProperty("statisticsType")
        private final UsedByStatistics statisticType;
        @JsonProperty("times")
        private int times;
        @JsonProperty("value")
        private double value;
        private SubscribersManager<StatisticValueObserver> subscribers;

        @JsonCreator
        public Values(@JsonProperty("times") int times,
                      @JsonProperty("value") double value,
                      @JsonProperty("beingAction") BeingAction.Action action,
                      @JsonProperty("statisticsType") UsedByStatistics type) {
            this.times = times;
            this.value = value;
            this.beingAction = action;
            this.statisticType = type;
        }

        public Values(BeingAction.Action beingAction, UsedByStatistics statisticType) {
            this.beingAction = beingAction;
            this.statisticType = statisticType;
            subscribers = new SubscribersManager<>();
            times = 0;
            value = 0d;
        }

        public void increase(double value) {
            this.value += value;
            times++;
            subscribers.notifySubscribers(subscriber -> subscriber.update(beingAction, statisticType, times, value));
        }

        public int getTimes() {
            return times;
        }

        public double getValue() {
            return value;
        }

        public void subscribe(StatisticValueObserver subscriber) {
            subscribers.subscribe(subscriber);
        }

        public void unsubscribe(StatisticValueObserver subscriber) {
            subscribers.unsubscribe(subscriber);
        }
    }
}
