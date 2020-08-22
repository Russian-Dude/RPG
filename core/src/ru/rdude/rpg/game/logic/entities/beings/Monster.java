package ru.rdude.rpg.game.logic.entities.beings;

import ru.rdude.rpg.game.logic.data.BeingData;
import ru.rdude.rpg.game.logic.enums.AttackType;

public class Monster extends Being {

    public Monster(BeingData beingData) {
        super(beingData);
    }

    @Override
    public AttackType getAttackType() {
        return null;
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
        return "default monster name";
    }
}
