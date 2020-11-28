package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.logic.enums.Size;

import java.util.HashSet;

public class BeingDataZglushka {

    public BeingData beingData;

    public BeingDataZglushka() {
        beingData = new MonsterData(123456789876L);
        beingData.setBeingTypes(new HashSet<>());
        beingData.setElements(new HashSet<>());
        beingData.setSize(Size.MEDIUM);

    }
}
