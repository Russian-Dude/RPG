package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.items.holders.EquipmentSlotsHolder;

import java.util.ArrayList;
import java.util.List;

public class EquipmentWindow extends Window {

    private EquipmentSlotsHolder equipment;

    private VerticalGroup leftGroup;
    private VerticalGroup rightGroup;
    private Image dude;

    public EquipmentWindow(Being being) {
        super(being.getName(), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        equipment = being.equipment();
        padTop(35);
        leftGroup = new VerticalGroup();
        rightGroup = new VerticalGroup();
        dude = new Image(UiData.DEFAULT_SKIN.getDrawable("Dude"));
        leftGroup.addActor(new ItemSlotVisual(equipment.helmet()));
        leftGroup.addActor(new ItemSlotVisual(equipment.rightHand()));
        leftGroup.addActor(new ItemSlotVisual(equipment.armor()));
        leftGroup.addActor(new ItemSlotVisual(equipment.pants()));
        leftGroup.addActor(new ItemSlotVisual(equipment.boots()));
        rightGroup.addActor(new ItemSlotVisual(equipment.necklace()));
        rightGroup.addActor(new ItemSlotVisual(equipment.leftHand()));
        rightGroup.addActor(new ItemSlotVisual(equipment.gloves()));
        rightGroup.addActor(new ItemSlotVisual(equipment.jewelry1()));
        rightGroup.addActor(new ItemSlotVisual(equipment.jewelry2()));
        leftGroup.setSize(leftGroup.getPrefWidth(), leftGroup.getPrefHeight());
        rightGroup.setSize(rightGroup.getPrefWidth(), rightGroup.getPrefHeight());
        add(leftGroup, dude, rightGroup);
        pack();
    }
}
