package ru.rdude.rpg.game.logic.gameStates;

import ru.rdude.rpg.game.utils.SubscribersManager;

public class GameStateHolder {

    private GameStateBase gameState;
    private final SubscribersManager<GameStateObserver> subscribers = new SubscribersManager<>();

    public GameStateBase getGameState() {
        return gameState;
    }

    public void setGameState(GameStateBase gameState) {
        GameStateBase oldValue = this.gameState;
        this.gameState = gameState;
        notifySubscribers(oldValue, gameState);
    }

    public void subscribe(GameStateObserver subscriber) {
        subscribers.subscribe(subscriber);
    }

    public void unsubscribe(GameStateObserver subscriber) {
        subscribers.unsubscribe(subscriber);
    }

    private void notifySubscribers(GameStateBase oldValue, GameStateBase newValue) {
        subscribers.notifySubscribers(subscriber -> subscriber.update(oldValue, newValue));
    }
}
