package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.quests.Quest;
import ru.rdude.rpg.game.logic.entities.quests.QuestsObserver;
import ru.rdude.rpg.game.logic.game.Game;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreType
public class QuestsJournal extends Window implements QuestsObserver {

    private final List<Quest> list;
    private final Array<Quest> quests;
    private final VerticalGroup selectedQuestHolder = new VerticalGroup();
    private final Map<Quest, QuestJournalEntry> questEntries = new HashMap<>();

    public QuestsJournal() {
        super("Quests", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        getTitleTable().padTop(20);
        defaults().padTop(25f).space(15f);
        selectedQuestHolder.align(Align.topLeft);

        // close button
        Button closeButton = new TextButton("X", UiData.DEFAULT_SKIN, UiData.NO_SQUARE_BUTTON_STYLE);
        getTitleTable().add(closeButton).padTop(25);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                QuestsJournal.this.setVisible(false);
            }
        });

        // list
        list = new List<>(UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        quests = new Array<>(false, 16);
        Game.getCurrentGame().getQuestHolder().getQuests().forEach(quests::add);
        list.setItems(quests);
        list.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedQuestHolder.clearChildren();
                Quest selected = list.getSelected();
                if (selected != null) {
                    QuestJournalEntry questJournalEntry = questEntries.get(selected);
                    if (questJournalEntry == null) {
                        questJournalEntry = new QuestJournalEntry(selected);
                        questEntries.put(selected, questJournalEntry);
                    }
                    selectedQuestHolder.addActor(questJournalEntry);
                }
            }
        });

        // add
        add(list).size(Gdx.graphics.getWidth() / 5f, Gdx.graphics.getHeight() / 1.5f);
        add(selectedQuestHolder).size(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 1.5f);

        // subscribe
        Game.getCurrentGame().getQuestHolder().subscribe(this);

        pack();

        if (quests.notEmpty()) {
            selectedQuestHolder.clearChildren();
            Quest selected = list.getSelected();
            if (selected != null) {
                QuestJournalEntry questJournalEntry = questEntries.get(selected);
                if (questJournalEntry == null) {
                    questJournalEntry = new QuestJournalEntry(selected);
                    questEntries.put(selected, questJournalEntry);
                }
                selectedQuestHolder.addActor(questJournalEntry);
            }
        }
    }

    @Override
    public void questsUpdate(boolean added, Quest quest) {
        if (added) {
            quests.add(quest);
        }
        else {
            quests.removeValue(quest, true);
        }
        list.setItems(quests);
    }
}
