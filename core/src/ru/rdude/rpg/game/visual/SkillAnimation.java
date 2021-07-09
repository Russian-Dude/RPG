package ru.rdude.rpg.game.visual;

public class SkillAnimation {

    enum SubTargetsOrder { SIMULTANEOUSLY, SIMULTANEOUSLY_AFTER_MAIN, ORDERED }
    enum Direction { FORWARD, BACKWARD }

    private Entry root;
    private SubTargetsOrder subTargetsOrder;

    public static class Entry {

        private Entry next;
        private Direction direction;
        private Long directed;
        private Long targetBack;
        private Long targetFront;
        private Long casterBack;
        private Long casterFront;
        private Long fullscreen;

    }

}
