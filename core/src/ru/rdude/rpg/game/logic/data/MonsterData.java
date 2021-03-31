package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.logic.data.resources.MonsterResources;
import ru.rdude.rpg.game.logic.enums.Biom;
import ru.rdude.rpg.game.logic.enums.Relief;

import java.util.*;

public class MonsterData extends BeingData {

    private static Map<Long, MonsterData> monsters = new HashMap<>();

    private Set<ItemsDrop> drop;
    private double goldDrop = 0d;
    private double expReward = 0d;
    private double minLvl = 0d;
    private double maxLvl = 0d;
    private double mainLvl = 0d;
    private Map<Long, Double> skills = new HashMap<>();
    private Set<Long> startBuffs = new HashSet<>();
    private Set<Biom> spawnBioms = new HashSet<>();
    private Set<Relief> spawnReliefs = new HashSet<>();


    public MonsterData(long guid) {
        super(guid);
        setResources(new MonsterResources());
        monsters.put(guid, this);
    }

    public Set<ItemsDrop> getDrop() {
        return drop;
    }

    public void setDrop(Set<ItemsDrop> drop) {
        this.drop = drop;
    }

    public double getGoldDrop() {
        return goldDrop;
    }

    public void setGoldDrop(double goldDrop) {
        this.goldDrop = goldDrop;
    }

    public double getExpReward() {
        return expReward;
    }

    public void setExpReward(double expReward) {
        this.expReward = expReward;
    }

    public double getMinLvl() {
        return minLvl;
    }

    public void setMinLvl(double minLvl) {
        this.minLvl = minLvl;
    }

    public double getMaxLvl() {
        return maxLvl;
    }

    public void setMaxLvl(double maxLvl) {
        this.maxLvl = maxLvl;
    }

    public double getMainLvl() {
        return mainLvl;
    }

    public void setMainLvl(double mainLvl) {
        this.mainLvl = mainLvl;
    }

    public Map<Long, Double> getSkills() {
        return skills;
    }

    public void setSkills(Map<Long, Double> skills) {
        this.skills = skills;
    }

    public Set<Long> getStartBuffs() {
        return startBuffs;
    }

    public void setStartBuffs(Set<Long> startBuffs) {
        this.startBuffs = startBuffs;
    }

    public Set<Biom> getSpawnBioms() {
        return spawnBioms;
    }

    public void setSpawnBioms(Set<Biom> spawnBioms) {
        this.spawnBioms = spawnBioms;
    }

    public Set<Relief> getSpawnReliefs() {
        return spawnReliefs;
    }

    public void setSpawnReliefs(Set<Relief> spawnReliefs) {
        this.spawnReliefs = spawnReliefs;
    }

    @Override
    public MonsterResources getResources() {
        return (MonsterResources) super.getResources();
    }

    public void setResources(MonsterResources monsterResources) {
        super.setResources(monsterResources);
    }

    @Override
    public boolean hasEntityDependency(long guid) {
        return false;
    }

    @Override
    public void replaceEntityDependency(long oldValue, long newValue) {

    }

    public static void storeMonsters(Collection<MonsterData> collection) {
        collection.forEach(monsterData -> monsters.put(monsterData.getGuid(), monsterData));
    }

    public class ItemsDrop {

        private long itemData;
        private int amount;
        private double chance;

        public ItemsDrop(long itemData, int amount, double chance) {
            this.itemData = itemData;
            this.amount = amount;
            this.chance = chance;
        }

        public long getItemData() {
            return itemData;
        }

        public void setItemData(long itemData) {
            this.itemData = itemData;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public double getChance() {
            return chance;
        }

        public void setChance(double chance) {
            this.chance = chance;
        }
    }
}
