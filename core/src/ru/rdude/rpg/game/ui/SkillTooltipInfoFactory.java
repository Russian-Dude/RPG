package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.enums.*;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.utils.Functions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class SkillTooltipInfoFactory {

    public List<Actor> getDescriber(
            SkillData skillData,
            EntityReferenceInfo referenceInfo,
            TooltipInfoHolder<?> infoHolder) {
        // if no info needed
        if (referenceInfo == EntityReferenceInfo.NO) {
            return new ArrayList<>();
        }

        List<Actor> result = new ArrayList<>();

        // name
        if (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.NAME) {
            Label nameLabel = new Label(Functions.capitalize(skillData.getName()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
            result.add(nameLabel);
        }

        List<Enum<?>> enums = new ArrayList<>();
        boolean stringEndsWithSkill = true;

        // attack type
        if (skillData.getAttackType() != null) {
            enums.add(skillData.getAttackType());
        }

        // elements
        if (skillData.getElements() != null && !skillData.getElements().isEmpty()) {
            enums.addAll(skillData.getElements());
        }

        // effect
        if (skillData.getEffect() != null && skillData.getEffect() != SkillEffect.NO) {
            enums.add(skillData.getEffect());
        }

        // type
        if (skillData.getType() != null) {
            stringEndsWithSkill = false;
            enums.add(skillData.getType());
        }

        String enumsString = enums.stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        String end = stringEndsWithSkill ? " skill" : "";

        if (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED) {
            Label enumsLabel = new Label("Random " + enumsString + end, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            result.add(enumsLabel);
        }

        if (skillData.getDescription() != null && !skillData.getDescription().isBlank()
                && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.DESCRIPTION)) {
            result.add(new Label(skillData.getDescription(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        }

        infoHolder.ignore(skillData);

        return result;

    }

    public List<Actor> getConcrete(
            SkillData skillData,
            EntityReferenceInfo referenceInfo,
            TooltipInfoHolder<?> infoHolder) {
        // if no info needed
        if (referenceInfo == EntityReferenceInfo.NO) {
            return new ArrayList<>();
        }

        List<Actor> result = new ArrayList<>();

        // name and type
        if (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.NAME) {
            Label nameLabel = new Label(Functions.capitalize(skillData.getName()) + stringType(skillData.getType()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
            result.add(nameLabel);
        }

        String elementsString = skillData.getElements().stream()
                .map(Element::name)
                .collect(Collectors.joining(", "));

        String subTargets = skillData.getTargets().stream()
                .map(Target::name)
                .collect(Collectors.joining(", "));
        String targets = skillData.getMainTarget().name();
        if (skillData.getTargets().size() > 0) {
            targets = targets + ", " + subTargets;
        }

        // targets
        if ((skillData.getDamage() == null || skillData.getDamage().isEmpty() || skillData.getDamage().equals("0"))
                && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)
                && infoHolder.optionMeetsCondition(AbilityTooltipInfoFactory.PassiveSkills.class, o -> o.isNotPassive(skillData))) {
            result.add(new Label("Targets: " + targets, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        }

        // requirements
        if ((referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)
                && (!skillData.getStaminaReq().isEmpty() || skillData.getConcentrationReq() > 0)
                && infoHolder.optionMeetsCondition(AbilityTooltipInfoFactory.PassiveSkills.class, o -> o.isNotPassive(skillData))) {
            String stamina = !skillData.getStaminaReq().isEmpty() ? skillData.getStaminaReq() + " stamina" : "";
            String concentration = skillData.getConcentrationReq() > 0 ? skillData.getConcentrationReq() + " concentration" : "";
            String requirements = Stream.of(stamina, concentration)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.joining(", "));
            result.add(new Label("Required " + requirements, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        }

        // stats requirements
        if ((referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)
                && skillData.getRequirements().getStats() != null && skillData.getRequirements().getStats()
                .stream().anyMatch(stat -> stat.value() > 0)
                && infoHolder.optionMeetsCondition(AbilityTooltipInfoFactory.PassiveSkills.class, o -> o.isNotPassive(skillData))) {
            Table statsTable = new Table();
            skillData.getRequirements().getStats().streamWithNestedStats()
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

        // items requirements
        if ((referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)
                && !skillData.getRequirements().getItems().isEmpty()
                && infoHolder.optionMeetsCondition(AbilityTooltipInfoFactory.PassiveSkills.class, o -> o.isNotPassive(skillData))) {
            String requiredOrConsumed = skillData.getRequirements().isTakeItems() ? "Consume items: " : "Items requirements ";
            String items = skillData.getRequirements().getItems().entrySet().stream()
                    .map(entry -> ItemData.getItemDataByGuid(entry.getKey()).getName() + " (" + entry.getValue() + ")")
                    .collect(Collectors.joining(", "));
            result.add(new Label(requiredOrConsumed + items, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        }

        // permanent
        if ((referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)
                && skillData.hasDuration() && skillData.isPermanent()
                && infoHolder.optionMeetsCondition(AbilityTooltipInfoFactory.PassiveSkills.class, o -> o.isNotPassive(skillData))) {
            result.add(new Label("PERMANENT", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        }

        // acts every
        if ((referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)
                && skillData.hasDuration() && (skillData.getActsEveryMinute() > 0 || skillData.getActsEveryTurn() > 0)) {
            String minutes = skillData.getActsEveryMinute() > 0 ? Functions.trimDouble(skillData.getActsEveryMinute()) + " minutes" : "";
            String turns = skillData.getActsEveryTurn() > 0 ? Functions.trimDouble(skillData.getActsEveryTurn()) + " turns" : "";
            String actsEvery = Stream.of(minutes, turns)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.joining(", "));
            result.add(new Label("Every " + actsEvery, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        }

        // damage
        final String damage = skillData.getDamage();
        if (damage != null && !damage.isBlank() && !damage.equals("0") && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)) {
            final String damageValue = Functions.convertFormulaToPrettyString(damage);
            // heal
            if (damage.startsWith("-")) {
                result.add(new Label(elementsString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                String open = Functions.isNumber(damageValue) ? "" : "( ";
                String close = Functions.isNumber(damageValue) ? "" : " )";
                String finalString = "Heal " + targets + " for " + open + damageValue.replaceFirst("-", "") + close;
                result.add(new Label(finalString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
            }
            // damage
            else {
                String open = Functions.isNumber(damageValue) ? "" : "( ";
                String close = Functions.isNumber(damageValue) ? " " : " ) ";
                final String attackType = skillData.getAttackType() == AttackType.WEAPON_TYPE ? "WEAPON" : skillData.getAttackType().name();
                final String finalString = "Deal " + open + damageValue + close + attackType + " " + elementsString + " damage to "
                        + targets;
                result.add(new Label(finalString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
            }
        }

        // damage coefficients
        if ((referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)
                && skillData.hasDamage() && skillData.getCoefficients() != null) {
            Coefficients coefficients = skillData.getCoefficients();
            coefficients.atk().element().getCoefficientsMap().entrySet().stream()
                    .filter(entry -> entry.getValue() != 1.0)
                    .forEach(entry -> {
                        String s = "This skill " + createCoefficientString(entry.getKey(), entry.getValue(), "deal ", "to ", " enemies");
                        result.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                    });
            coefficients.atk().beingType().getCoefficientsMap().entrySet().stream()
                    .filter(entry -> entry.getValue() != 1.0)
                    .forEach(entry -> {
                        String s = "This skill " + createCoefficientString(entry.getKey(), entry.getValue(), "deal ", "to ", " enemies");
                        result.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                    });
            coefficients.atk().size().getCoefficientsMap().entrySet().stream()
                    .filter(entry -> entry.getValue() != 1.0)
                    .forEach(entry -> {
                        String s = "This skill " + createCoefficientString(entry.getKey(), entry.getValue(), "deal ", "to ", " enemies");
                        result.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                    });
        }

        // stats
        if (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED) {
            Table statsTable = new Table();
            skillData.getStats().forEach(((statName, formula) -> {
                if (!formula.isEmpty()) {
                    statsTable.add(new Label(statName.getName(), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE)).padRight(10f);
                    statsTable.add(new Label(Functions.convertFormulaToPrettyString(statName, formula), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                    statsTable.row().space(10f);
                }
            }));
            if (statsTable.hasChildren()) {
                statsTable.pack();
                result.add(statsTable);
            }
        }

        // summon
        if (!skillData.getSummon().isEmpty() && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)) {

            // by name
            if (skillData.getEntityReferenceInfo() == EntityReferenceInfo.NAME) {
                String summonString = skillData.getSummon().stream()
                        .collect(Collectors.collectingAndThen(Collectors.toMap(s -> MonsterData.getMonsterByGuid(s.getGuid()).getName(), SkillData.Summon::getChance), Functions::normalizePercentsMap))
                        .entrySet().stream()
                        .map(entry -> {
                            String name = entry.getKey();
                            String percent = " (" + Functions.trimDouble(entry.getValue()) + "%)";
                            return name + percent;
                        })
                        .collect(Collectors.joining(", "));
                Label summon = new Label("Summon: ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
                Label summonLabel = new Label(summonString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                HorizontalGroup group = new HorizontalGroup();
                group.addActor(summon);
                group.addActor(summonLabel);
                result.add(group);

            } else {

                // all fields
                if (skillData.getEntityReferenceInfo() == EntityReferenceInfo.ALL) {
                    boolean isAllIgnored = skillData.getSummon().stream()
                            .mapToLong(SkillData.Summon::getGuid)
                            .mapToObj(MonsterData::getMonsterByGuid)
                            .allMatch(infoHolder::isIgnored);
                    // if all monsters ignored use names
                    if (isAllIgnored) {
                        String summonString = skillData.getSummon().stream()
                                .collect(Collectors.collectingAndThen(Collectors.toMap(s -> MonsterData.getMonsterByGuid(s.getGuid()).getName(), SkillData.Summon::getChance), Functions::normalizePercentsMap))
                                .entrySet().stream()
                                .map(entry -> {
                                    String name = entry.getKey();
                                    String percent = " (" + Functions.trimDouble(entry.getValue()) + "%)";
                                    return name + percent;
                                })
                                .collect(Collectors.joining(", "));
                        Label summon = new Label("Summon: ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
                        Label summonLabel = new Label(summonString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                        HorizontalGroup group = new HorizontalGroup();
                        group.addActor(summon);
                        group.addActor(summonLabel);
                        result.add(group);
                    }
                    // use full representation
                    else {
                        result.add(new Label("Summon:", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                        skillData.getSummon().stream()
                                .collect(Collectors.collectingAndThen(Collectors.toMap(SkillData.Summon::getGuid, SkillData.Summon::getChance), Functions::normalizePercentsMap))
                                .forEach((guid, percent) -> {
                            Table monsterTable = new Table();
                            monsterTable.add(new Label(Functions.trimDouble(percent) + "%", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                            Game.getTooltipInfoFactory().get(MonsterData.getMonsterByGuid(guid), EntityReferenceInfo.ALL, infoHolder)
                                    .forEach(actor -> monsterTable.add(actor).space(10f).row());
                            monsterTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
                            monsterTable.pack();
                            result.add(monsterTable);
                        });
                    }
                }

                // integrated, description
                else {
                    skillData.getSummon().stream()
                            .collect(Collectors.collectingAndThen(Collectors.toMap(SkillData.Summon::getGuid, SkillData.Summon::getChance), Functions::normalizePercentsMap))
                            .forEach((guid, percent) -> {
                        result.add(new Label(Functions.trimDouble(percent) + "%", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                        result.addAll(Game.getTooltipInfoFactory().get(MonsterData.getMonsterByGuid(guid), skillData.getEntityReferenceInfo(), infoHolder));
                    });
                }
            }
        }

        // receive items
        if (!skillData.getReceiveItems().isEmpty() && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)) {

            // by name
            if (skillData.getEntityReferenceInfo() == EntityReferenceInfo.NAME) {
                String receiveString = skillData.getReceiveItems().entrySet().stream()
                        .map(entry -> ItemData.getItemDataByGuid(entry.getKey()).getName() + " (" + Functions.trimDouble(entry.getValue()) + "%)")
                        .collect(Collectors.joining(", "));
                Label receive = new Label("Receive: ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
                Label receiveLabel = new Label(receiveString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                HorizontalGroup group = new HorizontalGroup();
                group.addActor(receive);
                group.addActor(receiveLabel);
                result.add(group);

            } else {

                // all fields
                if (skillData.getEntityReferenceInfo() == EntityReferenceInfo.ALL) {
                    boolean isAllIgnored = skillData.getReceiveItems().keySet().stream()
                            .map(ItemData::getItemDataByGuid)
                            .allMatch(infoHolder::isIgnored);
                    // if all monsters ignored use names
                    if (isAllIgnored) {
                        String receiveString = skillData.getReceiveItems().entrySet().stream()
                                .map(entry -> ItemData.getItemDataByGuid(entry.getKey()).getName() + " (" + Functions.trimDouble(entry.getValue()) + "%)")
                                .collect(Collectors.joining(", "));
                        Label receive = new Label("Receive: ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
                        Label receiveLabel = new Label(receiveString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                        HorizontalGroup group = new HorizontalGroup();
                        group.addActor(receive);
                        group.addActor(receiveLabel);
                        result.add(group);
                    }
                    // use full representation
                    else {
                        result.add(new Label("Receive:", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                        skillData.getReceiveItems().forEach((guid, percent) -> {
                            Table itemsTable = new Table();
                            itemsTable.add(new Label(Functions.trimDouble(percent) + "%", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                            Game.getTooltipInfoFactory().get(ItemData.getItemDataByGuid(guid), EntityReferenceInfo.ALL, infoHolder)
                                    .forEach(actor -> itemsTable.add(actor).space(10f).row());
                            itemsTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
                            itemsTable.pack();
                            result.add(itemsTable);
                        });
                    }
                }

                // integrated, description
                else {
                    skillData.getReceiveItems().forEach((guid, percent) -> {
                        result.add(new Label(Functions.trimDouble(percent) + "%", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                        result.addAll(Game.getTooltipInfoFactory().get(ItemData.getItemDataByGuid(guid), skillData.getEntityReferenceInfo(), infoHolder));
                    });
                }
            }
        }

        // transformation
        if (skillData.hasTransformation() && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)) {
            final Size size = skillData.getTransformation().getSize();
            String sizeString = size != null ? size.name() + " " : "";
            String elements = skillData.getTransformation().getElements().stream()
                    .map(Element::name)
                    .collect(Collectors.joining(", "));
            String beingTypes = skillData.getTransformation().getBeingTypes().stream()
                    .map(BeingType::name)
                    .collect(Collectors.joining(", "));
            result.add(new Label("Transform into " + sizeString + elements + beingTypes, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        }

        // buff coefficients
        if (skillData.getBuffCoefficients() != null && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)) {
            Coefficients coefficients = skillData.getBuffCoefficients();
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

        // skills effect
        if (skillData.getEffect() != SkillEffect.NO && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)) {
            result.add(new Label("Apply " + skillData.getEffect(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        }

        // skills can cast
        if (!skillData.getSkillsCouldCast().isEmpty() && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)) {

            // by name
            if (skillData.getEntityReferenceInfo() == EntityReferenceInfo.NAME) {
                String skillsString = skillData.getSkillsCouldCast().entrySet().stream()
                        .map(entry -> SkillData.getSkillByGuid(entry.getKey()).getName() + " (" + Functions.trimDouble(entry.getValue()) + "%)")
                        .collect(Collectors.joining(", "));
                Label skills = new Label("Cast: ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
                Label skillsLabel = new Label(skillsString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                HorizontalGroup group = new HorizontalGroup();
                group.addActor(skills);
                group.addActor(skillsLabel);
                result.add(group);

            } else {

                // all fields
                if (skillData.getEntityReferenceInfo() == EntityReferenceInfo.ALL) {
                    boolean isAllIgnored = skillData.getSkillsCouldCast().keySet().stream()
                            .map(SkillData::getSkillByGuid)
                            .allMatch(infoHolder::isIgnored);
                    // if all monsters ignored use names
                    if (isAllIgnored) {
                        String skillsString = skillData.getSkillsCouldCast().entrySet().stream()
                                .map(entry -> SkillData.getSkillByGuid(entry.getKey()).getName() + " (" + Functions.trimDouble(entry.getValue()) + "%)")
                                .collect(Collectors.joining(", "));
                        Label skills = new Label("Cast: ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
                        Label skillsLabel = new Label(skillsString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                        HorizontalGroup group = new HorizontalGroup();
                        group.addActor(skills);
                        group.addActor(skillsLabel);
                        result.add(group);
                    }
                    // use full representation
                    else {
                        result.add(new Label("Cast:", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                        skillData.getSkillsCouldCast().forEach((guid, percent) -> {
                            Table skillsTable = new Table();
                            skillsTable.add(new Label(Functions.trimDouble(percent) + "%", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                            Game.getTooltipInfoFactory().get(SkillData.getSkillByGuid(guid), EntityReferenceInfo.ALL, infoHolder)
                                    .forEach(actor -> skillsTable.add(actor).space(10f).row());
                            skillsTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
                            skillsTable.pack();
                            result.add(skillsTable);
                        });
                    }
                }

                // integrated, description
                else {
                    skillData.getSkillsCouldCast().forEach((guid, percent) -> {
                        result.add(new Label(Functions.trimDouble(percent) + "%", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                        result.addAll(Game.getTooltipInfoFactory().get(SkillData.getSkillByGuid(guid), skillData.getEntityReferenceInfo(), infoHolder));
                    });
                }
            }
        }

        // skills must cast
        if (!skillData.getSkillsMustCast().isEmpty() && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)) {

            // by name
            if (skillData.getEntityReferenceInfo() == EntityReferenceInfo.NAME) {
                String skillsString = skillData.getSkillsMustCast().entrySet().stream()
                        .map(entry -> SkillData.getSkillByGuid(entry.getKey()).getName() + " (" + Functions.trimDouble(entry.getValue()) + "%)")
                        .collect(Collectors.joining(", "));
                Label skills = new Label("Cast one of: ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
                Label skillsLabel = new Label(skillsString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                HorizontalGroup group = new HorizontalGroup();
                group.addActor(skills);
                group.addActor(skillsLabel);
                result.add(group);

            } else {

                // all fields
                if (skillData.getEntityReferenceInfo() == EntityReferenceInfo.ALL) {
                    boolean isAllIgnored = skillData.getSkillsMustCast().keySet().stream()
                            .map(SkillData::getSkillByGuid)
                            .allMatch(infoHolder::isIgnored);
                    // if all monsters ignored use names
                    if (isAllIgnored) {
                        String skillsString = skillData.getSkillsMustCast().entrySet().stream()
                                .map(entry -> SkillData.getSkillByGuid(entry.getKey()).getName() + " (" + Functions.trimDouble(entry.getValue()) + "%)")
                                .collect(Collectors.joining(", "));
                        Label skills = new Label("Cast one of: ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
                        Label skillsLabel = new Label(skillsString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                        HorizontalGroup group = new HorizontalGroup();
                        group.addActor(skills);
                        group.addActor(skillsLabel);
                        result.add(group);
                    }
                    // use full representation
                    else {
                        result.add(new Label("Cast one of:", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                        skillData.getSkillsMustCast().forEach((guid, percent) -> {
                            Table skillsTable = new Table();
                            skillsTable.add(new Label(Functions.trimDouble(percent) + "%", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                            Game.getTooltipInfoFactory().get(SkillData.getSkillByGuid(guid), EntityReferenceInfo.ALL, infoHolder)
                                    .forEach(actor -> skillsTable.add(actor).space(10f).row());
                            skillsTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
                            skillsTable.pack();
                            result.add(skillsTable);
                        });
                    }
                }

                // integrated, description
                else {
                    skillData.getSkillsMustCast().forEach((guid, percent) -> {
                        result.add(new Label(Functions.trimDouble(percent) + "%", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                        result.addAll(Game.getTooltipInfoFactory().get(MonsterData.getMonsterByGuid(guid), skillData.getEntityReferenceInfo(), infoHolder));
                    });
                }
            }
        }

        // skills on being action
        if (skillData.getSkillsOnBeingAction() != null && !skillData.getSkillsOnBeingAction().isEmpty()
                && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)) {
            // by name
            if (skillData.getEntityReferenceInfo() == EntityReferenceInfo.NAME) {
                skillData.getSkillsOnBeingAction().forEach((action, map) -> {
                    String actionString = "After " + action.prettyStringAfter + ", cast: ";
                    String skills = map.entrySet().stream()
                            .map(entry -> {
                                String name = SkillData.getSkillByGuid(entry.getKey()).getName();
                                String percent = " (" + Functions.trimDouble(entry.getValue()) + "%)";
                                return name + percent;
                            })
                            .collect(Collectors.joining(", "));
                    result.add(new Label(actionString + skills, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                });
            }
            // use full representation
           else {
               // all fields
                if (skillData.getEntityReferenceInfo() == EntityReferenceInfo.ALL) {
                    skillData.getSkillsOnBeingAction().forEach((action, map) -> {
                        boolean isAllIgnored = map.keySet().stream()
                                .map(SkillData::getSkillByGuid)
                                .allMatch(infoHolder::isIgnored);
                        // if all skills ignored - use names
                        if (isAllIgnored) {
                            String skillsString = map.entrySet().stream()
                                    .map(entry -> SkillData.getSkillByGuid(entry.getKey()).getName() + " (" + Functions.trimDouble(entry.getValue()) + "%)")
                                    .collect(Collectors.joining(", "));
                            Label skills = new Label("After " + action.prettyStringAfter + ", cast: ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
                            Label skillsLabel = new Label(skillsString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                            HorizontalGroup group = new HorizontalGroup();
                            group.addActor(skills);
                            group.addActor(skillsLabel);
                            result.add(group);
                        }
                        // use full representation
                        else {
                            result.add(new Label("After " + action.prettyStringAfter + ", cast:", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                            map.forEach((guid, percent) -> {
                                Table skillsTable = new Table();
                                skillsTable.add(new Label(Functions.trimDouble(percent) + "%", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                                Game.getTooltipInfoFactory().get(SkillData.getSkillByGuid(guid), EntityReferenceInfo.ALL, infoHolder)
                                        .forEach(actor -> skillsTable.add(actor).space(10f).row());
                                skillsTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
                                skillsTable.pack();
                                result.add(skillsTable);
                            });
                        }
                    });
                }
                // integrated, description
                else {
                    skillData.getSkillsOnBeingAction().forEach(((action, map) -> {
                        result.add(new Label("After " + action.prettyStringAfter, UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                        map.forEach((guid, percent) -> {
                            result.add(new Label(Functions.trimDouble(percent) + "%", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                            result.addAll(Game.getTooltipInfoFactory().get(MonsterData.getMonsterByGuid(guid), skillData.getEntityReferenceInfo(), infoHolder));
                        });
                    }));
                }
            }
        }

        // duration
        if (skillData.hasDuration() && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)
            && infoHolder.optionMeetsCondition(AbilityTooltipInfoFactory.PassiveSkills.class, o -> o.isNotPassive(skillData))) {
            String minutes;
            if (skillData.getDurationInMinutes() != null && !skillData.getDurationInMinutes().isBlank() && !skillData.getDurationInMinutes().matches("0+")) {
                minutes = Functions.convertFormulaToPrettyString(skillData.getDurationInMinutes()) + " minutes";
            }
            else {
                minutes = "";
            }
            String turns;
            if (skillData.getDurationInTurns() != null && !skillData.getDurationInTurns().isBlank() && !skillData.getDurationInTurns().matches("0+")) {
                turns = Functions.convertFormulaToPrettyString(skillData.getDurationInTurns()) + " turns";
            }
            else {
                turns = "";
            }
            if (!minutes.isEmpty() || !turns.isEmpty()) {
                HorizontalGroup horizontalGroup = new HorizontalGroup();
                horizontalGroup.space(10f);
                String duration = Stream.of(minutes, turns)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.joining(", "));
                horizontalGroup.addActor(new Label("Duration: ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                horizontalGroup.addActor(new Label(duration, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                result.add(horizontalGroup);
            }
        }

        // forced cancel
        if (skillData.canBeForceCanceled() && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED)
            && infoHolder.optionMeetsCondition(AbilityTooltipInfoFactory.PassiveSkills.class, o -> o.isNotPassive(skillData))) {
            result.add(new Label(getForceCancelString(skillData), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        }

        // can be blocked, resisted, dodged
        if (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.INTEGRATED
            && infoHolder.optionMeetsCondition(AbilityTooltipInfoFactory.PassiveSkills.class, o -> o.isNotPassive(skillData))) {
            String blocked = skillData.isCanBeBlocked() ? "" : "blocked";
            String resisted = skillData.isCanBeResisted() ? "" : "resisted";
            String dodged = skillData.isCanBeDodged() ? "" : "dodged";
            String all = Stream.of(blocked, resisted, dodged)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.joining(", "));
            if (!all.isEmpty()) {
                result.add(new Label("Can not be " + all, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
            }
        }

        // description
        if (!skillData.getDescription().isBlank() && (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.DESCRIPTION)) {
            Label description = new Label(skillData.getDescription(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            result.add(description);
        }

        infoHolder.ignore(skillData);

        return result;
    }

    private String getForceCancelString(SkillData skillData) {
        String damageMade;
        if (skillData.getDamageMade() != null && !skillData.getDamageMade().isEmpty() && !skillData.getDamageMade().matches("0+")){
            damageMade = Functions.convertFormulaToPrettyString(skillData.getDamageMade()) + " damage made";
        }
        else {
            damageMade = "";
        }
        String damageReceived;
        if (skillData.getDamageReceived() != null && !skillData.getDamageReceived().isEmpty() && !skillData.getDamageReceived().matches("0+")){
            damageReceived = Functions.convertFormulaToPrettyString(skillData.getDamageReceived()) + " damage received";
        }
        else {
            damageReceived = "";
        }
        String hitsMade;
        if (skillData.getHitsMade() != null && !skillData.getHitsMade().isEmpty() && !skillData.getHitsMade().matches("0+")){
            hitsMade = Functions.convertFormulaToPrettyString(skillData.getHitsMade()) + " hits made";
        }
        else {
            hitsMade = "";
        }
        String hitsReceived;
        if (skillData.getHitsReceived() != null && !skillData.getHitsReceived().isEmpty() && !skillData.getHitsReceived().matches("0+")){
            hitsReceived = Functions.convertFormulaToPrettyString(skillData.getHitsReceived()) + " hits received";
        }
        else {
            hitsReceived = "";
        }
        return "Ends after " + Stream.of(damageMade, damageReceived, hitsMade, hitsReceived)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" or "));
    }

    private String stringType(SkillType type) {
        return type == SkillType.NO_TYPE ? "" : " (" + type.name() + ")";
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
