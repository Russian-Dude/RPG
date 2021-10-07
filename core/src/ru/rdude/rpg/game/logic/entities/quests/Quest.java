package ru.rdude.rpg.game.logic.entities.quests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.data.QuestData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.entities.beings.*;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.items.ItemCountObserver;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.holders.Slot;
import ru.rdude.rpg.game.logic.holders.SlotObserver;
import ru.rdude.rpg.game.utils.SubscribersManager;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@JsonPolymorphicSubType("quest")
public class Quest implements BeingActionObserver, SlotObserver, PartyObserver, ItemCountObserver {

    private QuestData questData;
    private QuestEndLocation endLocation;

    private Map<Long, Amount> killMonsters = new HashMap<>();
    private Map<Long, Amount> collectItems = new HashMap<>();
    private Map<Long, Amount> useSkills = new HashMap<>();

    private boolean complete = false;

    @JsonCreator
    private Quest(@JsonProperty("questData") long guid) {
        this.questData = QuestData.getQuestByGuid(guid);
    }

    public Quest(QuestData questData) {
        this.questData = questData;
        // kill monsters
        questData.getKillMonsters().forEach((guid, amount) -> {
            MonsterData monsterData = MonsterData.getMonsterByGuid(guid);
            long res;
            if (questData.isKillMonstersDescriberToReal()) {
                res = Game.getEntityFactory().monsters().describerToReal(monsterData).getGuid();
            }
            else {
                res = monsterData.getGuid();
            }
            killMonsters.put(res, new Amount(amount));
        });
        // collect items
        questData.getCollectItems().forEach((guid, amount) -> {
            ItemData itemData = ItemData.getItemDataByGuid(guid);
            long res;
            if (questData.isCollectItemsDescriberToReal()) {
                res = Game.getEntityFactory().items().describerToReal(itemData).getGuid();
            }
            else {
                res = itemData.getGuid();
            }
            collectItems.put(res, new Amount(amount));
        });
        // use skills
        questData.getUseSkills().forEach((guid, amount) -> {
            SkillData skillData = SkillData.getSkillByGuid(guid);
            long res;
            if (questData.isUseSkillsDescriberToReal()) {
                res = Game.getEntityFactory().skills().describerToReal(skillData).getGuid();
            }
            else {
                res = skillData.getGuid();
            }
            useSkills.put(res, new Amount(amount));
        });

        // subscribe to party
        Game.getCurrentGame().getCurrentPlayers().subscribe(this);
        // subscribe to players
        Game.getCurrentGame().getCurrentPlayers().getBeings().forEach(being -> being.subscribe(this));
        // subscribe to slots
        Game.getCurrentGame().getCurrentPlayers().streamOnly(Player.class)
                .flatMap(player -> {
                    Stream<Slot<Item>> backpack = player.backpack().getSlots().stream();
                    Stream<Slot<Item>> equipment = player.equipment().getSlots().stream();
                    return Stream.concat(backpack, equipment);
                })
                .forEach(this::subscribeToSlot);
    }

    @JsonProperty("questData")
    private long getQuestDataJson() {
        return this.questData.getGuid();
    }

    public void setComplete(boolean value) {
        this.complete = value;
    }

    public boolean isComplete() {
        return complete ||
                (Stream.of(killMonsters.values(), useSkills.values(), collectItems.values())
                        .flatMap(Collection::stream)
                        .allMatch(Amount::isComplete)
                && Game.getCurrentGame().getGold().getAmount() >= questData.getCollectGold());
    }

    public QuestData getQuestData() {
        return questData;
    }

    public Map<Long, Amount> getKillMonsters() {
        return killMonsters;
    }

    public Map<Long, Amount> getCollectItems() {
        return collectItems;
    }

    public Map<Long, Amount> getUseSkills() {
        return useSkills;
    }

    public QuestEndLocation getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(QuestEndLocation endLocation) {
        this.endLocation = endLocation;
    }

    private void checkCompletion() {
        boolean kills = killMonsters.values().stream().allMatch(Amount::isComplete);
        boolean items = collectItems.values().stream().allMatch(Amount::isComplete);
        boolean skills = useSkills.values().stream().allMatch(Amount::isComplete);
        boolean result = kills && items && skills;
        if (this.complete != result) {
            setComplete(result);
        }
    }

    private void subscribeToSlot(Slot<Item> itemSlot) {
        itemSlot.subscribe(this);
        if (itemSlot.getEntity() != null) {
            if (collectItems.keySet().stream().anyMatch(guid -> Game.getSameEntityChecker().check(guid, itemSlot.getEntity().getEntityData().getGuid()))) {
                itemSlot.getEntity().subscribe(this);
            }
        }
    }

    private void unsubscribeFromSlot(Slot<Item> itemSlot) {
        itemSlot.unsubscribe(this);
        if (itemSlot.getEntity() != null) {
            itemSlot.getEntity().unsubscribe(this);
        }
    }

    private void unsubscribeFromAll() {
        // beings
        Game.getCurrentGame().getCurrentPlayers().forEach(being -> being.unsubscribe(this));
        // slots
        Game.getCurrentGame().getCurrentPlayers().streamOnly(Player.class)
                .forEach(player -> {
                    player.equipment().getSlots().forEach(this::unsubscribeFromSlot);
                    player.backpack().getSlots().forEach(this::unsubscribeFromSlot);
                });
    }

    @Override
    public void update(BeingAction action, Being<?> being) {
        // kill monsters
        if (action.action() == BeingAction.Action.KILL) {
            killMonsters.entrySet().stream()
                    .filter(entry -> Game.getSameEntityChecker().check(entry.getKey(), ((Being<?>) action.interactor()).getEntityData().getGuid()))
                    .forEach(entry -> entry.getValue().increase(1));
        }
        // use skills
        else if (action.action() == BeingAction.Action.USE_SKILL) {
            useSkills.entrySet().stream()
                    .filter(entry -> Game.getSameEntityChecker().check(entry.getKey(), action.withSkill().getGuid()))
                    .forEach(entry -> entry.getValue().increase(1));
        }
    }

    @Override
    public void partyUpdate(Party party, boolean added, Being<?> being, int position) {
        // subscribe for slots and actions for new being in party
        if (added) {
            being.subscribe(this);
            if (being instanceof Player) {
                being.backpack().getSlots().forEach(this::subscribeToSlot);
                being.equipment().getSlots().forEach(this::subscribeToSlot);
            }
        }
        // unsubscribe from slots and actions if being is removed from party
        else {
            being.unsubscribe(this);
            if (being instanceof Player) {
                being.backpack().getSlots().forEach(this::unsubscribeFromSlot);
                being.equipment().getSlots().forEach(this::unsubscribeFromSlot);
            }
        }
    }

    @Override
    public void update(Slot<?> slot, Entity<?> oldEntity, Entity<?> newEntity) {
        if (oldEntity == newEntity) {
            return;
        }
        if (newEntity == null) {
            Slot<? extends Entity<?>> slotWithOldEntity = Slot.withEntity(oldEntity);
            if (slotWithOldEntity == null || !slotWithOldEntity.getSubscribers().getSubscribers().contains(this)) {
                ((Item) oldEntity).unsubscribe(this);
                collectItems.entrySet().stream()
                        .filter(entry -> Game.getSameEntityChecker().check(entry.getKey(), oldEntity.getEntityData().getGuid()))
                        .forEach(entry -> entry.getValue().decrease(((Item) oldEntity).getAmount()));
            }
        }
        else if (!((Item) newEntity).getSubscribers().getSubscribers().contains(this)) {
            ((Item) newEntity).subscribe(this);
            collectItems.entrySet().stream()
                    .filter(entry -> Game.getSameEntityChecker().check(entry.getKey(), newEntity.getEntityData().getGuid()))
                    .forEach(entry -> entry.getValue().increase(((Item) newEntity).getAmount()));
        }
    }

    @Override
    public void update(Item item, int oldAmount, int newAmount) {
        collectItems.entrySet().stream()
                .filter(entry -> Game.getSameEntityChecker().check(entry.getKey(), item.getEntityData().getGuid()))
                .forEach(entry -> entry.getValue().increase(newAmount - oldAmount));
    }

    @Override
    public String toString() {
        return questData.getName();
    }

    public static class Amount {

        private SubscribersManager<QuestAmountObserver> subscribers;

        int required;
        int current = 0;

        private Amount() {}

        Amount(int required) {
            this.required = required;
            this.subscribers = new SubscribersManager<>();
        }

        void setCurrent(int value) {
            int oldValue = this.current;
            this.current = value;
            subscribers.notifySubscribers(subscriber -> subscriber.onQuestAmountUpdate(this, oldValue, current));
        }

        void increase(int value) {
            setCurrent(this.current + value);
        }

        void decrease(int value) {
            setCurrent(this.current - value);
        }

        boolean isComplete() {
            return current >= required;
        }

        public int getRequired() {
            return required;
        }

        public int getCurrent() {
            return current;
        }

        public void subscribe(QuestAmountObserver subscriber) {
            subscribers.subscribe(subscriber);
        }

        public void unsubscribe(QuestAmountObserver subscriber) {
            subscribers.unsubscribe(subscriber);
        }
    }

}
