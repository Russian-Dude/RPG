package ru.rdude.rpg.game.utils.aStar;

class AStarRouteNode<T extends AStarNode> implements Comparable<AStarRouteNode<?>> {

    private final T current;
    private T previous;
    private int routeScore;
    private int estimatedScore;

    AStarRouteNode(T current) {
        this(current, null, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    AStarRouteNode(T current, T previous, int routeScore, int estimatedScore) {
        this.current = current;
        this.previous = previous;
        this.routeScore = routeScore;
        this.estimatedScore = estimatedScore;
    }

    public T getCurrent() {
        return current;
    }

    public T getPrevious() {
        return previous;
    }

    public void setPrevious(T previous) {
        this.previous = previous;
    }

    public int getRouteScore() {
        return routeScore;
    }

    public void setRouteScore(int routeScore) {
        this.routeScore = routeScore;
    }

    public int getEstimatedScore() {
        return estimatedScore;
    }

    public void setEstimatedScore(int estimatedScore) {
        this.estimatedScore = estimatedScore;
    }

    @Override
    public int compareTo(AStarRouteNode aStarRouteNode) {
        if (this.estimatedScore > aStarRouteNode.estimatedScore) {
            return 1;
        } else if (this.estimatedScore < aStarRouteNode.estimatedScore) {
            return -1;
        } else {
            return 0;
        }
    }
}