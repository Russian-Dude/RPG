package ru.rdude.rpg.game.logic.entities.beings;

import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.enums.BeingType;
import ru.rdude.rpg.game.logic.enums.Element;

import java.util.List;

public class BeingAction {

    public enum Action {BLOCK, PARRY, DAMAGE_DEAL, DAMAGE_RECEIVE, CRITICAL_RECEIVE, DODGE, MISS, HEAL_DEAL,
        HEAL_RECEIVE, RESIST, DIE, KILL, NO_ACTION}

    private Action action;
    private List<Element> elements;
    private AttackType attackType;
    private List<BeingType> beingTypes;
    private double value;
    private Entity interactionWith;

    public BeingAction(Action action) {
        if (action == null)
            throw new IllegalArgumentException("required non null arguments");
        switch (action) {
            case BLOCK:
            case MISS:
            case DODGE:
            case PARRY:
            case RESIST:
            case NO_ACTION:
                this.action = action;
            default:
                throw new IllegalArgumentException("Any action caused by damage should be instanced with other constructor");
        }
    }

    public BeingAction(Action action, Entity interactionWith) {
        if (action == null || interactionWith == null)
            throw new IllegalArgumentException("required non null arguments");
        this.action = action;
        this.interactionWith = interactionWith;
    }

    public Action action() { return action; }
    public List<Element> elements() { return elements; }
    public AttackType attackType() { return attackType; }
    public List<BeingType> beingTypes() { return beingTypes; }
    public double value() { return value; }
    public Entity interactor() { return interactionWith; }
}

