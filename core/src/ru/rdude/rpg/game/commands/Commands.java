package ru.rdude.rpg.game.commands;

import com.badlogic.gdx.scenes.scene2d.Actor;
import ru.rdude.rpg.game.battleVisual.BattleVisual;
import ru.rdude.rpg.game.logic.data.QuestData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.quests.Quest;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.mapVisual.MapStage;
import ru.rdude.rpg.game.ui.PlayerVisual;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Commands {

    NO_SUCH_COMMAND
            (string -> Game.getCurrentGame().getGameLogger().log("Command \"" + string + "\" does not exist")),

    WIN_BATTLE
            (string -> {
                final GameStateBase currentGameState = Game.getCurrentGame().getCurrentGameState();
                if (currentGameState instanceof Battle) {
                    ((BattleVisual) Game.getGameVisual().getCurrentGameStateStage()).getVisualBeings()
                            .stream()
                            .filter(visualBeing -> ((Battle) currentGameState).getEnemySide().getBeings().contains(visualBeing.getBeing()))
                            .forEach(visualBeing -> ((Actor) visualBeing).remove());
                    ((Battle) currentGameState).win();
                }
            }),

    LOSE_BATTLE
            (string -> {
                final GameStateBase currentGameState = Game.getCurrentGame().getCurrentGameState();
                if (currentGameState instanceof Battle) {
                    ((Battle) currentGameState).lose();
                }
            }),

    START_ALL_QUESTS
            (string -> {
                QuestData.getQuests().values().forEach(questData -> Game.getCurrentGame().getQuestHolder().add(new Quest(questData, null)));
            }),

    END_FIRST_COMPLETED_QUEST
            (string -> {
                Game.getCurrentGame().getQuestHolder().getQuests().stream()
                        .filter(Quest::isComplete)
                        .findAny()
                        .ifPresent(completedQuest -> Game.getQuestRewarder().startRewarding(completedQuest));
            }),

    ITEMS_OF(
            string -> {
                try {
                    String value = string.split("\\s")[1];
                    int index = Integer.parseInt(value);
                    Being<?> being = Game.getCurrentGame().getCurrentPlayers().getBeings().get(index);
                    String items = Stream.of(being.backpack().getSlots(), being.equipment().getSlots())
                            .flatMap(Collection::stream)
                            .filter(slot -> !slot.isEmpty())
                            .map(slot -> slot.getEntity().getName() + "(" + slot.getEntity().getAmount() + ")")
                            .collect(Collectors.joining(", "));
                    Game.getCurrentGame().getGameLogger().log(
                            "Items of " + being.getName() + ": " + items);
                }
                catch (Exception ignored) {
                }
            }
    ),

    IS_READY(
            string -> {
                try {
                    String value = string.split("\\s")[1];
                    int index = Integer.parseInt(value);
                    Being<?> being = Game.getCurrentGame().getCurrentPlayers().getBeings().get(index);
                    Game.getCurrentGame().getGameLogger().log(
                            being.getName() + (being.isReady() ? " ready" : " not ready"));
                }
                catch (Exception ignored) {
                }
            }
    ),

    RECEIVE_GOLD(
            string -> {
                try {
                    String value = string.split("\\s")[1];
                    int amount = Integer.parseInt(value);
                    Game.getCurrentGame().getGold().increase(amount);
                }
                catch (Exception ignored) {
                }
            }
    );

    private final Consumer<String> action;

    Commands(Consumer<String> action) {
        this.action = action;
    }

    public void accept(String string) {
        action.accept(string);
    }
}
