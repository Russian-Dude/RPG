package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.entities.skills.AvailableSkillsObserver;
import ru.rdude.rpg.game.logic.entities.skills.SkillUser;
import ru.rdude.rpg.game.logic.enums.Target;
import ru.rdude.rpg.game.logic.game.Game;

@JsonIgnoreType
public class SkillSelector extends Tree<SkillSelectionElement, SkillData> implements AvailableSkillsObserver {

    public SkillSelector(Player player, Table parentTable) {
        super(UiData.DEFAULT_SKIN);

        player.getAvailableSkills().get().forEach(skillData -> add(new SkillSelectionElement(skillData)));

        player.getAvailableSkills().subscribe(this);
        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!SkillSelector.this.getSelection().isEmpty()) {
                    final SkillData skillData = SkillSelector.this.getSelection().first().getValue();
                    final Target mainTarget = skillData.getMainTarget();
                    if (mainTarget != Target.ALLY
                            && mainTarget != Target.ENEMY
                            && mainTarget != Target.ANY
                            && mainTarget != Target.ANY_OTHER) {
                        player.setReady(false);
                    }
                    Game.getSkillUser().use(skillData, player, skillData.getMainTarget());
                    parentTable.setVisible(false);
                }
            }
        });
    }

    @Override
    public void update(Action action, SkillData skillData) {
        if (action == Action.ADDED) {
            add(new SkillSelectionElement(skillData));
        }
        else if (action == Action.REMOVED) {
            remove(findNode(skillData));
        }
    }
}
