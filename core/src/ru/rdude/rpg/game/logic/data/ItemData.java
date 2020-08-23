package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.items.equip.Weapon;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.enums.ItemRarity;
import ru.rdude.rpg.game.logic.stats.Stats;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemData extends EntityData {

    private static Map<Long, ItemData> items = new HashMap<>();

    private Class<? extends Item> itemType;
    private String type;
    private boolean stackable;
    private Stats requirements;
    private Stats stats;
    private ItemRarity rarity;
    private Set<Element> elements;
    private List<Long> skillsOnUse;
    private List<Long> skillsEquip;
    private Coefficients coefficients;
    private Double durability;
    private double price;
    private AttackType attackType;
    private double minDmg;
    private double maxDmg;

    public ItemData(long guid) {
        super(guid);
        items.put(guid, this);
    }

    public static ListOfItemsWithParametersBuilder getItemsWith() {
        return new ListOfItemsWithParametersBuilder();
    }

    public static ItemData getItemDataByGuid(long guid) {
        return items.get(guid);
    }

    public static void storeItems(List<ItemData> list) {
        list.forEach(itemData -> items.put(itemData.getGuid(), itemData));
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

    public AttackType getAttackType() {
        return attackType;
    }

    public void setAttackType(AttackType attackType) {
        this.attackType = attackType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Double getDurability() {
        return durability;
    }

    public void setDurability(Double durability) {
        this.durability = durability;
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

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Class<? extends Item> getItemType() {
        return itemType;
    }

    public void setItemType(Class<? extends Item> itemType) {
        this.itemType = itemType;
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

        public ListOfItemsWithParametersBuilder type(Weapon.WeaponType weaponType) {
            stream = stream.filter(itemData -> itemData.type.equals(weaponType.name()));
            return this;
        }

        public ListOfItemsWithParametersBuilder type(AttackType attackType) {
            stream = stream.filter(itemData -> itemData.type.equals(attackType.name()));
            return this;
        }

        public ListOfItemsWithParametersBuilder type(Class<? extends Item> cl) {
            stream = stream.filter(itemData -> itemData.type.toUpperCase().equals(cl.getSimpleName().toUpperCase()));
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
