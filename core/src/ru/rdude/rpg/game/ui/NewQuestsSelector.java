package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.data.QuestData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.quests.Quest;
import ru.rdude.rpg.game.logic.entities.quests.QuestEndLocation;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.objects.CityInside;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NewQuestsSelector extends Stage {

    private Map<QuestListEntry, QuestDescriptionEntry> descriptionEntries = new HashMap<>();
    private final List<QuestListEntry> list = new List<>(UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
    private final Array<QuestListEntry> quests = new Array<>(false, 16);
    private final QuestEndLocation endLocation;


    public NewQuestsSelector(Collection<QuestData> questData, Runnable backButtonExtraAction, QuestEndLocation location) {
        super();
        this.endLocation = location;
        final Table mainTable = new Table(UiData.DEFAULT_SKIN);
        mainTable.background(UiData.SEMI_TRANSPARENT_BACKGROUND);
        mainTable.defaults().space(15f);

        // list
        final VerticalGroup selectedQuestHolder = new VerticalGroup();
        questData.forEach(data -> quests.add(new QuestListEntry(data)));
        list.setItems(quests);
        list.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedQuestHolder.clearChildren();
                QuestListEntry selected = list.getSelected();
                if (selected != null) {
                    QuestDescriptionEntry descriptionEntry = descriptionEntries.get(selected);
                    if (descriptionEntry == null) {
                        descriptionEntry = new QuestDescriptionEntry(selected);
                        descriptionEntries.put(selected, descriptionEntry);
                    }
                    selectedQuestHolder.addActor(descriptionEntry.descriptionActor);
                }
            }
        });

        // back button
        final Button backButton = new TextButton("Back", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Game.getGameVisual().backMenuStage();
                if (backButtonExtraAction != null) {
                    backButtonExtraAction.run();
                }
            }
        });

        // add
        mainTable.add(list).size(Gdx.graphics.getWidth() / 5f, Gdx.graphics.getHeight() / 1.5f);
        mainTable.add(selectedQuestHolder).size(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 1.5f);
        mainTable.row();
        mainTable.add(backButton);

        mainTable.pack();
        mainTable.setY((Gdx.graphics.getHeight() - mainTable.getHeight()) / 2);
        mainTable.setX((Gdx.graphics.getWidth() - mainTable.getWidth()) / 2);
        addActor(mainTable);
    }

    public void setQuests(Collection<QuestData> questData) {
        ArrayList<QuestData> keep = new ArrayList<>();
        ArrayList<QuestListEntry> remove = new ArrayList<>();
        for (QuestListEntry listEntry : quests) {
            if (questData.contains(listEntry.questData)) {
                keep.add(listEntry.questData);
            }
            else {
                remove.add(listEntry);
            }
        }
        remove.forEach(entry -> quests.removeValue(entry, true));
        questData.stream().filter(data -> !keep.contains(data))
                .forEach(data -> quests.add(new QuestListEntry(data)));
        list.setItems(quests);
    }

    private final class QuestListEntry {

        private final QuestData questData;

        QuestListEntry(QuestData questData) {
            this.questData = questData;
        }

        @Override
        public String toString() {
            return questData.getName();
        }
    }

    private final class QuestDescriptionEntry {

        private final Table descriptionActor = new Table(UiData.DEFAULT_SKIN);
        private final QuestListEntry questListEntry;

        QuestDescriptionEntry(QuestListEntry questListEntry) {
            this.questListEntry = questListEntry;
            final QuestData questData = questListEntry.questData;
            descriptionActor.defaults().space(10f);

            // description
            final Label description = new Label(questData.getDescription(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            description.setWrap(true);
            descriptionActor.add(description).row();

            // requirements
            // items
            questData.getCollectItems().forEach((guid, amount) -> {
                final StringBuilder stringBuilder = new StringBuilder("Collect ");
                stringBuilder
                        .append(amount + " ")
                        .append(ItemData.getItemDataByGuid(guid).getName());
                final Label collect = new Label(stringBuilder.toString(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                descriptionActor.add(collect);
                descriptionActor.row();
            });
            // items
            questData.getKillMonsters().forEach((guid, amount) -> {
                final StringBuilder stringBuilder = new StringBuilder("Kill ");
                stringBuilder
                        .append(amount + " ")
                        .append(MonsterData.getMonsterByGuid(guid).getName());
                final Label kill = new Label(stringBuilder.toString(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                descriptionActor.add(kill);
                descriptionActor.row();
            });
            // skills
            questData.getUseSkills().forEach((guid, amount) -> {
                final StringBuilder stringBuilder = new StringBuilder("Use ");
                stringBuilder
                        .append(amount + " ")
                        .append(SkillData.getSkillByGuid(guid).getName());
                final Label use = new Label(stringBuilder.toString(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                descriptionActor.add(use);
                descriptionActor.row();
            });
            // gold
            if (questData.getCollectGold() > 0) {
                descriptionActor.add(new Label("Collect " + questData.getCollectGold() + " gold", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                descriptionActor.row();
            }
            // take questButton
            final Button takeQuestButton = new TextButton("Take quest", UiData.DEFAULT_SKIN, UiData.YES_BUTTON_STYLE);
            takeQuestButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    Game.getCurrentGame().getQuestHolder().add(new Quest(questData, endLocation));
                    quests.removeValue(QuestDescriptionEntry.this.questListEntry, true);
                    list.setItems(quests);
                    if (endLocation instanceof CityInside) {
                        ((CityInside) endLocation).removeQuest(QuestDescriptionEntry.this.questListEntry.questData);
                    }
                    if (quests.size > 0) {
                        list.setSelected(quests.first());
                    }
                    else {
                        list.setSelected(null);
                    }
                }
            });
            descriptionActor.add(takeQuestButton);
            descriptionActor.row();
            descriptionActor.pack();
        }

    }
}
