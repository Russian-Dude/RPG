package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.data.AbilityData;
import ru.rdude.rpg.game.logic.data.AbilityDataCell;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.playerClass.*;

import java.util.HashSet;
import java.util.Set;

@JsonIgnoreType
public class ClassAbilitiesDataTreeVisual extends Table implements AbilityObserver {

    private final ClassAbilitiesDataTree dataTree;
    private final PlayerClass playerClass;
    private final Set<AbilityCellVisual> cellVisuals = new HashSet<>();

    public ClassAbilitiesDataTreeVisual(PlayerClass playerClass, Player player) {
        super(UiData.DEFAULT_SKIN);
        this.playerClass = playerClass;
        background(UiData.SEMI_TRANSPARENT_BACKGROUND);
        this.dataTree = playerClass.getClassData().getResources().getCells();
        for (int y = 0; y < dataTree.getCells()[0].length; y++) {
            for (int x = 0; x < dataTree.getCells().length; x++) {
                if (dataTree.get(x, y).isPresent()) {
                    AbilityDataCell<?> dataCell = dataTree.get(x, y).get();
                    // ability cell
                    if (dataCell instanceof AbilityData) {
                        playerClass.getAbilities().stream()
                                .filter(ability -> ability.getAbilityData().equals(dataCell))
                                .findAny()
                                .ifPresent(ability -> {
                                    AbilityCellVisual cellVisual = new AbilityCellVisual(ability, player);
                                    cellVisual.setTooltip(new AbilityInfoTooltip(ability, playerClass));
                                    add(cellVisual);
                                    cellVisuals.add(cellVisual);
                                    ability.subscribe(this);
                                });
                    }
                    // path cell
                    else if (dataCell instanceof AbilityPath) {
                        Image image = new Image(Game.getImageFactory().getAbilityPathAtlasRegion((AbilityPath) dataCell));
                        add(image);
                    }
                }
                else {
                    // empty cell
                    Actor placeholder = new Actor();
                    placeholder.setSize(40.0f, 40.0f);
                    add(placeholder).size(40f, 40f);
                }
            }
            row();
        }
        pack();
    }


    @Override
    public void updateAbility(Ability ability, boolean open, int oldLvl, int newLvl) {
        cellVisuals.forEach(cell -> cell.setTooltip(new AbilityInfoTooltip(cell.getAbility(), playerClass)));
    }
}
