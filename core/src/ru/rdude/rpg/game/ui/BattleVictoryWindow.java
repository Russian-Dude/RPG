package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.items.holders.ItemSlotsHolder;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.holders.Slot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BattleVictoryWindow extends Table {

    private final ItemSlotsHolder slotsHolder = new ItemSlotsHolder(30);
    private final Map<Being<?>, Label> expLabels = new HashMap<>();

    public BattleVictoryWindow() {
        super(UiData.DEFAULT_SKIN);
        background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
        add(new Label("VICTORY", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE))
                .colspan(10)
                .padBottom(Gdx.graphics.getHeight() / 44f)
                .center()
                .row();

        add(new Label("Experience reward:", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE))
                .colspan(10)
                .padBottom(Gdx.graphics.getHeight() / 72f)
                .center()
                .row();

        Game.getCurrentGame().getCurrentPlayers().forEach(being -> {
            Label expLabel = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            expLabels.put(being, expLabel);
            add(new Label(being.getName(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE))
                    .colspan(5)
                    .padBottom(Gdx.graphics.getHeight() / 216f)
                    .center();
            add(expLabel)
                    .colspan(5)
                    .center()
                    .row();
        });

        add(new Label("Items reward:", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE))
                .colspan(10)
                .padTop(Gdx.graphics.getHeight() / 72f)
                .padBottom(Gdx.graphics.getHeight() / 72f)
                .center()
                .row();

        int visualSlotsInOneRowAmount = 0;
        for (Slot<Item> slot : slotsHolder.getSlots()) {
            visualSlotsInOneRowAmount++;
            add(new ItemSlotVisual(slot));
            if (visualSlotsInOneRowAmount == 10) {
                row();
                visualSlotsInOneRowAmount = 0;
            }
        }

        TextButton done = new TextButton("Done", UiData.DEFAULT_SKIN, UiData.YES_BUTTON_STYLE);
        done.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                BattleVictoryWindow.this.setVisible(false);
                Game.getGameStateSwitcher().switchToMap();
            }
        });
        add(done)
                .padTop(Gdx.graphics.getHeight() / 72f)
                .center()
                .colspan(10);
        pack();
    }

    public void setItemsReward(Collection<Item> itemsReward) {
        slotsHolder.clear();
        for (Item item : itemsReward) {
            slotsHolder.receiveEntity(item);
        }
    }

    public void setExpRewards(Map<Being<?>, Double> expRewards) {
        expRewards.forEach((being, exp) -> {
            final Label label = expLabels.get(being);
            if (label != null) {
                label.setText(exp == 0d ? "-" : "+ " + exp);
            }
        });
    }
}
