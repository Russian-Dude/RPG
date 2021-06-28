package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.enums.EntityReferenceInfo;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.utils.Functions;

import java.util.*;
import java.util.stream.Collectors;

final class ItemTooltipInfoFactory {

    public List<Actor> getDescriber(ItemData itemData,
                                    EntityReferenceInfo referenceInfo,
                                    TooltipInfoHolder<?> infoHolder) {
        // if no info needed
        if (referenceInfo == EntityReferenceInfo.NO) {
            return new ArrayList<>();
        }

        List<Actor> result = new ArrayList<>();

        // name
        if (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.NAME) {
            Label nameLabel = new Label(Functions.capitalize(itemData.getName()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
            result.add(nameLabel);
        }

        List<Enum<?>> enums = new ArrayList<>();
        boolean stringEndsWithItem = true;

        // rarity
        if (itemData.getRarity() != null && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)) {
            enums.add(itemData.getRarity());
        }

        // elements
        if (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED) {
            enums.addAll(itemData.getElements());
        }

        // item type
        if (itemData.getItemType() != null && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)) {
            enums.add(itemData.getItemType());
            stringEndsWithItem = false;
        }

        // main type
        if (itemData.getItemMainType() != null && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)) {
            enums.add(itemData.getItemMainType());
            stringEndsWithItem = false;
        }

        String enumsString = enums.stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        String end = stringEndsWithItem ? " item" : "";

        if (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED) {
            Label enumsLabel = new Label("Random " + enumsString + end, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            result.add(enumsLabel);
        }

        if (itemData.getDescription() != null && !itemData.getDescription().isBlank()
                && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.DESCRIPTION)) {
            result.add(new Label(itemData.getDescription(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        }

        infoHolder.ignore(itemData);

        return result;
    }

    public List<Actor> getConcrete(ItemData itemData,
                                   EntityReferenceInfo referenceInfo,
                                   TooltipInfoHolder<?> infoHolder) {
        // if no info needed
        if (referenceInfo == EntityReferenceInfo.NO) {
            return new ArrayList<>();
        }

        List<Actor> result = new ArrayList<>();

        // name
        if (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.NAME) {
            Label nameLabel = new Label(Functions.capitalize(itemData.getName()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
            result.add(nameLabel);
        }

        // type
        if (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED) {
            if (!infoHolder.isMainObject(itemData) || !itemData.getName().toLowerCase().contains(itemData.getItemType().name().toLowerCase())) {
                Label itemType = new Label(itemData.getItemType().name(), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
                result.add(itemType);
            }
        }

        String elementsString = itemData.getElements().stream()
                .map(Element::name)
                .collect(Collectors.joining(" "));

        // requirements
        if ((referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)
                && itemData.getRequirements() != null && itemData.getRequirements()
                .stream().anyMatch(stat -> stat.value() > 0)) {
            Table statsTable = new Table();
            itemData.getRequirements().streamWithNestedStats()
                    .filter(stat -> stat.value() > 0.0)
                    .forEach(stat -> {
                        String value = Functions.trimDouble(stat.value());
                        Label statName = new Label(stat.getName(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                        Label statValue = new Label(value, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                        statsTable.add(statName)
                                .space(5f)
                                .padRight(10f);
                        statsTable.add(statValue)
                                .align(Align.left)
                                .space(5f);
                        statsTable.row();
                    });
            if (statsTable.hasChildren()) {
                statsTable.pack();
                result.add(new Label("Stats requirements:", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                result.add(statsTable);
            }
        }

        // damage
        if (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED) {
            if (itemData.isWeapon()) {
                String damageString =
                        Functions.trimDouble(itemData.getWeaponData().getMinDmg())
                                + " - "
                                + Functions.trimDouble(itemData.getWeaponData().getMaxDmg())
                                + " " + itemData.getWeaponData().getAttackType().name()
                                + (elementsString.isEmpty() ? "" : " " + elementsString)
                                + " DAMAGE";
                Label damage = new Label(damageString, UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
                result.add(damage);
                if (infoHolder.isMainObject(itemData)) {
                    infoHolder.addSubscriber(damage);
                }
            }
        }

        // elements
        if (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED) {
            if (!elementsString.isEmpty() && !itemData.isWeapon()) {
                Label elements = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                elements.setText(elementsString);
                result.add(elements);
                if (infoHolder.isMainObject(itemData)) {
                    infoHolder.addSubscriber(elements);
                }
            }
        }

        // stats
        if (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED) {
            Table statsTable = new Table();
            itemData.getStats().streamWithNestedStats()
                    .filter(stat -> stat.value() != 0.0)
                    .forEach(stat -> {
                        String value = Functions.trimDouble(stat.value());
                        if (stat.value() > 0) {
                            value = "+" + value;
                        }
                        Label statName = new Label(stat.getName(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                        Label statValue = new Label(value, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                        statsTable.add(statName)
                                .space(5f)
                                .padRight(10f);
                        statsTable.add(statValue)
                                .align(Align.left)
                                .space(5f);
                        statsTable.row();
                    });
            if (statsTable.hasChildren()) {
                statsTable.pack();
                result.add(statsTable);
            }
        }

        // skills on use
        if (!itemData.getSkillsOnUse().isEmpty() && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)) {

            // by name
            if (itemData.getEntityReferenceInfo() == EntityReferenceInfo.NAME) {
                String skillsOnUseString = itemData.getSkillsOnUse().stream()
                        .map(SkillData::getSkillByGuid)
                        .map(SkillData::getName)
                        .collect(Collectors.joining(", "));
                Label cast = new Label("Cast: ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
                Label skillsOnUse = new Label(skillsOnUseString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                HorizontalGroup group = new HorizontalGroup();
                group.addActor(cast);
                group.addActor(skillsOnUse);
                result.add(group);

            } else {

                // all fields
                if (itemData.getEntityReferenceInfo() == EntityReferenceInfo.ALL) {
                    Set<SkillData> skills = itemData.getSkillsOnUse().stream()
                            .map(SkillData::getSkillByGuid)
                            .collect(Collectors.toSet());
                    boolean isAllIgnored = skills.stream().allMatch(infoHolder::isIgnored);
                    // if all skills ignored use names
                    if (isAllIgnored) {
                        String skillsOnUseString = skills.stream()
                                .map(SkillData::getName)
                                .collect(Collectors.joining(", "));
                        Label cast = new Label("Cast: ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
                        Label skillsOnUse = new Label(skillsOnUseString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                        HorizontalGroup group = new HorizontalGroup();
                        group.addActor(cast);
                        group.addActor(skillsOnUse);
                        result.add(group);
                    }
                    // use full representation
                    else {
                        Table table = new Table();
                        table.add(new Label("Cast:", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE)).row();
                        skills.forEach(skillData -> Game.getTooltipInfoFactory().get(skillData, EntityReferenceInfo.ALL, infoHolder)
                                .forEach(actor -> table.add(actor).space(10f).row()));
                        table.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
                        table.pack();
                        result.add(table);
                    }
                }

                // integrated, description
                else {
                    itemData.getSkillsOnUse().stream()
                            .map(SkillData::getSkillByGuid)
                            .map(skillData -> Game.getTooltipInfoFactory().get(skillData, itemData.getEntityReferenceInfo(), infoHolder))
                            .flatMap(List::stream)
                            .forEach(result::add);
                }
            }
        }

        // skills on equip
        if (!itemData.getSkillsEquip().isEmpty() && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)) {

            // by name
            if (itemData.getEntityReferenceInfo() == EntityReferenceInfo.NAME) {
                String skillsOnEquipString = itemData.getSkillsEquip().stream()
                        .map(SkillData::getSkillByGuid)
                        .map(SkillData::getName)
                        .collect(Collectors.joining(", "));
                Label equip = new Label("Allows to use: ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
                Label skillsOnEquip = new Label(skillsOnEquipString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                HorizontalGroup group = new HorizontalGroup();
                group.addActor(equip);
                group.addActor(skillsOnEquip);
                result.add(group);

            } else {

                // all fields
                if (itemData.getEntityReferenceInfo() == EntityReferenceInfo.ALL) {
                    Set<SkillData> skills = itemData.getSkillsEquip().stream()
                            .map(SkillData::getSkillByGuid)
                            .collect(Collectors.toSet());
                    boolean isAllIgnored = skills.stream().allMatch(infoHolder::isIgnored);
                    // if all skills ignored use names
                    if (isAllIgnored) {
                        String skillsOnEquipString = skills.stream()
                                .map(SkillData::getName)
                                .collect(Collectors.joining(", "));
                        Label allows = new Label("Allows to use: ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
                        Label skillsOnEquip = new Label(skillsOnEquipString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                        HorizontalGroup group = new HorizontalGroup();
                        group.addActor(allows);
                        group.addActor(skillsOnEquip);
                        result.add(group);
                    }
                    // use full representation
                    else {
                        Table table = new Table();
                        table.add(new Label("Cast:", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE)).row();
                        skills.forEach(skillData -> Game.getTooltipInfoFactory().get(skillData, EntityReferenceInfo.ALL, infoHolder)
                                .forEach(actor -> table.add(actor).space(10f).row()));
                        table.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
                        table.pack();
                        result.add(table);
                    }
                }

                // integrated, description
                else {
                    itemData.getSkillsEquip().stream()
                            .map(SkillData::getSkillByGuid)
                            .map(skillData -> Game.getTooltipInfoFactory().get(skillData, itemData.getEntityReferenceInfo(), infoHolder))
                            .flatMap(List::stream)
                            .forEach(result::add);
                }
            }
        }

        // coefficients
        if (itemData.getCoefficients() != null && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)) {
            Coefficients coefficients = itemData.getCoefficients();
            Set<Label> labels = new HashSet<>();
            coefficients.atk().attackType().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "deal ", "with ", " attacks");
                if (!s.isBlank()) {
                    labels.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                }
            });
            coefficients.atk().beingType().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "deal ", "to ", " enemies");
                if (!s.isBlank()) {
                    labels.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                }

            });
            coefficients.atk().element().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "deal ", "to ", " enemies");
                if (!s.isBlank()) {
                    labels.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                }

            });
            coefficients.atk().size().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "deal ", "to ", " enemies");
                if (!s.isBlank()) {
                    labels.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                }

            });

            coefficients.def().attackType().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "receive ", "from ", " attacks");
                if (!s.isBlank()) {
                    labels.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                }

            });
            coefficients.def().beingType().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "receive ", "from ", " enemies");
                if (!s.isBlank()) {
                    labels.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                }

            });
            coefficients.def().element().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "receive ", "from ", " attacks");
                if (!s.isBlank()) {
                    labels.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                }

            });
            coefficients.def().size().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "receive ", "from ", " enemies");
                if (!s.isBlank()) {
                    labels.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                }

            });
            result.addAll(labels);
        }

        if (referenceInfo == EntityReferenceInfo.ALL) {
            Label price = new Label("Price: " + Functions.trimDouble(itemData.getPrice()), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            result.add(price);
        }

        // description
        if (!itemData.getDescription().isBlank() && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.DESCRIPTION)) {
            Label description = new Label(itemData.getDescription(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            result.add(description);
        }

        infoHolder.ignore(itemData);

        return result;
    }


    private String createCoefficientString(Enum<?> type,
                                           double value,
                                           String dealOrReceive,
                                           String withOrTo,
                                           String targets) {
        StringBuilder builder = new StringBuilder();
        String lessOrMore = value < 1 ? "less" : "more";
        double percents = value < 1 ? (1 - value) * 100 : (value - 1) * 100;
        if (value != 1) {
            builder.append(dealOrReceive)
                    .append(Functions.trimDouble(percents))
                    .append("% ")
                    .append(lessOrMore)
                    .append(" damage ")
                    .append(withOrTo)
                    .append(type.name().toLowerCase())
                    .append(targets);
        }
        return builder.toString();
    }
}
