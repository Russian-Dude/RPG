package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.logic.data.resources.MonsterResources;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.enums.Biom;
import ru.rdude.rpg.game.logic.enums.Relief;
import ru.rdude.rpg.game.logic.stats.Stats;

import java.util.*;
import java.util.stream.Collectors;

public class MonsterData extends BeingData {

    private static Map<Long, MonsterData> monsters = new HashMap<>();

    private Map<Long, Double> drop = new HashMap<>();
    private double goldDrop = 0d;
    private double expReward = 0d;
    private double minLvl = 1d;
    private double maxLvl = 1d;
    private double mainLvl = 1d;
    private Stats stats = new Stats(true);
    private Map<Long, Double> skills = new HashMap<>();
    private Set<Long> startBuffs = new HashSet<>();
    private Set<Biom> spawnBioms = Arrays.stream(Biom.values()).collect(Collectors.toSet());
    private Set<Relief> spawnReliefs = Arrays.stream(Relief.values()).collect(Collectors.toSet());
    private AttackType defaultAttackType = AttackType.MELEE;
    private boolean canBlock = false;
    private boolean canParry = false;

    public MonsterData() {
        super();
    }

    public MonsterData(long guid) {
        super(guid);
        setResources(new MonsterResources());
        monsters.put(guid, this);
    }

    public static Map<Long, MonsterData> getMonsters() {
        return monsters;
    }

    public static MonsterData getMonsterByGuid(long guid) {
        return monsters.get(guid);
    }

    public Map<Long, Double> getDrop() {
        return drop;
    }

    public void setDrop(Map<Long, Double> drop) {
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

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public AttackType getDefaultAttackType() {
        return defaultAttackType;
    }

    public void setDefaultAttackType(AttackType defaultAttackType) {
        this.defaultAttackType = defaultAttackType;
    }

    public boolean isCanBlock() {
        return canBlock;
    }

    public void setCanBlock(boolean canBlock) {
        this.canBlock = canBlock;
    }

    public boolean isCanParry() {
        return canParry;
    }

    public void setCanParry(boolean canParry) {
        this.canParry = canParry;
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
        return
                drop.containsKey(guid)
                || skills.containsKey(guid)
                || startBuffs.contains(guid);
    }

    @Override
    public void replaceEntityDependency(long oldValue, long newValue) {
        if (drop.containsKey(oldValue)) {
            drop.put(newValue, drop.get(oldValue));
            drop.remove(oldValue);
        }
        if (skills.containsKey(oldValue)) {
            skills.put(newValue, drop.get(oldValue));
            skills.remove(oldValue);
        }
        if (startBuffs.contains(oldValue)) {
            startBuffs.remove(oldValue);
            startBuffs.add(newValue);
        }
    }

    public static void storeMonsters(Collection<MonsterData> collection) {
        collection.forEach(monsterData -> monsters.put(monsterData.getGuid(), monsterData));
    }
}
