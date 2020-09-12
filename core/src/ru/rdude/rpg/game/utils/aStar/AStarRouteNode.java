package ru.rdude.rpg.game.utils.aStar;

class AStarRouteNode<T extends AStarNode> implements Comparable<AStarRouteNode<?>> {

    private final T current;
    private T previous;
    private double routeScore;
    private double estimatedScore;

    AStarRouteNode(T current) {
        this(current, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    AStarRouteNode(T current, T previous, double routeScore, double estimatedScore) {
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

    public double getRouteScore() {
        return routeScore;
    }

    public void setRouteScore(double routeScore) {
        this.routeScore = routeScore;
    }

    public double getEstimatedScore() {
        return estimatedScore;
    }

    public void setEstimatedScore(double estimatedScore) {
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