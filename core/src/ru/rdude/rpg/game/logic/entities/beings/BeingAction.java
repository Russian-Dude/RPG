package ru.rdude.rpg.game.logic.entities.beings;

import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.entities.skills.Damage;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.enums.BeingType;
import ru.rdude.rpg.game.logic.enums.Element;

import java.util.List;

public class BeingAction {

    public enum Action {
        BLOCK ("successful block"),
        PARRY("successful parry"),
        DAMAGE_DEAL("damage deal"),
        DAMAGE_RECEIVE("damage received"),
        CRITICAL_RECEIVE("critical damage received"),
        DODGE("successful dodge"),
        MISS("attack missed"),
        HEAL_DEAL("heal someone"),
        HEAL_RECEIVE("being healed"),
        BUFF_DEAL("cast buff"),
        BUFF_RECEIVE("receiving a buff"),
        BUFF_REMOVED("buff ends"),
        RESIST("resisting a buff"),
        DIE("death"),
        KILL("killing"),
        ITEM_USED("using an item"),
        NO_ACTION("no action");

        public final String prettyStringAfter;

        Action(String prettyStringAfter) {
            this.prettyStringAfter = prettyStringAfter;
        }
    }

    private Action action;
    private double value;
    private Entity interactionWith;
    private SkillData withSkill;

    public BeingAction(Action action, Entity interactionWith, SkillData withSkill, double value) {
        if (action == null || interactionWith == null)
            throw new IllegalArgumentException("required non null arguments");
        this.action = action;
        this.interactionWith = interactionWith;
        this.withSkill = withSkill;
        this.value = value;
    }

    public Action action() { return action; }
    public double value() { return value; }
    public Entity interactor() { return interactionWith; }
    public SkillData withSkill() { return withSkill; }
}

