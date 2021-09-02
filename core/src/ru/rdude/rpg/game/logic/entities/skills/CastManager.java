package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.utils.SubscribersManager;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("castManager")
public class CastManager implements CastObserver {

    private SubscribersManager<CastObserver> subscribersManager = new SubscribersManager<>();
    private Cast cast;

    public Cast getCast() {
        return cast;
    }

    public void setCast(Cast cast) {
        if (this.cast != null && this.cast != cast) {
            this.cast.unsubscribe(this);
        }
        this.cast = cast;
        if (cast != null) {
            cast.subscribe(this);
        }
        subscribersManager.notifySubscribers(subscriber -> subscriber.castUpdate(cast));
    }

    public void subscribe(CastObserver subscriber) {
        subscribersManager.subscribe(subscriber);
    }

    public void unsubscribe(CastObserver subscriber) {
        subscribersManager.unsubscribe(subscriber);
    }

    public void update() {
        if (cast != null) {
            cast.update();
        }
    }

    @Override
    public void castUpdate(Cast cast) {
        subscribersManager.notifySubscribers(subscriber -> subscriber.castUpdate(cast));
        if (cast.isComplete()) {
            setCast(null);
        }
    }
}
