package ru.rdude.rpg.game.logic.gameStates;

import ru.rdude.rpg.game.utils.SubscribersManager;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("campSetup")
public class CampSetup implements GameStateObserver {

    private static final int BEFORE_NEXT_CAMP_ALLOWED = 3;

    private SubscribersManager<CampSetupObserver> subscribers = new SubscribersManager<>();
    private int beforeNextCampAllowed = BEFORE_NEXT_CAMP_ALLOWED;

    private CampSetup() {}

    public CampSetup(GameStateHolder gameStateHolder) {
        gameStateHolder.subscribe(this);
    }

    public boolean canSetUpCamp() {
        return beforeNextCampAllowed <= 0;
    }

    public void subscribe(CampSetupObserver subscriber) {
        subscribers.subscribe(subscriber);
    }

    public void unsubscribe(CampSetupObserver subscriber) {
        subscribers.unsubscribe(subscriber);
    }

    public void setBeforeNextCampAllowed(int value) {
        int oldValue = this.beforeNextCampAllowed;
        this.beforeNextCampAllowed = value;
        subscribers.notifySubscribers(sub -> sub.updateBeforeSetUpCampAllowed(this, oldValue, value));
    }

    public int getBeforeNextCampAllowed() {
        return beforeNextCampAllowed;
    }

    @Override
    public void update(GameStateBase oldValue, GameStateBase newValue) {
        if (oldValue instanceof Battle) {
            setBeforeNextCampAllowed(beforeNextCampAllowed - 1);
        }
        if (newValue instanceof Camp) {
            setBeforeNextCampAllowed(BEFORE_NEXT_CAMP_ALLOWED);
        }
    }
}
