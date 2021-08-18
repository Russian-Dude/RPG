package ru.rdude.rpg.game.logic.entities.beings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.data.PlayerData;
import ru.rdude.rpg.game.logic.entities.states.StateHolder;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.enums.BeingType;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.enums.Size;
import ru.rdude.rpg.game.logic.stats.Stats;
import ru.rdude.rpg.game.utils.SubscribersManager;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.HashSet;

@JsonIgnoreProperties("entityData")
@JsonPolymorphicSubType("player")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Player extends Being<PlayerData> {

    private SubscribersManager<PlayerReadyObserver> isReadyObservers;

    public Player() {
        this(new PlayerData());
    }

    @JsonCreator
    public Player(@JsonProperty("playerData") PlayerData playerData) {
        super(playerData);
        isReadyObservers = new SubscribersManager<>();
        stats = new Stats(true);
        beingTypes = new StateHolder<>(BeingType.HUMAN);
        elements = new StateHolder<>(Element.NEUTRAL);
        size = new StateHolder<>(Size.MEDIUM);
        buffs = new HashSet<>();
    }

    @JsonProperty("playerData")
    private PlayerData getPlayerData() {
        return entityData;
    }

    @Override
    protected PlayerData getDataByGuid(long guid) {
        return null;
    }

    @Override
    public AttackType getAttackType() {
        return equipment.attackType();
    }

    @Override
    public boolean canBlock() {
        // TODO: 24.05.2021 can player block
        return false;
    }

    @Override
    public boolean canParry() {
        // TODO: 24.05.2021 can player parry
        return false;
    }

    @Override
    public void setReady(boolean ready) {
        super.setReady(ready);
        isReadyObservers.notifySubscribers(observer -> observer.update(this, isReady()));
    }

    public void subscribe(PlayerReadyObserver playerReadyObserver) {
        isReadyObservers.subscribe(playerReadyObserver);
    }

    public void unsubscribe(PlayerReadyObserver playerReadyObserver) {
        isReadyObservers.unsubscribe(playerReadyObserver);
    }
}
