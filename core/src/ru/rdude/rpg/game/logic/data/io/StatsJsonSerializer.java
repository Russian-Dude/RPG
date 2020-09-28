package ru.rdude.rpg.game.logic.data.io;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.Stats;
import ru.rdude.rpg.game.logic.stats.primary.Agi;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsJsonSerializer {

    @JsonIgnore
    private Stats stats;
    @JsonIgnore
    ObjectMapper mapper;
    private boolean calculatable;

    public StatsJsonSerializer() {
        stats = new Stats(false);
        calculatable = false;
        mapper = new ObjectMapper();
    }

    public StatsJsonSerializer(Stats stats) {
        this.stats = stats;
        calculatable = stats.isCalculatable();
        mapper = new ObjectMapper();
    }

    public Stats getStats() {
        stats.setCalculatable(this.calculatable);
        return stats;
    }

    public boolean isCalculatable() {
        return calculatable;
    }

    public void setCalculatable(boolean calculatable) {
        this.calculatable = calculatable;
    }

    @JsonGetter(value = "Agi")
    public JsonNode getAgi() {
        ObjectNode node = mapper.createObjectNode();
        node.put("Value", stats.agiValue());
        node.put("Buffs", mapper.valueToTree(stats.agi().getBuffs()));
        return node;
    }

    @JsonSetter(value = "Agi")
    public void setAgi(JsonNode node) {
        stats.agi().set(node.get("Value").doubleValue());
        stats.agi().setBuffs(mapper.convertValue(node.get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }

    @JsonGetter(value = "Dex")
    public JsonNode getDex() {
        ObjectNode node = mapper.createObjectNode();
        node.put("Value", stats.dexValue());
        node.put("Buffs", mapper.valueToTree(stats.dex().getBuffs()));
        return node;
    }

    @JsonSetter(value = "Dex")
    public void setDex(JsonNode node) {
        stats.dex().set(node.get("Value").doubleValue());
        stats.dex().setBuffs(mapper.convertValue(node.get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }

    @JsonGetter(value = "Int")
    public JsonNode getInt() {
        ObjectNode node = mapper.createObjectNode();
        node.put("Value", stats.intelValue());
        node.put("Buffs", mapper.valueToTree(stats.intel().getBuffs()));
        return node;
    }

    @JsonSetter(value = "Int")
    public void setInt(JsonNode node) {
        stats.intel().set(node.get("Value").doubleValue());
        stats.intel().setBuffs(mapper.convertValue(node.get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }

    @JsonGetter(value = "Luck")
    public JsonNode getLuck() {
        ObjectNode node = mapper.createObjectNode();
        node.put("Value", stats.luckValue());
        node.put("Buffs", mapper.valueToTree(stats.luck().getBuffs()));
        return node;
    }

    @JsonSetter(value = "Luck")
    public void setLuck(JsonNode node) {
        stats.luck().set(node.get("Value").doubleValue());
        stats.luck().setBuffs(mapper.convertValue(node.get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }

    @JsonGetter(value = "Str")
    public JsonNode getStr() {
        ObjectNode node = mapper.createObjectNode();
        node.put("Value", stats.strValue());
        node.put("Buffs", mapper.valueToTree(stats.str().getBuffs()));
        return node;
    }

    @JsonSetter(value = "Str")
    public void setStr(JsonNode node) {
        stats.str().set(node.get("Value").doubleValue());
        stats.str().setBuffs(mapper.convertValue(node.get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }

    @JsonGetter(value = "Vit")
    public JsonNode getVit() {
        ObjectNode node = mapper.createObjectNode();
        node.put("Value", stats.vitValue());
        node.put("Buffs", mapper.valueToTree(stats.vit().getBuffs()));
        return node;
    }

    @JsonSetter(value = "Vit")
    public void setVit(JsonNode node) {
        stats.vit().set(node.get("Value").doubleValue());
        stats.vit().setBuffs(mapper.convertValue(node.get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }

    @JsonGetter(value = "Lvl")
    public JsonNode getlvl() {
        ObjectNode nodeLvl = mapper.createObjectNode();
        nodeLvl.put("Type", stats.lvl().getType().name());
        nodeLvl.put("Value", stats.lvlValue());
        ObjectNode nodeExp = mapper.createObjectNode();
        nodeExp.put("Value", stats.lvl().expValue());
        nodeExp.put("Max", stats.lvl().exp().getMax());
        nodeLvl.put("Exp", nodeExp);
        nodeLvl.put("SkillPoints", stats.lvl().skillPoints().value());
        nodeLvl.put("StatPoints", stats.lvl().statPoints().value());
        return nodeLvl;
    }

    @JsonSetter(value = "Lvl")
    public void setLvl(JsonNode jsonNode) {
        stats.lvl().setType(Lvl.Type.valueOf(jsonNode.get("Type").textValue()));
        stats.lvl().set(jsonNode.get("Value").doubleValue());
        stats.lvl().exp().set(jsonNode.get("Exp").get("Value").doubleValue());
        stats.lvl().exp().setMax(jsonNode.get("Exp").get("Max").doubleValue());
        stats.lvl().skillPoints().set(jsonNode.get("SkillPoints").doubleValue());
        stats.lvl().statPoints().set(jsonNode.get("StatPoints").doubleValue());
    }

    @JsonGetter(value = "Block")
    public JsonNode getBlock() {
        ObjectNode node = mapper.createObjectNode();
        node.put("Value", stats.blockValue());
        node.put("Buffs", mapper.valueToTree(stats.block().getBuffs()));
        return node;
    }

    @JsonSetter(value = "Block")
    public void setBlock(JsonNode node) {
        stats.block().set(node.get("Value").doubleValue());
        stats.block().setBuffs(mapper.convertValue(node.get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }

    @JsonGetter(value = "Concentration")
    public JsonNode getConcentration() {
        ObjectNode node = mapper.createObjectNode();
        node.put("Value", stats.concentrationValue());
        node.put("Buffs", mapper.valueToTree(stats.concentration().getBuffs()));
        return node;
    }

    @JsonSetter(value = "Concentration")
    public void setConcentration(JsonNode node) {
        stats.concentration().set(node.get("Value").doubleValue());
        stats.concentration().setBuffs(mapper.convertValue(node.get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }

    @JsonGetter(value = "Crit")
    public JsonNode getCrit() {
        ObjectNode node = mapper.createObjectNode();
        node.put("Value", stats.critValue());
        node.put("Buffs", mapper.valueToTree(stats.crit().getBuffs()));
        return node;
    }

    @JsonSetter(value = "Crit")
    public void setCrit(JsonNode node) {
        stats.crit().set(node.get("Value").doubleValue());
        stats.crit().setBuffs(mapper.convertValue(node.get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }

    @JsonGetter(value = "Def")
    public JsonNode getDef() {
        ObjectNode node = mapper.createObjectNode();
        node.put("Value", stats.defValue());
        node.put("Buffs", mapper.valueToTree(stats.def().getBuffs()));
        return node;
    }

    @JsonSetter(value = "Def")
    public void setDef(JsonNode node) {
        stats.def().set(node.get("Value").doubleValue());
        stats.def().setBuffs(mapper.convertValue(node.get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }

    @JsonGetter(value = "Dmg")
    public JsonNode getDmg() {
        ObjectNode all = mapper.createObjectNode();
        ObjectNode melee = mapper.createObjectNode();
        ObjectNode range = mapper.createObjectNode();
        ObjectNode magic = mapper.createObjectNode();

        ObjectNode meleeMin = mapper.createObjectNode();
        meleeMin.put("Value", stats.dmg().melee().minValue());
        meleeMin.put("Buffs", mapper.valueToTree(stats.dmg().melee().min().getBuffs()));
        ObjectNode meleeMax = mapper.createObjectNode();
        meleeMax.put("Value", stats.dmg().melee().maxValue());
        meleeMax.put("Buffs",  mapper.valueToTree(stats.dmg().melee().max().getBuffs()));
        melee.put("Min", meleeMin);
        melee.put("Max", meleeMax);

        ObjectNode rangeMin = mapper.createObjectNode();
        rangeMin.put("Value", stats.dmg().range().minValue());
        rangeMin.put("Buffs", mapper.valueToTree(stats.dmg().range().min().getBuffs()));
        ObjectNode rangeMax = mapper.createObjectNode();
        rangeMax.put("Value", stats.dmg().range().maxValue());
        rangeMax.put("Buffs",  mapper.valueToTree(stats.dmg().range().max().getBuffs()));
        range.put("Min", rangeMin);
        range.put("Max", rangeMax);

        ObjectNode magicMin = mapper.createObjectNode();
        magicMin.put("Value", stats.dmg().magic().minValue());
        magicMin.put("Buffs", mapper.valueToTree(stats.dmg().magic().min().getBuffs()));
        ObjectNode magicMax = mapper.createObjectNode();
        magicMax.put("Value", stats.dmg().magic().maxValue());
        magicMax.put("Buffs",  mapper.valueToTree(stats.dmg().magic().max().getBuffs()));
        magic.put("Min", magicMin);
        magic.put("Max", magicMax);

        all.put("Melee", melee);
        all.put("Range", range);
        all.put("Magic", magic);

        return all;
    }

    @JsonSetter(value = "Dmg")
    public void setDmg(JsonNode node) {
        stats.dmg().melee().min().set(node.get("Melee").get("Min").get("Value").doubleValue());
        stats.dmg().melee().min().setBuffs(mapper.convertValue(node.get("Melee").get("Min").get("Buffs"), new TypeReference<Map<String, Double>>() {}));
        stats.dmg().range().min().set(node.get("Range").get("Min").get("Value").doubleValue());
        stats.dmg().range().min().setBuffs(mapper.convertValue(node.get("Range").get("Min").get("Buffs"), new TypeReference<Map<String, Double>>() {}));
        stats.dmg().magic().min().set(node.get("Magic").get("Min").get("Value").doubleValue());
        stats.dmg().magic().min().setBuffs(mapper.convertValue(node.get("Magic").get("Min").get("Buffs"), new TypeReference<Map<String, Double>>() {}));

        stats.dmg().melee().max().set(node.get("Melee").get("Max").get("Value").doubleValue());
        stats.dmg().melee().max().setBuffs(mapper.convertValue(node.get("Melee").get("Max").get("Buffs"), new TypeReference<Map<String, Double>>() {}));
        stats.dmg().range().max().set(node.get("Range").get("Max").get("Value").doubleValue());
        stats.dmg().range().max().setBuffs(mapper.convertValue(node.get("Range").get("Max").get("Buffs"), new TypeReference<Map<String, Double>>() {}));
        stats.dmg().magic().max().set(node.get("Magic").get("Max").get("Value").doubleValue());
        stats.dmg().magic().max().setBuffs(mapper.convertValue(node.get("Magic").get("Max").get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }

    @JsonGetter(value = "Flee")
    public JsonNode getFlee() {
        ObjectNode node = mapper.createObjectNode();
        node.put("Value", stats.flee().pureValue());
        node.put("Buffs", mapper.valueToTree(stats.flee().getBuffs()));
        ObjectNode lucky = mapper.createObjectNode();
        lucky.put("Value", stats.flee().luckyDodgeChance().value());
        lucky.put("Buffs", mapper.valueToTree(stats.flee().luckyDodgeChance().getBuffs()));
        node.put("LuckyDodge", lucky);
        return node;
    }

    @JsonSetter(value = "Flee")
    public void setFlee(JsonNode node) {
        stats.flee().set(node.get("Value").doubleValue());
        stats.flee().setBuffs(mapper.convertValue(node.get("Buffs"), new TypeReference<Map<String, Double>>() {}));
        stats.flee().luckyDodgeChance().set(node.get("LuckyDodge").get("Value").doubleValue());
        stats.flee().luckyDodgeChance().setBuffs(mapper.convertValue(node.get("LuckyDodge").get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }

    @JsonGetter(value = "Hit")
    public JsonNode getHit() {
        ObjectNode node = mapper.createObjectNode();
        node.put("Value", stats.hitValue());
        node.put("Buffs", mapper.valueToTree(stats.hit().getBuffs()));
        return node;
    }

    @JsonSetter(value = "Hit")
    public void setHit(JsonNode node) {
        stats.hit().set(node.get("Value").doubleValue());
        stats.hit().setBuffs(mapper.convertValue(node.get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }

    @JsonGetter(value = "MagicResistance")
    public JsonNode getMagicResistance() {
        ObjectNode node = mapper.createObjectNode();
        node.put("Value", stats.magicResistanceValue());
        node.put("Buffs", mapper.valueToTree(stats.magicResistance().getBuffs()));
        return node;
    }

    @JsonSetter(value = "MagicResistance")
    public void setMagicResistance(JsonNode node) {
        stats.magicResistance().set(node.get("Value").doubleValue());
        stats.magicResistance().setBuffs(mapper.convertValue(node.get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }

    @JsonGetter(value = "Parry")
    public JsonNode getParry() {
        ObjectNode node = mapper.createObjectNode();
        node.put("Value", stats.parryValue());
        node.put("Buffs", mapper.valueToTree(stats.parry().getBuffs()));
        return node;
    }

    @JsonSetter(value = "Parry")
    public void setParry(JsonNode node) {
        stats.parry().set(node.get("Value").doubleValue());
        stats.parry().setBuffs(mapper.convertValue(node.get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }

    @JsonGetter(value = "PhysicResistance")
    public JsonNode getPhysicResistance() {
        ObjectNode node = mapper.createObjectNode();
        node.put("Value", stats.physicResistanceValue());
        node.put("Buffs", mapper.valueToTree(stats.physicResistance().getBuffs()));
        return node;
    }

    @JsonSetter(value = "PhysicResistance")
    public void setPhysicResistance(JsonNode node) {
        stats.physicResistance().set(node.get("Value").doubleValue());
        stats.physicResistance().setBuffs(mapper.convertValue(node.get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }

    @JsonGetter(value = "Hp")
    public JsonNode getHp() {
        ObjectNode node = mapper.createObjectNode();
        node.put("Value", stats.hp().value());
        node.put("Buffs", mapper.valueToTree(stats.hp().getBuffs()));
        ObjectNode max = mapper.createObjectNode();
        max.put("Value", stats.hp().maxValue());
        max.put("Buffs", mapper.valueToTree(stats.hp().max().getBuffs()));
        node.put("Max", max);
        ObjectNode recovery = mapper.createObjectNode();
        recovery.put("Value", stats.hp().recoveryValue());
        recovery.put("Buffs", mapper.valueToTree(stats.hp().recovery().getBuffs()));
        node.put("Recovery", recovery);
        return node;
    }

    @JsonSetter(value = "Hp")
    public void setHp(JsonNode node) {
        stats.hp().set(node.get("Value").doubleValue());
        stats.hp().setBuffs(mapper.convertValue(node.get("Buffs"), new TypeReference<Map<String, Double>>() {}));
        stats.hp().max().set(node.get("Max").get("Value").doubleValue());
        stats.hp().max().setBuffs(mapper.convertValue(node.get("Max").get("Buffs"), new TypeReference<Map<String, Double>>() {}));
        stats.hp().recovery().set(node.get("Recovery").get("Value").doubleValue());
        stats.hp().recovery().setBuffs(mapper.convertValue(node.get("Recovery").get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }

    @JsonGetter(value = "Stm")
    public JsonNode getStm() {
        ObjectNode node = mapper.createObjectNode();
        node.put("Value", stats.stmValue());
        node.put("Buffs", mapper.valueToTree(stats.stm().getBuffs()));

        ObjectNode max = mapper.createObjectNode();
        max.put("Value", stats.stm().maxValue());
        max.put("Buffs", mapper.valueToTree(stats.stm().max().getBuffs()));
        node.put("Max", max);

        ObjectNode recovery = mapper.createObjectNode();
        recovery.put("Value", stats.stm().recoveryValue());
        recovery.put("Buffs", mapper.valueToTree(stats.stm().recovery().getBuffs()));
        node.put("Recovery", recovery);

        ObjectNode perHit = mapper.createObjectNode();
        perHit.put("Value", stats.stm().perHitValue());
        perHit.put("Buffs", mapper.valueToTree(stats.stm().perHit().getBuffs()));
        node.put("PerHit", perHit);

        ObjectNode hardness = mapper.createObjectNode();
        hardness.put("Value", stats.stm().hardness().value());
        hardness.put("Buffs", mapper.valueToTree(stats.stm().hardness().getBuffs()));
        node.put("Hardness", hardness);
        return node;
    }

    @JsonSetter(value = "Stm")
    public void setStm(JsonNode node) {
        stats.stm().set(node.get("Value").doubleValue());
        stats.stm().setBuffs(mapper.convertValue(node.get("Buffs"), new TypeReference<Map<String, Double>>() {}));

        stats.stm().max().set(node.get("Max").get("Value").doubleValue());
        stats.stm().max().setBuffs(mapper.convertValue(node.get("Max").get("Buffs"), new TypeReference<Map<String, Double>>() {}));

        stats.stm().recovery().set(node.get("Recovery").get("Value").doubleValue());
        stats.stm().recovery().setBuffs(mapper.convertValue(node.get("Recovery").get("Buffs"), new TypeReference<Map<String, Double>>() {}));

        stats.stm().perHit().set(node.get("PerHit").get("Value").doubleValue());
        stats.stm().perHit().setBuffs(mapper.convertValue(node.get("PerHit").get("Buffs"), new TypeReference<Map<String, Double>>() {}));

        stats.stm().hardness().set(node.get("Hardness").get("Value").doubleValue());
        stats.stm().hardness().setBuffs(mapper.convertValue(node.get("Hardness").get("Buffs"), new TypeReference<Map<String, Double>>() {}));
    }
}
