package ru.rdude.rpg.game.logic.gameStates;

import java.util.HashSet;
import java.util.Set;

public class GameStateHolder {

    private GameStateBase gameState;
    private Set<GameStateObserver> subscribers = new HashSet<>();

    public GameStateBase getGameState() {
        return gameState;
    }

    public void setGameState(GameStateBase gameState) {
        GameStateBase oldValue = this.gameState;
        this.gameState = gameState;
        notifySubscribers(oldValue, gameState);
    }

    public void subscribe(GameStateObserver subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(GameStateObserver subscriber) {
        subscribers.remove(subscriber);
    }

    public void clearSubscriptions() {
        subscribers.clear();
    }

    private void notifySubscribers(GameStateBase oldValue, GameStateBase newValue) {
        subscribers.forEach(subscriber -> subscriber.update(oldValue, newValue));
    }
}
