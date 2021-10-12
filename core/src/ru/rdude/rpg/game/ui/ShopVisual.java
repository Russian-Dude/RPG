package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.items.holders.ShopSlotsHolder;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.PlaceObserver;

@JsonIgnoreType
public class ShopVisual extends Table implements PlaceObserver {

    private final ShopSlotsHolder shopSlotsHolder;
    private Runnable backButtonAction;

    public ShopVisual(ShopSlotsHolder shopSlotsHolder, Runnable backButtonAction) {
        super(UiData.DEFAULT_SKIN);
        this.shopSlotsHolder = shopSlotsHolder;
        this.backButtonAction = backButtonAction;
        Game.getCurrentGame().getGameMap().subscribe(this);

        background(UiData.SEMI_TRANSPARENT_BACKGROUND);
        defaults().space(10f);

        add(new Label("Shop", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        row();
        add(new ItemSlotsHolderVisual(shopSlotsHolder));
        row();
        Button backButton = new TextButton("Back", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                backButtonAction.run();
                ShopVisual.this.setVisible(false);
            }
        });
        add(backButton);
        row();
        pack();
    }

    @Override
    public void update(Cell oldPosition, Cell newPosition) {
        this.setVisible(false);
    }
}
