package ru.rdude.rpg.game.logic.map;

import java.util.HashSet;
import java.util.Set;

public class Road extends CellProperty {

    private Set<CellSide> destinations;

    public Road() {
        destinations = new HashSet<>();
    }

    public Set<CellSide> getDestinations() {
        return destinations;
    }

    public Set<CellSide> getDestinationsCopy() {
        return new HashSet<>(destinations);
    }

    public void setDestinations(Set<CellSide> destinations) {
        this.destinations = destinations;
    }

    public void addDestination(CellSide destination) {
        this.destinations.add(destination);
    }
}
