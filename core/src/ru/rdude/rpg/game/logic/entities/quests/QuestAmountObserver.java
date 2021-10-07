package ru.rdude.rpg.game.logic.entities.quests;

public interface QuestAmountObserver {

    void onQuestAmountUpdate(Quest.Amount amount, int oldValue, int newValue);

}
