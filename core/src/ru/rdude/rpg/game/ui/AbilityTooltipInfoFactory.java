package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import ru.rdude.rpg.game.logic.data.AbilityData;
import ru.rdude.rpg.game.logic.data.PlayerClassData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.enums.EntityReferenceInfo;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.playerClass.Ability;
import ru.rdude.rpg.game.logic.playerClass.PlayerClass;
import ru.rdude.rpg.game.utils.Functions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static ru.rdude.rpg.game.logic.enums.EntityReferenceInfo.*;

public class AbilityTooltipInfoFactory {


    public List<Actor> get(Ability ability,
                           PlayerClass playerClass,
                           EntityReferenceInfo referenceInfo,
                           TooltipInfoHolder<?> infoHolder) {

        // if no info needed
        if (referenceInfo == NO) {
            return new ArrayList<>();
        }

        List<Actor> result = new ArrayList<>();
        AbilityData abilityData = ability.getAbilityData();
        PlayerClassData.AbilityEntry abilityEntry = playerClass.getClassData().getAbilityEntries().stream()
                .filter(entry -> entry.getAbilityData() == ability.getAbilityData().getGuid())
                .findAny()
                .orElse(null);
        if (abilityEntry == null) {
            return result;
        }

        // name
        if (referenceInfo == ALL || referenceInfo == NAME) {
            Label nameLabel = new Label(Functions.capitalize(abilityData.getName()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
            result.add(nameLabel);
        }

        // requirements
        if ((referenceInfo == ALL || referenceInfo == INTEGRATED)
                && !ability.isOpen()) {
            // abilities requirements
            AtomicBoolean hasAbilityRequirements = new AtomicBoolean(false);
            String requirementsString = "Required " + abilityEntry.getRequirements().entrySet().stream()
                    .filter(reqEntry -> playerClass.getAbilities().stream()
                            .filter(ab -> ab.getAbilityData().getGuid() == reqEntry.getKey())
                            .anyMatch(ab -> ab.getLvl() < reqEntry.getValue()))
                    .map(reqEntry -> {
                        String lvlReq = reqEntry.getValue() > 1 ? " level " + reqEntry.getValue() : "";
                        return AbilityData.getAbilityByGuid(reqEntry.getKey()).getName() + lvlReq;
                    })
                    .peek(s -> hasAbilityRequirements.set(true))
                    .collect(Collectors.joining(", "));
            if (hasAbilityRequirements.get()) {
                Label requirementsLabel = new Label(requirementsString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                requirementsLabel.addAction(Actions.color(Color.RED));
                result.add(requirementsLabel);
            }
            // class level requirements
            ability.getAbilityData().getLvl(ability.getLvl() + 1).ifPresent(abilityLevel -> {
                int classLvlReq = abilityLevel.getClassLvlRequirement();
                if (classLvlReq > 0 && playerClass.getLvl().value() < classLvlReq) {
                    Label label = new Label("Required class level " + classLvlReq, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                    label.addAction(Actions.color(Color.RED));
                    result.add(label);
                }
            });
        }

        // current lvl
        if (ability.getLvl() > 0 && (referenceInfo == ALL || referenceInfo == INTEGRATED)) {
            // passives
            if (!ability.getBuffsForCurrentLvl().isEmpty()) {
                result.add(new Label("Passive", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                ability.getBuffsForCurrentLvl()
                        .forEach(skillData -> {
                            infoHolder.getOption(PassiveSkills.class).add(skillData);
                            result.addAll(Game.getTooltipInfoFactory().get(skillData, INTEGRATED, infoHolder));
                        });
            }
            // actives
            if (!ability.getSkillsForCurrentLvl().isEmpty()) {
                if (!ability.getBuffsForCurrentLvl().isEmpty() || ability.getSkillsForCurrentLvl().size() > 1) {
                    result.add(new Label("Allows to use:", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                }
                EntityReferenceInfo skillReferenceInfo = ability.getBuffsForCurrentLvl().size() > 1 ? ALL : INTEGRATED;
                ability.getSkillsForCurrentLvl()
                        .forEach(skillData -> result.addAll(Game.getTooltipInfoFactory().get(skillData, skillReferenceInfo, infoHolder)));
            }
        }

        // next lvl
        ability.getAbilityData().getLvl(ability.getLvl() + 1).ifPresent(nextLvlAbility -> {
            if (ability.getLvl() >= 1) {
                result.add(new Label("Next level:", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
            }
            // passives
            if (!nextLvlAbility.getBuffs().isEmpty()) {
                result.add(new Label("Passive", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                nextLvlAbility.getBuffs().stream()
                        .map(SkillData::getSkillByGuid)
                        .forEach(skillData -> {
                            infoHolder.getOption(PassiveSkills.class).add(skillData);
                            result.addAll(Game.getTooltipInfoFactory().get(skillData, INTEGRATED, infoHolder));
                        });
            }
            // actives
            if (!nextLvlAbility.getSkills().isEmpty()) {
                if (!nextLvlAbility.getBuffs().isEmpty() || ability.getSkillsForCurrentLvl().size() > 1) {
                    result.add(new Label("Allows to use:", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
                }
                EntityReferenceInfo skillReferenceInfo = nextLvlAbility.getSkills().size() > 1 ? ALL : INTEGRATED;
                nextLvlAbility.getSkills().stream()
                        .map(SkillData::getSkillByGuid)
                        .forEach(skillData -> result.addAll(Game.getTooltipInfoFactory().get(skillData, skillReferenceInfo, infoHolder)));
            }
        });

        // description
        if (!abilityData.getDescription().isBlank() && (referenceInfo == ALL || referenceInfo == DESCRIPTION)) {
            Label description = new Label(abilityData.getDescription(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            result.add(description);
        }

        infoHolder.ignore(abilityData);

        return result;
    }

    public final static class PassiveSkills implements TooltipInfoHolder.SingleOption {

        private final Set<Long> skills = new HashSet<>();

        public void add(long guid) {
            skills.add(guid);
        }

        public void add(SkillData skillData) {
            add(skillData.getGuid());
        }

        public boolean isPassive(long guid) {
            return skills.contains(guid);
        }

        public boolean isPassive(SkillData skillData) {
            return isPassive(skillData.getGuid());
        }

        public boolean isNotPassive(long guid) {
            return !isPassive(guid);
        }

        public boolean isNotPassive(SkillData skillData) {
            return isNotPassive(skillData.getGuid());
        }

    }

}
