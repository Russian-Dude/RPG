package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.logic.data.resources.QuestResources;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.stats.Stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class QuestData extends EntityData {

    public static Map<Long, QuestData> quests = new HashMap<>();

    // requirements
    private Map<Long, Integer> killMonsters = new HashMap<>();
    private Map<Long, Integer> collectItems = new HashMap<>();
    private boolean takeItems = true;
    private double collectGold = 0d;
    private Map<Long, Integer> useSkills = new HashMap<>();
    private Map<BeingAction.Action, Integer> doActions = new HashMap<>();
    private Stats reachStats = new Stats(false);

    // rewards
    private Set<Long> learnSkills = new HashSet<>();
    private Map<Long, Integer> receiveItems = new HashMap<>();
    private double receiveGold = 0d;
    private Stats receiveStats = new Stats(false);
    private Set<Long> startQuests = new HashSet<>();
    private Long startEvent = null;


    public QuestData(long guid) {
        super(guid);
        setResources(new QuestResources());
        quests.put(guid, this);
    }

    public Map<Long, Integer> getKillMonsters() {
        return killMonsters;
    }

    public void setKillMonsters(Map<Long, Integer> killMonsters) {
        this.killMonsters = killMonsters;
    }

    public Map<Long, Integer> getCollectItems() {
        return collectItems;
    }

    public void setCollectItems(Map<Long, Integer> collectItems) {
        this.collectItems = collectItems;
    }

    public boolean isTakeItems() {
        return takeItems;
    }

    public void setTakeItems(boolean takeItems) {
        this.takeItems = takeItems;
    }

    public double getCollectGold() {
        return collectGold;
    }

    public void setCollectGold(double collectGold) {
        this.collectGold = collectGold;
    }

    public Map<Long, Integer> getUseSkills() {
        return useSkills;
    }

    public void setUseSkills(Map<Long, Integer> useSkills) {
        this.useSkills = useSkills;
    }

    public Map<BeingAction.Action, Integer> getDoActions() {
        return doActions;
    }

    public void setDoActions(Map<BeingAction.Action, Integer> doActions) {
        this.doActions = doActions;
    }

    public Stats getReachStats() {
        return reachStats;
    }

    public void setReachStats(Stats reachStats) {
        this.reachStats = reachStats;
    }

    public Set<Long> getLearnSkills() {
        return learnSkills;
    }

    public void setLearnSkills(Set<Long> learnSkills) {
        this.learnSkills = learnSkills;
    }

    public Map<Long, Integer> getReceiveItems() {
        return receiveItems;
    }

    public void setReceiveItems(Map<Long, Integer> receiveItems) {
        this.receiveItems = receiveItems;
    }

    public double getReceiveGold() {
        return receiveGold;
    }

    public void setReceiveGold(double receiveGold) {
        this.receiveGold = receiveGold;
    }

    public Stats getReceiveStats() {
        return receiveStats;
    }

    public void setReceiveStats(Stats receiveStats) {
        this.receiveStats = receiveStats;
    }

    public Set<Long> getStartQuests() {
        return startQuests;
    }

    public void setStartQuests(Set<Long> startQuests) {
        this.startQuests = startQuests;
    }

    public Long getStartEvent() {
        return startEvent;
    }

    public void setStartEvent(Long startEvent) {
        this.startEvent = startEvent;
    }

    @Override
    public boolean hasEntityDependency(long guid) {
        return Stream.of(
                killMonsters.keySet().stream(),
                collectItems.keySet().stream(),
                useSkills.keySet().stream(),
                learnSkills.stream(),
                receiveItems.keySet().stream(),
                startQuests.stream(),
                Stream.of(startEvent))
                .reduce(Stream::concat)
                .orElse(Stream.of())
                .anyMatch(g -> g.equals(guid));
    }

    @Override
    public void replaceEntityDependency(long oldValue, long newValue) {
        if (killMonsters.containsKey(oldValue)) {
            killMonsters.put(newValue, killMonsters.get(oldValue));
            killMonsters.remove(oldValue);
        }
        if (collectItems.containsKey(oldValue)) {
            collectItems.put(newValue, collectItems.get(oldValue));
            collectItems.remove(oldValue);
        }
        if (useSkills.containsKey(oldValue)) {
            useSkills.put(newValue, useSkills.get(oldValue));
            useSkills.remove(oldValue);
        }
        if (learnSkills.contains(oldValue)) {
            learnSkills.remove(oldValue);
            learnSkills.add(newValue);
        }
        if (receiveItems.containsKey(oldValue)) {
            receiveItems.put(newValue, receiveItems.get(oldValue));
            receiveItems.remove(oldValue);
        }
        if (startQuests.contains(oldValue)) {
            startQuests.remove(oldValue);
            startQuests.add(newValue);
        }
        if (startEvent == oldValue) {
            startEvent = newValue;
        }
    }
}
