package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.logic.enums.BeingType;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.enums.Size;

import java.util.HashSet;
import java.util.Set;

public abstract class BeingData extends EntityData {

    private Set<BeingType> beingTypes = new HashSet<>();
    private Set<Element> elements = new HashSet<>();
    private Size size;

    public BeingData() {
        super();
    }

    public BeingData(long guid) {
        super(guid);
    }

    public Set<Element> getElements() {
        return elements;
    }

    public void setElements(Set<Element> elements) {
        this.elements = elements;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Set<BeingType> getBeingTypes() {
        return beingTypes;
    }

    public void setBeingTypes(Set<BeingType> beingType) {
        this.beingTypes = beingType;
    }
}
