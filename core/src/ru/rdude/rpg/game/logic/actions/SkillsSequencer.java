package ru.rdude.rpg.game.logic.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.entities.skills.SkillResult;
import ru.rdude.rpg.game.logic.entities.skills.SkillTargets;
import ru.rdude.rpg.game.logic.game.Game;

import java.util.List;

public class SkillsSequencer extends Actor {

    private SequenceAction currentSequence;
    private ParallelAction mainAction;

    public SkillsSequencer() {
        mainAction = Actions.parallel(Actions.forever(Actions.delay(1)));
        addAction(mainAction);
    }

    public void add(Buff buff) {
        final List<SkillResult> results = Game.getSkillResultsCreator().createFromBuff(buff);
        results.forEach(Game.getSkillApplier()::apply);
    }

    public void add(SkillData skillData, Being<?> caster, SkillTargets targets) {

        List<SkillResult> skillResults = Game.getSkillResultsCreator().createFromSkill(skillData, caster, targets);

        skillResults.forEach(Game.getSkillApplier()::apply);

        skillResults.forEach(result -> {
            if (currentSequence == null || currentSequence.getActor() == null) {
                currentSequence = Actions.sequence(createSkillAnimationAction(result));
                mainAction.addAction(currentSequence);
            }
            else {
                currentSequence.addAction(createSkillAnimationAction(result));
            }
        });



    }

    private Action createSkillAnimationAction(SkillResult result) {
        return Actions.run(() -> {});
    }
}
