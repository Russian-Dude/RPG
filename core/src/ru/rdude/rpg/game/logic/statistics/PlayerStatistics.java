package ru.rdude.rpg.game.logic.statistics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.entities.beings.BeingActionObserver;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.enums.*;
import ru.rdude.rpg.game.logic.holders.Slot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerStatistics implements BeingActionObserver {

    @JsonProperty("statistics")
    private final Map<BeingAction.Action, BeingActionStatistic> statistics;

    @JsonCreator
    PlayerStatistics(@JsonProperty("statistics") Map<BeingAction.Action, BeingActionStatistic> statistics) {
        this.statistics = statistics;
    }

    public PlayerStatistics() {
        statistics = new HashMap<>();
        for (BeingAction.Action value : BeingAction.Action.values()) {
            statistics.put(value, new BeingActionStatistic(value));
        }
    }

    @Override
    public void update(BeingAction action, Being being) {
        if (action.interactor().equals(being)) {
            statistics.get(action.action()).withSelf.increase(action.value());
        } else {
            BeingActionStatistic statistic = statistics.get(action.action());
            Entity interactor = action.interactor();
            double value = action.value();
            if (interactor instanceof Being) {
                ((Being) interactor).beingTypes().getCurrent().forEach(type -> statistic.beingTypes.get(type).increase(value));
                ((Being) interactor).size().getCurrent().forEach(size -> statistic.sizes.get(size).increase(value));
            }
            if (interactor instanceof Buff) {
                statistic.buffTypes.get(((Buff) interactor).getSkillData().getBuffType()).increase(value);
            }
            if (action.withSkill() != null) {
                action.withSkill().getElements().forEach(element -> statistic.elements.get(element).increase(value));
                statistic.skillTypes.get(action.withSkill().getType()).increase(value);
                if (action.withSkill().getAttackType() != AttackType.WEAPON_TYPE) {
                    statistic.attackTypes.get(action.withSkill().getAttackType()).increase(value);
                } else {
                    if (action.action() == BeingAction.Action.DAMAGE_DEAL
                            || action.action() == BeingAction.Action.HEAL_DEAL
                            || action.action() == BeingAction.Action.BUFF_DEAL
                            || action.action() == BeingAction.Action.KILL) {
                        statistic.attackTypes.get(being.getAttackType()).increase(value);
                    } else if (interactor instanceof Being) {
                        statistic.attackTypes.get(((Being) interactor).getAttackType()).increase(value);
                    }
                }
            }
            Slot<Item> leftHandSlot = being.equipment().leftHand();
            Slot<Item> rightHandSlot = being.equipment().rightHand();
            if (!leftHandSlot.isEmpty()) {
                statistic.itemTypes.get(leftHandSlot.getEntity().getItemData().getItemType()).increase(value);
            }
            if (!rightHandSlot.isEmpty()) {
                statistic.itemTypes.get(rightHandSlot.getEntity().getItemData().getItemType()).increase(value);
            }
            if (interactor instanceof Item) {
                statistic.itemMainTypes.get(((Item) interactor).getItemData().getItemType().getMainType()).increase(value);
                statistic.itemTypes.get(((Item) interactor).getItemData().getItemType()).increase(value);
            }
        }
    }

    public static class BeingActionStatistic {

        @JsonProperty("beingAction") private final BeingAction.Action beingAction;
        @JsonProperty("beingTypes") private final Map<BeingType, Values> beingTypes;
        @JsonProperty("elements") private final Map<Element, Values> elements;
        @JsonProperty("sizes") private final Map<Size, Values> sizes;
        @JsonProperty("itemMainTypes") private final Map<ItemMainType, Values> itemMainTypes;
        @JsonProperty("itemTypes") private final Map<ItemType, Values> itemTypes;
        @JsonProperty("attackTypes") private final Map<AttackType, Values> attackTypes;
        @JsonProperty("buffTypes") private final Map<BuffType, Values> buffTypes;
        @JsonProperty("skillTypes") private final Map<SkillType, Values> skillTypes;
        @JsonProperty("withSelf") private final Values withSelf;

        @JsonCreator
        BeingActionStatistic(
                @JsonProperty("beingAction") BeingAction.Action beingAction,
                @JsonProperty("beingTypes") Map<BeingType, Values> beingTypes,
                @JsonProperty("elements") Map<Element, Values> elements,
                @JsonProperty("sizes") Map<Size, Values> sizes,
                @JsonProperty("itemMainTypes") Map<ItemMainType, Values> itemMainTypes,
                @JsonProperty("itemTypes") Map<ItemType, Values> itemTypes,
                @JsonProperty("attackTypes") Map<AttackType, Values> attackTypes,
                @JsonProperty("buffTypes") Map<BuffType, Values> buffTypes,
                @JsonProperty("skillTypes") Map<SkillType, Values> skillTypes,
                @JsonProperty("withSelf") Values withSelf) {
            this.beingAction = beingAction;
            this.beingTypes = beingTypes;
            this.elements = elements;
            this.sizes = sizes;
            this.itemMainTypes = itemMainTypes;
            this.itemTypes = itemTypes;
            this.attackTypes = attackTypes;
            this.buffTypes = buffTypes;
            this.skillTypes = skillTypes;
            this.withSelf = withSelf;
        }

        public BeingActionStatistic(BeingAction.Action beingAction) {
            this.beingAction = beingAction;
            beingTypes = new HashMap<>();
            elements = new HashMap<>();
            sizes = new HashMap<>();
            itemMainTypes = new HashMap<>();
            itemTypes = new HashMap<>();
            attackTypes = new HashMap<>();
            buffTypes = new HashMap<>();
            skillTypes = new HashMap<>();
            withSelf = new Values(BeingAction.Action.NO_ACTION, null);
            for (BeingType value : BeingType.values()) {
                beingTypes.put(value, new Values(beingAction, value));
            }
            for (Element value : Element.values()) {
                elements.put(value, new Values(beingAction, value));
            }
            for (Size value : Size.values()) {
                sizes.put(value, new Values(beingAction, value));
            }
            for (ItemType value : ItemType.values()) {
                itemTypes.put(value, new Values(beingAction, value));
            }
            for (AttackType value : AttackType.values()) {
                if (value != AttackType.WEAPON_TYPE) {
                    attackTypes.put(value, new Values(beingAction, value));
                }
            }
            for (BuffType value : BuffType.values()) {
                buffTypes.put(value, new Values(beingAction, value));
            }
            for (SkillType value : SkillType.values()) {
                skillTypes.put(value, new Values(beingAction, value));
            }
        }

        public BeingAction.Action getBeingAction() {
            return beingAction;
        }

        public Map<Element, Values> getElements() {
            return elements;
        }

        public Map<Size, Values> getSizes() {
            return sizes;
        }

        public Map<ItemMainType, Values> getItemMainTypes() {
            return itemMainTypes;
        }

        public Map<BeingType, Values> getBeingTypes() {
            return beingTypes;
        }

        public Map<ItemType, Values> getItemTypes() {
            return itemTypes;
        }

        public Map<AttackType, Values> getAttackTypes() {
            return attackTypes;
        }

        public Map<BuffType, Values> getBuffTypes() {
            return buffTypes;
        }

        public Map<SkillType, Values> getSkillTypes() {
            return skillTypes;
        }

        public Values beingType(BeingType beingType) {
            return beingTypes.get(beingType);
        }

        public Values elements(Element element) {
            return elements.get(element);
        }

        public Values size(Size size) {
            return sizes.get(size);
        }

        public Values itemMainType(ItemMainType itemMainType) {
            return itemMainTypes.get(itemMainType);
        }

        public Values itemType(ItemType itemType) {
            return itemTypes.get(itemType);
        }

        public Values attackType(AttackType attackType) {
            return attackTypes.get(attackType);
        }

        public Values buffType(BuffType buffType) {
            return buffTypes.get(buffType);
        }

        public Values skillType(SkillType skillType) {
            return skillTypes.get(skillType);
        }

        public Values withSelf() {
            return withSelf;
        }

        public static class Values {

            @JsonProperty("beingAction") private final BeingAction.Action beingAction;
            @JsonProperty("statisticsType") private final UsedByStatistics statisticType;
            @JsonProperty("times") private int times;
            @JsonProperty("value") private double value;
            @JsonIgnore private final Set<StatisticValueObserver> subscribers = new HashSet<>();

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
                times = 0;
                value = 0d;
            }

            public void increase(double value) {
                this.value += value;
                times++;
            }

            public int getTimes() {
                return times;
            }

            public double getValue() {
                return value;
            }

            public void subscribe(StatisticValueObserver subscriber) {
                subscribers.add(subscriber);
            }

            public void unsubscribe(StatisticValueObserver subscriber) {
                subscribers.remove(subscriber);
            }

            private void notifySubscribers() {
                subscribers.forEach(subscriber -> subscriber.update(beingAction, statisticType, times, value));
            }
        }
    }
}
