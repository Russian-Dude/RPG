package ru.rdude.rpg.game.logic.gold;

import ru.rdude.rpg.game.utils.SubscribersManager;

public class GoldHolder {

    private SubscribersManager<GoldObserver> subscribersManager = new SubscribersManager<>();

    private int amount = 0;

    private void set(int amount) {
        final int oldValue = this.amount;
        this.amount = Math.max(amount, 0);
        subscribersManager.notifySubscribers(sub -> sub.updateGold(oldValue, amount));
    }

    public void increase(int amount) {
        set(this.amount + amount);
    }

    public void decrease(int amount) {
        set(this.amount - amount);
    }

    public int getAmount() {
        return amount;
    }

    public void subscribe(GoldObserver subscriber) {
        subscribersManager.subscribe(subscriber);
    }

    public void unsubscribe(GoldObserver subscriber) {
        subscribersManager.unsubscribe(subscriber);
    }

}
