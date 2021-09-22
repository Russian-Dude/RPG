package ru.rdude.rpg.game.logic.entities.beings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.data.PlayerClassData;
import ru.rdude.rpg.game.logic.data.PlayerData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.EntityReceiver;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.entities.states.StateHolder;
import ru.rdude.rpg.game.logic.enums.*;
import ru.rdude.rpg.game.logic.playerClass.Ability;
import ru.rdude.rpg.game.logic.playerClass.AbilityObserver;
import ru.rdude.rpg.game.logic.playerClass.CurrentPlayerClassObserver;
import ru.rdude.rpg.game.logic.playerClass.PlayerClass;
import ru.rdude.rpg.game.logic.statistics.PlayerStatistics;
import ru.rdude.rpg.game.logic.stats.Stats;
import ru.rdude.rpg.game.utils.SubscribersManager;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@JsonIgnoreProperties("entityData")
@JsonPolymorphicSubType("player")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Player extends Being<PlayerData> implements AbilityObserver {

    private SubscribersManager<PlayerReadyObserver> isReadyObservers;
    private SubscribersManager<CurrentPlayerClassObserver> currentClassObservers;

    private PlayerClass currentClass;
    private Set<PlayerClass> allClasses;
    private PlayerStatistics playerStatistics;

    public Player() {
        this(new PlayerData());
    }

    public Player(PlayerData playerData) {
        super(playerData);
        isReadyObservers = new SubscribersManager<>();
        currentClassObservers = new SubscribersManager<>();
        stats = new Stats(true);
        beingTypes = new StateHolder<>(BeingType.HUMAN);
        elements = new StateHolder<>(Element.NEUTRAL);
        size = new StateHolder<>(Size.MEDIUM);
        buffs = new HashSet<>();
        playerStatistics = new PlayerStatistics();
        subscribe(playerStatistics);
        allClasses = PlayerClassData.getClasses().values().stream()
                .map(PlayerClass::new)
                .collect(Collectors.toSet());
        allClasses.forEach(playerClass -> playerStatistics.subscribe(playerClass));
        allClasses.stream()
                .flatMap(playerClass -> playerClass.getAbilities().stream())
                .forEach(ability -> ability.subscribe(this));
    }

    @JsonCreator
    public Player(@JsonProperty("playerData") PlayerData playerData, @JsonProperty("currentClass") PlayerClass currentClass) {
        super(playerData);
        this.currentClass = currentClass;
    }

    public PlayerClass getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(PlayerClass currentClass) {
        PlayerClass oldValue = this.currentClass;
        this.currentClass = currentClass;
        if (oldValue != null) {
            for (Ability ability : oldValue.getAbilities()) {
                // remove buffs from old class
                getBuffs().stream()
                        .filter(buff -> buff.getType() == Buff.Type.ABILITY)
                        .filter(buff -> ability.getBuffsForCurrentLvl().contains(buff.getEntityData()))
                        .collect(Collectors.toSet())
                        .forEach(Buff::remove);

                // remove usable skills from previous lvl
                getAvailableSkills().removeAll(ability.getSkillsForCurrentLvl());
            }
        }
        for (Ability ability : currentClass.getAbilities()) {
            // add buffs for new class
            ability.getBuffsForCurrentLvl().stream()
                    .map(skillData -> new Buff(skillData, this, this, null, Buff.Type.ABILITY))
                    .forEach(buff ->  EntityReceiver.buff(this, buff));
            // add usable skills from new lvl
            getAvailableSkills().addAll(ability.getSkillsForCurrentLvl());
        }
        currentClassObservers.notifySubscribers(sub -> sub.currentPlayerClassUpdate(oldValue, currentClass));
    }

    public Set<PlayerClass> getAllClasses() {
        return allClasses;
    }

    public PlayerStatistics getPlayerStatistics() {
        return playerStatistics;
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
        return (!equipment.leftHand().isEmpty() && equipment.leftHand().getEntity().getEntityData().getItemType().equals(ItemType.SHIELD))
                || (!equipment.rightHand().isEmpty() && equipment.rightHand().getEntity().getEntityData().getItemType().equals(ItemType.SHIELD));
    }

    @Override
    public boolean canParry() {
        return (!equipment.leftHand().isEmpty() && equipment.leftHand().getEntity().getEntityData().getItemMainType().equals(ItemMainType.WEAPON))
                || (!equipment.rightHand().isEmpty() && equipment.rightHand().getEntity().getEntityData().getItemMainType().equals(ItemMainType.WEAPON));
    }

    @Override
    public void setReady(boolean ready) {
        super.setReady(ready);
        isReadyObservers.notifySubscribers(observer -> observer.update(this, isReady()));
    }

    public void subscribe(PlayerReadyObserver playerReadyObserver) {
        isReadyObservers.subscribe(playerReadyObserver);
    }

    public void subscribe(CurrentPlayerClassObserver currentPlayerClassObserver) {
        currentClassObservers.subscribe(currentPlayerClassObserver);
    }

    public void unsubscribe(PlayerReadyObserver playerReadyObserver) {
        isReadyObservers.unsubscribe(playerReadyObserver);
    }

    public void unsubscribe(CurrentPlayerClassObserver currentPlayerClassObserver) {
        currentClassObservers.unsubscribe(currentPlayerClassObserver);
    }

    @Override
    public void updateAbility(Ability ability, boolean open, int oldLvl, int newLvl) {
        if (oldLvl == newLvl) {
            return;
        }
        // remove buffs from previous level
        getBuffs().stream()
                .filter(buff -> buff.getType() == Buff.Type.ABILITY)
                .filter(buff -> ability.getAbilityData().getLvl(oldLvl).get().getBuffs().contains(buff.getEntityData().getGuid()))
                .collect(Collectors.toSet())
                .forEach(Buff::remove);
        // add buffs for new lvl
        ability.getAbilityData().getLvl(newLvl).get().getBuffs().stream()
                .map(SkillData::getSkillByGuid)
                .map(skillData -> new Buff(skillData, this, this, null, Buff.Type.ABILITY))
                .forEach(buff ->  EntityReceiver.buff(this, buff));
        // remove usable skills from previous lvl
        getAvailableSkills().removeAllByGuid(ability.getAbilityData().getLvl(oldLvl).get().getSkills());
        // add usable skills from new lvl
        getAvailableSkills().addAllByGuid(ability.getAbilityData().getLvl(newLvl).get().getSkills());
    }
}
