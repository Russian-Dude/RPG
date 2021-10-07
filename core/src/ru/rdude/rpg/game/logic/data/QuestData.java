package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.logic.data.resources.QuestResources;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.enums.StatName;
import ru.rdude.rpg.game.logic.stats.Stats;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.*;
import java.util.stream.Stream;

@JsonPolymorphicSubType("questData")
public class QuestData extends EntityData {

    public enum RewardTarget { ALL, SELECTED, RANDOM }

    public enum EndQuestPlace { WHERE_GET, INSTANT }

    private static Map<Long, QuestData> quests = new HashMap<>();

    private int lvl = 0;
    private EndQuestPlace endQuestPlace = EndQuestPlace.WHERE_GET;
    private boolean unique = false;

    // requirements
    private Map<Long, Integer> killMonsters = new HashMap<>();
    private boolean killMonstersDescriberToReal = true;
    private Map<Long, Integer> collectItems = new HashMap<>();
    private boolean collectItemsDescriberToReal = true;
    private boolean takeItems = true;
    private int collectGold = 0;
    private boolean takeGold = true;
    private Map<Long, Integer> useSkills = new HashMap<>();
    private boolean useSkillsDescriberToReal = true;

    // rewards
    private Set<Long> learnSkills = new HashSet<>();
    private RewardTarget learnSkillsRewardTarget = RewardTarget.ALL;
    private Map<Long, Integer> receiveItems = new HashMap<>();
    private Map<StatName, Double> receiveStats = new HashMap<>();
    private RewardTarget receiveStatsRewardTarget = RewardTarget.ALL;
    private int receiveGold = 0;
    private Set<Long> startQuests = new HashSet<>();
    private Long startEvent = null;
    private int expReward = 0;

    private QuestData() { }

    public QuestData(long guid) {
        super(guid);
        setResources(new QuestResources());
        quests.put(guid, this);
    }

    public static void storeQuests(Collection<QuestData> collection) {
        collection.forEach(questData -> quests.put(questData.getGuid(), questData));
    }

    public static QuestData getQuestByGuid(long guid) {
        return quests.get(guid);
    }

    public static Map<Long, QuestData> getQuests() {
        return quests;
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

    public int getCollectGold() {
        return collectGold;
    }

    public void setCollectGold(int collectGold) {
        this.collectGold = collectGold;
    }

    public Map<Long, Integer> getUseSkills() {
        return useSkills;
    }

    public void setUseSkills(Map<Long, Integer> useSkills) {
        this.useSkills = useSkills;
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

    public int getReceiveGold() {
        return receiveGold;
    }

    public void setReceiveGold(int receiveGold) {
        this.receiveGold = receiveGold;
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

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public boolean isKillMonstersDescriberToReal() {
        return killMonstersDescriberToReal;
    }

    public void setKillMonstersDescriberToReal(boolean killMonstersDescriberToReal) {
        this.killMonstersDescriberToReal = killMonstersDescriberToReal;
    }

    public boolean isCollectItemsDescriberToReal() {
        return collectItemsDescriberToReal;
    }

    public void setCollectItemsDescriberToReal(boolean collectItemsDescriberToReal) {
        this.collectItemsDescriberToReal = collectItemsDescriberToReal;
    }

    public boolean isUseSkillsDescriberToReal() {
        return useSkillsDescriberToReal;
    }

    public void setUseSkillsDescriberToReal(boolean useSkillsDescriberToReal) {
        this.useSkillsDescriberToReal = useSkillsDescriberToReal;
    }

    public int getExpReward() {
        return expReward;
    }

    public void setExpReward(int expReward) {
        this.expReward = expReward;
    }

    public RewardTarget getLearnSkillsRewardTarget() {
        return learnSkillsRewardTarget;
    }

    public void setLearnSkillsRewardTarget(RewardTarget learnSkillsRewardTarget) {
        this.learnSkillsRewardTarget = learnSkillsRewardTarget;
    }

    public Map<StatName, Double> getReceiveStats() {
        return receiveStats;
    }

    public void setReceiveStats(Map<StatName, Double> receiveStats) {
        this.receiveStats = receiveStats;
    }

    public RewardTarget getReceiveStatsRewardTarget() {
        return receiveStatsRewardTarget;
    }

    public void setReceiveStatsRewardTarget(RewardTarget receiveStatsRewardTarget) {
        this.receiveStatsRewardTarget = receiveStatsRewardTarget;
    }

    public EndQuestPlace getEndQuestPlace() {
        return endQuestPlace;
    }

    public void setEndQuestPlace(EndQuestPlace endQuestPlace) {
        this.endQuestPlace = endQuestPlace;
    }

    public boolean isTakeGold() {
        return takeGold;
    }

    public void setTakeGold(boolean takeGold) {
        this.takeGold = takeGold;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
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
