package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.data.EntityData;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.quests.Quest;
import ru.rdude.rpg.game.logic.entities.quests.QuestAmountObserver;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gold.GoldObserver;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreType
public class QuestJournalEntry extends VerticalGroup implements QuestAmountObserver, GoldObserver {

    private final Quest quest;
    private final Map<Quest.Amount, Label> amountLabels = new HashMap<>();
    private Label goldRequirementLabel;
    private final Table requirementsTable = new Table(UiData.DEFAULT_SKIN);

    public QuestJournalEntry(Quest quest) {
        super();
        this.quest = quest;

        space(15f);

        // description
        String description = quest.getQuestData().getDescription();
        if (description != null && !description.isBlank()) {
            final Label descriptionLabel = new Label(description, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            descriptionLabel.setWrap(true);
            descriptionLabel.setWidth(Gdx.graphics.getWidth() / 3f);
            descriptionLabel.setAlignment(Align.center);
            Table descriptionTable = new Table(UiData.DEFAULT_SKIN);
            descriptionTable.add(descriptionLabel).width(Gdx.graphics.getWidth() / 3f);
            descriptionTable.pack();
            addActor(descriptionTable);
        }

        addActor(requirementsTable);

        // requirements
        // items
        quest.getCollectItems().forEach((guid, amountEntry) -> addAmountEntry(ItemData.getItemDataByGuid(guid), amountEntry));
        // monsters
        quest.getKillMonsters().forEach((guid, amountEntry) -> addAmountEntry(MonsterData.getMonsterByGuid(guid), amountEntry));
        // skills
        quest.getUseSkills().forEach((guid, amountEntry) -> addAmountEntry(SkillData.getSkillByGuid(guid), amountEntry));
        // gold
        if (quest.getQuestData().getCollectGold() > 0) {
            requirementsTable.add(new Label("Collect gold ", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
            goldRequirementLabel = new Label(Math.min(Game.getCurrentGame().getGold().getAmount(), quest.getQuestData().getCollectGold()) + " / " + quest.getQuestData().getCollectGold(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            goldRequirementLabel.setAlignment(Align.left);
            requirementsTable.add(goldRequirementLabel);
            Game.getCurrentGame().getGold().subscribe(this);
            requirementsTable.row();
        }
        setSize(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 1.5f);
        requirementsTable.pack();
    }

    private void addAmountEntry(EntityData entityData, Quest.Amount amountEntry) {
        String name;
        String actionString;
        // name
        if (entityData.isDescriber() || entityData.getDescription().isBlank()) {
            name = entityData.getName();
        }
        else {
            name = entityData.getDescription();
        }

        // action
        if (entityData instanceof MonsterData) {
            actionString = "Kill ";
        }
        else if (entityData instanceof ItemData) {
            actionString = "Collect ";
        }
        else if (entityData instanceof SkillData) {
            actionString = "Use ";
        }
        else {
            actionString = "";
        }

        Label nameLabel = new Label(actionString + name + " ", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        Label amountLabel = new Label(Math.min(amountEntry.getCurrent(), amountEntry.getRequired()) + " / " + amountEntry.getRequired(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        amountLabels.put(amountEntry, amountLabel);
        amountEntry.subscribe(this);
        requirementsTable.add(nameLabel);
        requirementsTable.add(amountLabel);
        requirementsTable.row();
    }

    @Override
    public void onQuestAmountUpdate(Quest.Amount amount, int oldValue, int newValue) {
        Label label = amountLabels.get(amount);
        if (label != null) {
            label.setText(Math.min(newValue, amount.getRequired()) + " / " + amount.getRequired());
        }
    }

    @Override
    public void updateGold(int oldValue, int newValue) {
        if (goldRequirementLabel != null) {
            goldRequirementLabel.setText(((int) Math.min(newValue, quest.getQuestData().getCollectGold())) + " / " + quest.getQuestData().getCollectGold());
        }
    }
}
