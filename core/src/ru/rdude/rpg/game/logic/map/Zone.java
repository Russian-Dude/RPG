package ru.rdude.rpg.game.logic.map;

public class Zone {

    private Point start;
    private Point end;

    public Zone(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Zone(int startX, int startY, int endX, int endY) {
        this(new Point(startX, startY), new Point(endX, endY));
    }

    public Point getStartPoint() {
        return start;
    }

    public Point getEndPoint() {
        return end;
    }

    public boolean hasPoint(Point point) {
        return
                point.x >= start.x
                        && point.x <= end.x
                        && point.y >= start.y
                        && point.y <= end.y;
    }

    public boolean hasPoint(int x, int y) {
        return
                x >= start.x
                        && x <= end.x
                        && y >= start.y
                        && y <= end.y;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
