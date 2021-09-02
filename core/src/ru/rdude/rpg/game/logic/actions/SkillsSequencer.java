package ru.rdude.rpg.game.logic.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.entities.skills.Cast;
import ru.rdude.rpg.game.logic.entities.skills.SkillResult;
import ru.rdude.rpg.game.logic.entities.skills.SkillTargets;
import ru.rdude.rpg.game.logic.game.Game;

import java.util.List;

public class SkillsSequencer extends Actor {

    private SequenceAction currentSequence;

    public void add(Cast cast) {
        add(Game.getSkillResultsCreator().createFromCast(cast));
    }

    public void add(Buff buff) {
        add(Game.getSkillResultsCreator().createFromBuff(buff));
    }

    public void add(SkillData skillData, Being<?> caster, SkillTargets targets, boolean mainSkill) {
        add(Game.getSkillResultsCreator().createFromSkill(skillData, caster, targets, mainSkill));
    }

    private void add(List<SkillResult> skillResults) {
        skillResults.forEach(Game.getSkillApplier()::apply);

        if (currentSequence == null || currentSequence.getActor() == null) {
            currentSequence = Actions.sequence(Game.getSkillAnimator().action(skillResults));
            addAction(currentSequence);
        }
        else {
            currentSequence.addAction(Game.getSkillAnimator().action(skillResults));
        }
    }

    public void add(Action action) {
        if (currentSequence != null && currentSequence.getActor() != null) {
            currentSequence.addAction(action);
        }
        else {
            currentSequence = Actions.sequence(action);
            addAction(currentSequence);
        }
    }
}
