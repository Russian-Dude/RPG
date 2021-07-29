package ru.rdude.rpg.game.visual;

public class SkillAnimation {

    public enum SubTargetsOrder { SIMULTANEOUSLY, SIMULTANEOUSLY_AFTER_MAIN, ORDERED }
    public enum EntryOrder { SIMULTANEOUSLY, ORDERED }
    public enum Direction { NO, FORWARD, BACKWARD }

    private Entry root;
    private SubTargetsOrder subTargetsOrder;

    public Entry getRoot() {
        return root;
    }

    public void setRoot(Entry root) {
        this.root = root;
    }

    public SubTargetsOrder getSubTargetsOrder() {
        return subTargetsOrder;
    }

    public void setSubTargetsOrder(SubTargetsOrder subTargetsOrder) {
        this.subTargetsOrder = subTargetsOrder;
    }

    public static class Entry {

        private Entry next;
        private EntryOrder entryOrder;
        private Direction direction;
        private Long directed;
        private Long targetBack;
        private Long targetFront;
        private Long casterBack;
        private Long casterFront;
        private Long fullscreen;

        public Entry getNext() {
            return next;
        }

        public void setNext(Entry next) {
            this.next = next;
        }

        public EntryOrder getEntryOrder() {
            return entryOrder;
        }

        public void setEntryOrder(EntryOrder entryOrder) {
            this.entryOrder = entryOrder;
        }

        public Direction getDirection() {
            return direction;
        }

        public void setDirection(Direction direction) {
            this.direction = direction;
        }

        public Long getDirected() {
            return directed;
        }

        public void setDirected(Long directed) {
            this.directed = directed;
        }

        public Long getTargetBack() {
            return targetBack;
        }

        public void setTargetBack(Long targetBack) {
            this.targetBack = targetBack;
        }

        public Long getTargetFront() {
            return targetFront;
        }

        public void setTargetFront(Long targetFront) {
            this.targetFront = targetFront;
        }

        public Long getCasterBack() {
            return casterBack;
        }

        public void setCasterBack(Long casterBack) {
            this.casterBack = casterBack;
        }

        public Long getCasterFront() {
            return casterFront;
        }

        public void setCasterFront(Long casterFront) {
            this.casterFront = casterFront;
        }

        public Long getFullscreen() {
            return fullscreen;
        }

        public void setFullscreen(Long fullscreen) {
            this.fullscreen = fullscreen;
        }
    }

}
