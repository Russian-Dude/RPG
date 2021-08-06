package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.enums.EntityReferenceInfo;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.mapVisual.MonstersOnMap;
import ru.rdude.rpg.game.utils.Functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

final class MonsterTooltipInfoFactory {

    public List<Actor> getDescriber(MonsterData monsterData,
                                    EntityReferenceInfo referenceInfo,
                                    TooltipInfoHolder<?> infoHolder) {
        // if no info needed
        if (referenceInfo == EntityReferenceInfo.NO) {
            return new ArrayList<>();
        }

        List<Actor> result = new ArrayList<>();

        if (referenceInfo == EntityReferenceInfo.NAME || referenceInfo == EntityReferenceInfo.ALL) {
            result.add(new Label(Functions.capitalize(monsterData.getName()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        }

        List<Enum<?>> enums = new ArrayList<>();
        boolean stringEndsWithMonster = true;

        if (monsterData.getSize() != null && (referenceInfo == EntityReferenceInfo.INTEGRATED || referenceInfo == EntityReferenceInfo.ALL)) {
            enums.add(monsterData.getSize());
        }

        if (!monsterData.getElements().isEmpty() && (referenceInfo == EntityReferenceInfo.INTEGRATED || referenceInfo == EntityReferenceInfo.ALL)) {
            enums.addAll(monsterData.getElements());
        }

        if (!monsterData.getBeingTypes().isEmpty() && (referenceInfo == EntityReferenceInfo.INTEGRATED || referenceInfo == EntityReferenceInfo.ALL)) {
            stringEndsWithMonster = false;
            enums.addAll(monsterData.getBeingTypes());
        }

        if (!enums.isEmpty()) {
            String enumsString = enums.stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            String end = stringEndsWithMonster ? " monster" : "";
            result.add(new Label("Random " + enumsString + end, UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        }

        if (referenceInfo == EntityReferenceInfo.INTEGRATED || referenceInfo == EntityReferenceInfo.ALL) {
            String levels = Functions.trimDouble(monsterData.getMinLvl()) + " - "
                    + Functions.trimDouble(monsterData.getMaxLvl()) + " level";
            result.add(new Label(levels, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        }

        if (monsterData.getDescription() != null && !monsterData.getDescription().isEmpty() &&
                (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.DESCRIPTION)) {
            result.add(new Label(monsterData.getDescription(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        }

        infoHolder.ignore(monsterData);

        return result;
    }

    public List<Actor> getConcrete(MonsterData monsterData,
                                   EntityReferenceInfo referenceInfo,
                                   TooltipInfoHolder<?> infoHolder) {
        // if no info needed
        if (referenceInfo == EntityReferenceInfo.NO) {
            return new ArrayList<>();
        }

        List<Actor> result = new ArrayList<>();

        if (referenceInfo == EntityReferenceInfo.NAME || referenceInfo == EntityReferenceInfo.ALL) {
            result.add(new Label(Functions.capitalize(monsterData.getName()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        }

        List<Enum<?>> enums = new ArrayList<>();
        boolean stringEndsWithMonster = true;

        if (monsterData.getSize() != null && (referenceInfo == EntityReferenceInfo.INTEGRATED || referenceInfo == EntityReferenceInfo.ALL)) {
            enums.add(monsterData.getSize());
        }

        if (!monsterData.getElements().isEmpty() && (referenceInfo == EntityReferenceInfo.INTEGRATED || referenceInfo == EntityReferenceInfo.ALL)) {
            enums.addAll(monsterData.getElements());
        }

        if (!monsterData.getBeingTypes().isEmpty() && (referenceInfo == EntityReferenceInfo.INTEGRATED || referenceInfo == EntityReferenceInfo.ALL)) {
            stringEndsWithMonster = false;
            enums.addAll(monsterData.getBeingTypes());
        }

        if (!enums.isEmpty()) {
            String enumsString = enums.stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            String end = stringEndsWithMonster ? " monster" : "";
            result.add(new Label(enumsString + end, UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        }

        if (monsterData.getDescription() != null && !monsterData.getDescription().isEmpty() &&
                (referenceInfo == EntityReferenceInfo.ALL || referenceInfo == EntityReferenceInfo.DESCRIPTION)) {
            result.add(new Label(monsterData.getDescription(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        }

        infoHolder.ignore(monsterData);

        return result;
    }

    public List<Actor> getOnMap(Map.MonstersOnCell monsters) {
        int minLvl = Integer.MAX_VALUE;
        int maxLvl = 0;
        final HashMap<String, Integer> monstersCount = new HashMap<>();
        for (Map.MonstersOnCell.Monster monster : monsters.getMonsters()) {
            minLvl = Math.min(minLvl, monster.lvl);
            maxLvl = Math.max(maxLvl, monster.lvl);
            monstersCount.merge(MonsterData.getMonsterByGuid(monster.guid).getName(), 1, Integer::sum);
        }
        final String names = monstersCount.entrySet().stream()
                .map(entry -> entry.getKey() + (entry.getValue() > 1 ? " (" + entry.getValue() + ")" : ""))
                .collect(Collectors.joining(", "));
        final Label namesLabel = new Label(names, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        String levels = monsters.getMonsters().size() > 1 && minLvl != maxLvl ? "Levels " : "Level ";
        levels += minLvl == maxLvl ? String.valueOf(minLvl) : minLvl + " - " + maxLvl;
        final Label levelsLabel = new Label(levels, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        return List.of(namesLabel, levelsLabel);
    }
}
