package ru.rdude.rpg.game.logic.entities.beings;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Party {

    private Set<Member> members;

    public void add(Being being) {

    }

    public void addToTheRightOf(Being of, Being who) {

    }

    public void addToTheLeftOf(Being of, Being who) {

    }

    public void summon(Being summoner, Being minion) {

    }

    public void forEach(Consumer<? super Being> action) {
        members.stream().map(Member::getMember).forEach(action);
    }

    public Stream<Being> stream() {
        return members.stream().map(Member::getMember);
    }

    private class Member implements BeingActionObserver {
        private Being member;
        private Being left;
        private Being right;

        public Being getMember() {
            return member;
        }

        public Being getLeft() {
            return left;
        }

        public Being getRight() {
            return right;
        }

        @Override
        public void update(BeingAction action, Being being) {

        }
    }
}
