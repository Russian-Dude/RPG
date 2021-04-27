package ru.rdude.rpg.game.logic.map;

import java.util.HashSet;
import java.util.Set;

public class Road {

    private Set<CellSide> destinations;
    private boolean realRoad = true;

    public Road() {
        destinations = new HashSet<>();
    }

    public Set<CellSide> getDestinations() {
        return destinations;
    }

    public void setDestinations(Set<CellSide> destinations) {
        this.destinations = destinations;
    }

    public void addDestination(CellSide destination) {
        this.destinations.add(destination);
    }

    public boolean isRealRoad() {
        return realRoad;
    }

    public void setRealRoad(boolean realRoad) {
        this.realRoad = realRoad;
    }
}
