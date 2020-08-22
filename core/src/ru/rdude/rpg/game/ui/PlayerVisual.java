package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import ru.rdude.rpg.game.logic.entities.beings.Player;

import static ru.rdude.rpg.game.ui.UiData.*;

public class PlayerVisual extends VerticalGroup {

    private Player player;

    private PlayerAvatar avatar;
    private Label name;
    private HpBar hpBar;
    private StmBar stmBar;
    private Button attack;
    private Button spells;
    private Button items;
    private HorizontalGroup buttons;

    public PlayerVisual(Player player) {
        this.player = player;
        avatar = new PlayerAvatar();
        hpBar = new HpBar(player);
        stmBar = new StmBar(player);
        buttons = new HorizontalGroup();
        attack = new Button(DEFAULT_SKIN, "attack");
        items = new Button(DEFAULT_SKIN, "items");
        spells = new Button(DEFAULT_SKIN, "spells");
        name = new Label(player.getName(), DEFAULT_SKIN);
        addActor(avatar);
        addActor(name);
        buttons.addActor(spells);
        buttons.addActor(attack);
        buttons.addActor(items);
        buttons.setWidth(buttons.getPrefWidth());
        buttons.setHeight(buttons.getPrefHeight());
        addActor(buttons);
        addActor(hpBar);
        addActor(stmBar);
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
    }
}
