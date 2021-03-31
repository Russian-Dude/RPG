package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.data.resources.ItemResources;
import ru.rdude.rpg.game.logic.enums.*;
import ru.rdude.rpg.game.logic.stats.Stats;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemData extends EntityData {

    private static Map<Long, ItemData> items = new HashMap<>();

    private ItemType itemType;
    private boolean stackable;
    private Stats requirements = new Stats(false);
    private Stats stats = new Stats(false);
    private ItemRarity rarity;
    private Set<Element> elements = new HashSet<>();
    private List<Long> skillsOnUse = new ArrayList<>();
    private List<Long> skillsEquip = new ArrayList<>();
    private Coefficients coefficients;
    private double price;
    private WeaponData weaponData = new WeaponData();

    ItemData() {
        super();
    }

    public ItemData(long guid) {
        super(guid);
        setResources(new ItemResources());
        items.put(guid, this);
    }

    public static ListOfItemsWithParametersBuilder getItemsWith() {
        return new ListOfItemsWithParametersBuilder();
    }

    public static ItemData getItemDataByGuid(long guid) {
        return items.get(guid);
    }

    public static void storeItems(Collection<ItemData> collection) {
        collection.forEach(itemData -> items.put(itemData.getGuid(), itemData));
    }

    public static Map<Long, ItemData> getItems() {
        return items;
    }

    public static void setItems(Map<Long, ItemData> items) {
        ItemData.items = items;
    }

    public WeaponData getWeaponData() {
        return weaponData;
    }

    public void setWeaponData(WeaponData weaponData) {
        this.weaponData = weaponData;
    }

    public boolean isWeapon() {
        return weaponData != null;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isStackable() {
        return stackable;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public Stats getRequirements() {
        return requirements;
    }

    public void setRequirements(Stats requirements) {
        this.requirements = requirements;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public void setRarity(ItemRarity rarity) {
        this.rarity = rarity;
    }

    public Set<Element> getElements() {
        return elements;
    }

    public void setElements(Set<Element> elements) {
        this.elements = elements;
    }

    public List<Long> getSkillsOnUse() {
        return skillsOnUse;
    }

    public void setSkillsOnUse(List<Long> skillsOnUse) {
        this.skillsOnUse = skillsOnUse;
    }

    public List<Long> getSkillsEquip() {
        return skillsEquip;
    }

    public void setSkillsEquip(List<Long> skillsEquip) {
        this.skillsEquip = skillsEquip;
    }

    public Coefficients getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(Coefficients coefficients) {
        this.coefficients = coefficients;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    @Override
    public ItemResources getResources() {
        return (ItemResources) super.getResources();
    }

    public void setResources(ItemResources resources) {
        super.setResources(resources);
    }

    @Override
    public boolean hasEntityDependency(long guid) {
        return skillsOnUse.contains(guid) || skillsEquip.contains(guid);
    }

    @Override
    public void replaceEntityDependency(long oldValue, long newValue) {
        if (skillsOnUse.contains(oldValue)) {
            skillsOnUse.remove(oldValue);
            skillsOnUse.add(newValue);
        }
        if (skillsEquip.contains(oldValue)) {
            skillsEquip.remove(oldValue);
            skillsEquip.add(newValue);
        }
    }

    public class WeaponData implements Comparable<WeaponData> {
        private AttackType attackType;
        private boolean dualHanded;
        private double minDmg;
        private double maxDmg;

        public WeaponData() {
        }

        public WeaponData(AttackType attackType, boolean dualHanded, double minDmg, double maxDmg) {
            this.attackType = attackType;
            this.dualHanded = dualHanded;
            this.minDmg = minDmg;
            this.maxDmg = maxDmg;
        }

        public AttackType getAttackType() {
            return attackType;
        }

        public void setAttackType(AttackType attackType) {
            this.attackType = attackType;
        }

        public boolean isDualHanded() {
            return dualHanded;
        }

        public void setDualHanded(boolean dualHanded) {
            this.dualHanded = dualHanded;
        }

        public double getMinDmg() {
            return minDmg;
        }

        public void setMinDmg(double minDmg) {
            this.minDmg = minDmg;
        }

        public double getMaxDmg() {
            return maxDmg;
        }

        public void setMaxDmg(double maxDmg) {
            this.maxDmg = maxDmg;
        }

        @Override
        public int compareTo(WeaponData weaponData) {
            return (int) ((this.minDmg + this.maxDmg) - (weaponData.minDmg + weaponData.maxDmg));
        }
    }

    public static class ListOfItemsWithParametersBuilder {

        private Stream<ItemData> stream;

        private ListOfItemsWithParametersBuilder() {
            stream = items.values().stream();
        }

        public ListOfItemsWithParametersBuilder rarity(ItemRarity rarity) {
            stream = stream.filter(itemData -> itemData.rarity.equals(rarity));
            return this;
        }

        public ListOfItemsWithParametersBuilder type(ItemType itemType) {
            stream = stream.filter(itemData -> itemData.itemType == itemType);
            return this;
        }

        public ListOfItemsWithParametersBuilder type(ItemMainType itemMainType) {
            stream = stream.filter(itemData -> itemData.itemType.getMainType() == itemMainType);
            return this;
        }

        public ListOfItemsWithParametersBuilder type(AttackType attackType) {
            stream = stream.filter(itemData -> itemData.isWeapon() && itemData.weaponData.attackType == attackType);
            return this;
        }

        public ListOfItemsWithParametersBuilder priceLowerThan(double price) {
            stream = stream.filter(itemData -> itemData.price < price);
            return this;
        }

        public ListOfItemsWithParametersBuilder priceHigherThan(double price) {
            stream = stream.filter(itemData -> itemData.price > price);
            return this;
        }

        public ListOfItemsWithParametersBuilder elements(Set<Element> elements) {
            stream = stream.filter(itemData -> itemData.elements.containsAll(elements));
            return this;
        }

        public ListOfItemsWithParametersBuilder elements(Element... elements) {
            return elements(new HashSet<>(Arrays.asList(elements)));
        }

        public List<ItemData> get() {
            return stream.collect(Collectors.toList());
        }
    }
}
