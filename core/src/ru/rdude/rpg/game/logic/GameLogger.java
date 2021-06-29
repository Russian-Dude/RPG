package ru.rdude.rpg.game.logic;

import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.enums.Target;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class GameLogger {

    // concat messages to one String if limit is exceed
    private final int messagesLimit = 1000;

    private List<String> messages;
    private Set<Consumer<String>> listeners;

    public GameLogger() {
        messages = new ArrayList<>(messagesLimit);
        listeners = new HashSet<>();
    }

    public void log(Being<?> caster, Being<?> target, SkillData skill) {
        if (caster == target) {
            log(caster.getName() + " used " + skill.getName());
        }
        else {
            log(caster.getName() + " used " + skill.getName() + ". Target is " + target.getName());
        }
    }

    public void log(Being<?> caster, SkillData skillData, Target target) {
        String targetString = target.name()
                .toLowerCase()
                .replaceAll("_", " ")
                .replaceAll(" other", "");
        log(caster.getName() + " used " + skillData.getName() + ", targeting " + targetString);
    }

    public void log(Being<?> caster, SkillData skill) {
        log(caster.getName() + " used " + skill.getName());
    }

    public void log(BeingAction beingAction, Being<?> being) {
        switch (beingAction.action()) {
            case DAMAGE_RECEIVE:
                log(being.getName() + " received " + (int) beingAction.value() + " damage");
                break;
            case RESIST:
                log(being.getName() + " resisted " + beingAction.withSkill().getName());
                break;
            case PARRY:
                log(being.getName() + " parry " + beingAction.withSkill().getName());
                break;
            case DODGE:
                log(being.getName() + " dodged " + beingAction.withSkill().getName());
                break;
            case MISS:
                log(being.getName() + " missed");
                break;
            case DIE:
                log(being.getName() + " died");
                break;
            case BLOCK:
                log(being.getName() + " blocked " + beingAction.withSkill().getName());
                break;
            case HEAL_RECEIVE:
                log(being.getName() + " healed by " + (int) beingAction.value());
                break;
            case CRITICAL_RECEIVE:
                log(being.getName() + " received  " + (int) beingAction.value() + " critical damage");
                break;
        }
    }

    public void log(Being<?> being, Buff buff, BeingAction beingAction) {
        switch (beingAction.action()) {
            case BUFF_RECEIVE:
                log(being.getName() + " received buff " + buff.getName() + " from " + beingAction.interactor().getName());
                break;
            case BUFF_REMOVED:
                log("Buff " + buff.getName() + " ended on " + being.getName());
                break;
        }
    }

    public void log(String message) {
        messages.add(message);
        notifyListeners(message);
        checkLimit();
    }

    public void addListener(Consumer<String> listener) {
        listeners.add(listener);
    }

    private void notifyListeners(String message) {
        listeners.forEach(l -> l.accept(message));
    }

    private void checkLimit() {
        if (messages.size() >= messagesLimit) {
            StringBuilder builder = new StringBuilder();
            for (String message : messages) {
                builder.append(message).append("\r\n");
            }
            messages.clear();
            messages.add(builder.toString());
        }
    }
}
