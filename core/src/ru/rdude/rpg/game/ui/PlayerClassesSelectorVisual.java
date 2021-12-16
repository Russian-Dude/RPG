package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.playerClass.CurrentPlayerClassObserver;
import ru.rdude.rpg.game.logic.playerClass.PlayerClass;
import ru.rdude.rpg.game.logic.playerClass.PlayerClassObserver;
import ru.rdude.rpg.game.utils.ui.SortableVerticalGroup;

@JsonIgnoreType
public class PlayerClassesSelectorVisual extends Table implements PlayerClassObserver, CurrentPlayerClassObserver {

    private final SortableVerticalGroup<ClassSelectorElement> mainGroup;

    public PlayerClassesSelectorVisual(Player player) {
        super(UiData.DEFAULT_SKIN);
        this.mainGroup = new SortableVerticalGroup<>();
        ScrollPane scrollPane = new ScrollPane(mainGroup);
        player.subscribe(this);
        player.getAllClasses().forEach(playerClass -> {
            playerClass.subscribe(this);
            mainGroup.addSortableActor(new ClassSelectorElement(playerClass, player));
        });
        add(scrollPane).align(Align.top).height(465f);
        background(UiData.DEFAULT_BACKGROUND);
        pack();
    }

    @Override
    public void updatePlayerClass(PlayerClass playerClass) {
        this.mainGroup.sort();
    }

    @Override
    public void currentPlayerClassUpdate(PlayerClass oldValue, PlayerClass newValue) {
        this.mainGroup.sort();
    }
}
