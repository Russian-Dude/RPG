package ru.rdude.rpg.game.logic.map;

public enum CellSide  {

    NN, SS, SW, SE, NW, NE, CENTER, NOT_RELATED;

    // is sides close to each other in hex cell
    public boolean isCloseTo(CellSide cellSide) {
        if (this == cellSide) return true;
        switch (cellSide) {
            case NE:
                return this == NN || this == SE;
            case NN:
                return this == NE || this == NW;
            case NW:
                return this == NN || this == SW;
            case SE:
                return this == SS || this == NE;
            case SS:
                return this == SE || this == SW;
            case SW:
                return this == SS || this == NW;
            default:
                return false;
        }
    }

}
