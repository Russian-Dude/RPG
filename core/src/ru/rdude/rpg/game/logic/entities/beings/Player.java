package ru.rdude.rpg.game.logic.entities.beings;

import ru.rdude.rpg.game.logic.data.BeingDataZglushka;
import ru.rdude.rpg.game.logic.entities.states.StateHolder;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.enums.BeingType;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.enums.Size;
import ru.rdude.rpg.game.logic.stats.Stats;

import java.util.HashSet;

public class Player extends Being {

    private String name;

    public Player() {
        super(new BeingDataZglushka().beingData);
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
        return false;
    }

    @Override
    public boolean canParry() {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.isBlank() ? "Default Player Name" : name;
    }
}
