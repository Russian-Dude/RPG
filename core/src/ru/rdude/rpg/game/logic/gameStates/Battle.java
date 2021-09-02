package ru.rdude.rpg.game.logic.gameStates;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.fasterxml.jackson.annotation.*;
import ru.rdude.rpg.game.battleVisual.BattleVisual;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.*;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.enums.GameState;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.time.TurnChangeObserver;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.utils.SubscribersManager;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;
import ru.rdude.rpg.game.visual.GameStateStage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonPolymorphicSubType("battle")
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public class Battle extends GameStateBase implements TurnChangeObserver, BeingActionObserver {

    @JsonIgnore private BattleVisual battleStage;
    private SubscribersManager<BattleObserver> subscribers;
    private final Party playerSide;
    private int cellX;
    private int cellY;
    private final Party enemySide;
    private boolean playersTurn = true;

    @JsonCreator
    private Battle(@JsonProperty("enemySide") Party enemySide,
                   @JsonProperty("playerSide") Party playerSide) {
        this.enemySide = enemySide;
        this.playerSide = playerSide;
    }

    public Battle(Party enemySide, Cell cell) {
        this.playerSide = Game.getCurrentGame().getCurrentPlayers();
        this.enemySide = enemySide;
        this.cellX = cell.getX();
        this.cellY = cell.getY();
        this.subscribers = new SubscribersManager<>();
        Game.getCurrentGame().getTurnsManager().subscribe(this);
        playerSide.getBeings().forEach(being -> being.subscribe(this));
        enemySide.getBeings().forEach(being -> being.subscribe(this));
    }

    public Party getEnemySide() {
        return enemySide;
    }

    public Party getAllySide(Being<?> of) {
        for (Being<?> being : playerSide.getBeings()) {
            if (of == being)
                return playerSide;
        }
        for (Being<?> being : enemySide.getBeings()) {
            if (of == being)
                return enemySide;
        }
        return null;
    }

    public Party getEnemySide(Being<?> of) {
        return getAllySide(of) == playerSide ? enemySide : playerSide;
    }

    public Party getCurrentSide() {
        return playersTurn ? playerSide : enemySide;
    }

    @Override
    public void turnUpdate() {
        // switch side:
        playersTurn = !playersTurn;
        if (!playersTurn) {
            playerSide.stream().forEach(being -> being.setReady(false));
            enemySide.forEach(being -> being.setReady(true));
            aiTurn();
        }
        else {
            enemySide.forEach(being -> being.setReady(false));
            playerSide.forEach(being -> being.setReady(true));
            updateCasts(playerSide);
        }
    }

    @Override
    public GameState getEnumValue() {
        return GameState.BATTLE;
    }

    @Override
    public GameStateStage getStage() {
        if (battleStage == null) {
            battleStage = new BattleVisual(Game.getCurrentGame().getGameMap().getGameMap().cell(cellX, cellY), this);
        }
        return battleStage;
    }

    private void aiTurn() {
        final RunnableAction aiTurnAction = Actions.run(() -> {
            // player's ai monsters
            playerSide.forEach(being -> {
                if (being instanceof Monster && being.isReady()) {
                    final Long skillGuid = Functions.randomWithWeights(((MonsterData) being.getEntityData()).getSkills());
                    final SkillData skill = Game.getEntityFactory().skills().describerToReal(skillGuid);
                    Game.getSkillUser().use(skill, being, skill.getMainTarget());
                }
            });
            // enemies cast
            List<Being<?>> wereCasting = enemySide.getBeings().stream()
                    .filter(Being::isCasting)
                    .collect(Collectors.toList());
            updateCasts(enemySide);
            // enemies main turn
            enemySide.forEach(enemy -> {
                if (enemy instanceof Monster && enemy.isReady() && !enemy.isCasting() && !wereCasting.contains(enemy)) {
                    final Long skillGuid = Functions.randomWithWeights(((MonsterData) enemy.getEntityData()).getSkills());
                    if (skillGuid != null) {
                        final SkillData skill = Game.getEntityFactory().skills().describerToReal(skillGuid);
                        Game.getSkillUser().use(skill, enemy, skill.getMainTarget());
                    }
                }
            });
            final RunnableAction startNextTurnAction = Actions.run(() -> Game.getCurrentGame().getTurnsManager().nextTurn());
            Game.getCurrentGame().getSkillsSequencer().add(startNextTurnAction);
        });
        Game.getCurrentGame().getSkillsSequencer().add(Actions.after(aiTurnAction));
    }

    private void updateCasts(Party party) {
        party.stream()
                .filter(Being::isCasting)
                .collect(Collectors.toList())
                .stream()
                .map(Being::getCast)
                .forEach(Game.getCurrentGame().getSkillsSequencer()::add);
    }

    private boolean checkLose() {
        return playerSide.getBeings()
                .stream()
                .noneMatch(Being::isAlive);
    }

    private boolean checkWin() {
        return enemySide.getBeings()
                .stream()
                .noneMatch(Being::isAlive);
    }

    public void win() {
        Game.getCurrentGame().getGameMap().getCellProperties(cellX, cellY).setMonsters(null);
        Game.getCurrentGame().getGameMap().getStage().removeMonsterFrom(cellX, cellY);
        unsubscribeFromAll();
        final Map<Being<?>, Double> expRewards = Game.getCurrentGame().getExpSpreader().spreadExp(enemySide);
        expRewards.forEach((player, exp) -> player.stats().lvl().exp().increase(exp));
        BattleAction.WIN.lastExpRewards = expRewards;
        subscribers.notifySubscribers(subscriber -> subscriber.update(BattleAction.WIN));
    }

    public void lose() {
        unsubscribeFromAll();
        subscribers.notifySubscribers(subscriber -> subscriber.update(BattleAction.LOSE));
    }

    public List<Item> createItemsReward() {
        return enemySide.getBeings().stream()
                .filter(being -> being instanceof Monster)
                .flatMap(being -> ((MonsterData) being.getEntityData()).getDrop().entrySet().stream())
                .filter(entry -> entry.getValue() >= Functions.random(100.0))
                .map(entry -> Game.getEntityFactory().items().get(ItemData.getItemDataByGuid(entry.getKey())))
                .collect(Collectors.toList());
    }

    private void unsubscribeFromAll() {
        Game.getCurrentGame().getTurnsManager().unsubscribe(this);
        enemySide.getBeings().forEach(being -> being.unsubscribe(this));
        playerSide.getBeings().forEach(being -> being.unsubscribe(this));
    }

    @Override
    public void update(BeingAction action, Being<?> being) {
        if (action.action() == BeingAction.Action.DIE) {
            final RunnableAction checkWinOrLose = Actions.run(() -> {
                if (checkLose()) {
                    lose();
                }
                else if (checkWin()) {
                    win();
                }
            });
            Game.getCurrentGame().getSkillsSequencer().add(checkWinOrLose);
        }
    }

    public void subscribe(BattleObserver subscriber) {
        subscribers.subscribe(subscriber);
    }

    public void unsubscribe(BattleObserver subscriber) {
        subscribers.unsubscribe(subscriber);
    }
}
