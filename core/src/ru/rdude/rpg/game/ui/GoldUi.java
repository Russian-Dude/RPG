package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gold.GoldObserver;

@JsonIgnoreType
public class GoldUi extends Table implements GoldObserver {

    private final Label label = new Label("999999999", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);

    public GoldUi() {
        super(UiData.DEFAULT_SKIN);
        background(UiData.SEMI_TRANSPARENT_BACKGROUND);
        add(label).center();
        label.setText("Gold " + Game.getCurrentGame().getGold().getAmount());
        pack();
        Game.getCurrentGame().getGold().subscribe(this);
    }

    @Override
    public void updateGold(int oldValue, int newValue) {
        label.setText("Gold " + newValue);
    }
}
