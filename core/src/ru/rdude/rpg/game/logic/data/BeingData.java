package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.logic.enums.BeingType;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.enums.Size;

import java.util.Set;

public class BeingData extends EntityData {

    public BeingData(long guid) {
        super(guid);
    }
    private Set<BeingType> beingTypes;
    private Set<Element> elements;
    private Size size;

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
