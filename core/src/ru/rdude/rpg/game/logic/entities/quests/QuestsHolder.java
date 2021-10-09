package ru.rdude.rpg.game.logic.entities.quests;

import ru.rdude.rpg.game.logic.data.QuestData;
import ru.rdude.rpg.game.utils.SubscribersManager;

import java.util.HashSet;
import java.util.Set;

public class QuestsHolder {

    private SubscribersManager<QuestsObserver> subscribers = new SubscribersManager<>();
    private Set<Quest> quests = new HashSet<>();
    private Set<Long> uniqueQuestCreated = new HashSet<>();

    public void add(Quest quest) {
        boolean add = quests.add(quest);
        if (add) {
            subscribers.notifySubscribers(subscriber -> subscriber.questsUpdate(true, quest));
        }
    }

    public void remove(Quest quest) {
        boolean remove = quests.remove(quest);
        if (remove) {
            subscribers.notifySubscribers(subscriber -> subscriber.questsUpdate(false, quest));
            if (quest.getQuestData().getUnique() == QuestData.Unique.ONE_ACTIVE) {
                uniqueQuestCreated.remove(quest.getQuestData().getGuid());
            }
        }
    }

    public void uniqueQuestCreated(QuestData questData) {
        uniqueQuestCreated.add(questData.getGuid());
    }

    public boolean isUniqueQuestCreated(QuestData questData) {
        return isUniqueQuestCreated(questData.getGuid());
    }

    public boolean isUniqueQuestCreated(long guid) {
        return uniqueQuestCreated.contains(guid);
    }

    public Set<Quest> getQuests() {
        return quests;
    }

    public void subscribe(QuestsObserver subscriber) {
        subscribers.subscribe(subscriber);
    }

    public void unsubscribe(QuestsObserver subscriber) {
        subscribers.unsubscribe(subscriber);
    }
}
