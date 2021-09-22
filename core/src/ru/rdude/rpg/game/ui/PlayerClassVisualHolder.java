package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.playerClass.CurrentPlayerClassObserver;
import ru.rdude.rpg.game.logic.playerClass.PlayerClass;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@JsonIgnoreType
public class PlayerClassVisualHolder extends Group implements CurrentPlayerClassObserver {

    private final Player player;
    private final Map<PlayerClass, PlayerClassVisual> playerClasses;
    private PlayerClassVisual currentClass;

    public PlayerClassVisualHolder(Player player) {
        super();
        this.player = player;
        player.subscribe(this);
        this.playerClasses = player.getAllClasses().stream()
                .collect(Collectors.toMap(Function.identity(), cl -> new PlayerClassVisual(cl, player)));
        currentClass = playerClasses.get(player.getCurrentClass());
        addActor(currentClass);
        setSize(currentClass.getPrefWidth(), currentClass.getPrefHeight());
    }

    @Override
    public void currentPlayerClassUpdate(PlayerClass oldValue, PlayerClass newValue) {
        addActor(playerClasses.get(newValue));
        currentClass.remove();
    }
}
