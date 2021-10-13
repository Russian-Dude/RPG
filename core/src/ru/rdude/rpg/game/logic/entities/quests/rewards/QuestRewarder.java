package ru.rdude.rpg.game.logic.entities.quests.rewards;

import ru.rdude.rpg.game.logic.data.QuestData;
import ru.rdude.rpg.game.logic.entities.quests.Quest;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.ui.QuestRewardTargetRequestStage;
import ru.rdude.rpg.game.ui.QuestRewardsStage;

import java.util.LinkedList;
import java.util.Queue;

public class QuestRewarder {

    private Quest quest;
    private final QuestRewardCreator questRewardCreator = new QuestRewardCreator();

    private final Queue<QuestReward<?>> needToSelect = new LinkedList<>();
    private final Queue<Quest> completedQuestsQueue = new LinkedList<>();
    private QuestRewards currentQuestRewards;

    public void startRewarding(Quest quest) {
        if (this.quest != null) {
            completedQuestsQueue.add(quest);
            return;
        }
        this.quest = quest;
        this.currentQuestRewards = questRewardCreator.create(quest);
        this.needToSelect.addAll(currentQuestRewards.getRewardsThatNeedToBeSelected());
        continueRewarding();
    }

    public void continueRewarding() {
        if (needToSelect.isEmpty()) {
            endRewarding();
            return;
        }
        QuestRewardTargetRequestStage.getInstance().setCurrentReward(needToSelect.poll());
        Game.getGameVisual().setMenuStage(QuestRewardTargetRequestStage.getInstance());
    }

    public void endRewarding() {
        currentQuestRewards.getAllRewards().forEach(QuestReward::reward);
        Game.getGameVisual().setMenuStage(new QuestRewardsStage(quest, currentQuestRewards));
        Game.getCurrentGame().getQuestHolder().remove(quest);
        quest.getQuestData().getStartQuests()
                .forEach(guid -> Game.getCurrentGame().getQuestHolder().add(new Quest(QuestData.getQuestByGuid(guid), null)));
        quest = null;
        currentQuestRewards = null;
        // TODO: 30.09.2021 add start event on end quest
        if (!completedQuestsQueue.isEmpty()) {
            startRewarding(completedQuestsQueue.poll());
        }
    }

    /*
    private Long startEvent = null;
     */


}
