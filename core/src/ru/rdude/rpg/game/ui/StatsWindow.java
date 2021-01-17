package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.stats.Stats;

public class StatsWindow extends Window {

    public StatsWindow(Being being) {
        super(being.getName(), UiData.DEFAULT_SKIN, "mud");
        getTitleTable().padTop(20);
        Stats stats = being.stats();
        align(Align.center);

        HorizontalGroup mainStats = new HorizontalGroup();
        Table primaryStats = new Table(UiData.DEFAULT_SKIN);
        Table secondaryStats = new Table();

        // close button
        Button closeButton = new TextButton("X", UiData.DEFAULT_SKIN, "square_mud_no");
        getTitleTable().add(closeButton).padTop(25);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StatsWindow.this.setVisible(false);
            }
        });

        // main stats
        mainStats.space(10);
        mainStats.padTop(20);
        mainStats.addActor(new StatVisual(being, stats.lvl(), false));
        mainStats.addActor(new StatVisual(being, stats.lvl().exp(), false));
        mainStats.addActor(new StatVisual(being, stats.lvl().statPoints(), false));
        add(mainStats);
        row().space(15);

        // primary stats
        primaryStats.row().align(Align.topRight).spaceRight(20).padBottom(2);
        primaryStats.add(new StatVisual(being, stats.agi()));
        primaryStats.add(new StatVisual(being, stats.dex()));
        primaryStats.row().align(Align.topRight).spaceRight(20).padBottom(2);
        primaryStats.add(new StatVisual(being, stats.intel()));
        primaryStats.add(new StatVisual(being, stats.luck()));
        primaryStats.row().align(Align.topRight).spaceRight(20);
        primaryStats.add(new StatVisual(being, stats.str()));
        primaryStats.add(new StatVisual(being, stats.vit()));
        primaryStats.pack();
        add(primaryStats);
        row();

        // secondary stats
        secondaryStats.row().align(Align.topRight).spaceRight(20);
        secondaryStats.add(new StatVisual(being, stats.block(), false, true));
        secondaryStats.add(new StatVisual(being, stats.concentration(), false));
        secondaryStats.row().align(Align.topRight).spaceRight(20);
        secondaryStats.add(new StatVisual(being, stats.crit(), false, true));
        secondaryStats.add(new StatVisual(being, stats.def(), false));
        secondaryStats.row().align(Align.topRight).spaceRight(20);
        secondaryStats.add(new StatVisual(being, stats.flee(), false));
        secondaryStats.add(new StatVisual(being, stats.flee().luckyDodgeChance(), false, true));
        secondaryStats.row().align(Align.topRight).spaceRight(20);
        secondaryStats.add(new StatVisual(being, stats.hit(), false));
        secondaryStats.add(new StatVisual(being, stats.parry(), false, true));
        secondaryStats.row().align(Align.topRight).spaceRight(20);
        secondaryStats.add(new StatVisual(being, stats.magicResistance(), false, true));
        secondaryStats.add(new StatVisual(being, stats.physicResistance(), false, true));
        secondaryStats.row().align(Align.topRight).spaceRight(20);
        secondaryStats.add(new StatVisual(being, stats.hp().recovery(), false));
        secondaryStats.add(new StatVisual(being, stats.stm().recovery(), false));
        secondaryStats.row().align(Align.topRight).spaceRight(20);
        secondaryStats.add(new StatVisual(being, stats.dmg().melee().min(), false));
        secondaryStats.add(new StatVisual(being, stats.dmg().melee().max(), false));
        secondaryStats.row().align(Align.topRight).spaceRight(20);
        secondaryStats.add(new StatVisual(being, stats.dmg().range().min(), false));
        secondaryStats.add(new StatVisual(being, stats.dmg().range().max(), false));
        secondaryStats.row().align(Align.topRight).spaceRight(20);
        secondaryStats.add(new StatVisual(being, stats.dmg().magic().min(), false));
        secondaryStats.add(new StatVisual(being, stats.dmg().magic().max(), false));
        secondaryStats.pack();
        add(secondaryStats);

        pack();
    }
}
