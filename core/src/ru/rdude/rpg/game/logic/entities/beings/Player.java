package ru.rdude.rpg.game.logic.entities.beings;

import ru.rdude.rpg.game.logic.data.PlayerData;
import ru.rdude.rpg.game.logic.entities.states.StateHolder;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.enums.BeingType;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.enums.Size;
import ru.rdude.rpg.game.logic.stats.Stats;

import java.util.HashSet;

public class Player extends Being {

    public Player() {
        this(new PlayerData());
    }

    public Player(PlayerData playerData) {
        super(playerData);
        stats = new Stats(true);
        beingTypes = new StateHolder<>(BeingType.HUMAN);
        elements = new StateHolder<>(Element.NEUTRAL);
        size = new StateHolder<>(Size.MEDIUM);
        buffs = new HashSet<>();
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
}
