package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.StatObserver;
import ru.rdude.rpg.game.logic.stats.primary.*;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreType
public class PlayerPrimaryStatsVisual extends Table implements StatObserver {

    private final Player player;

    private final Label agiPure = new Label("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
    private final Label agi = new Label("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
    private final Label dexPure = new Label("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
    private final Label dex = new Label("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
    private final Label intelPure = new Label("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
    private final Label intel = new Label("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
    private final Label luckPure = new Label("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
    private final Label luck = new Label("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
    private final Label strPure = new Label("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
    private final Label str = new Label("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
    private final Label vitPure = new Label("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
    private final Label vit = new Label("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);

    private final List<Button> buttons = new ArrayList<>();


    public PlayerPrimaryStatsVisual(Player player) {
        super(UiData.DEFAULT_SKIN);
        this.player = player;
        defaults().minWidth(50).space(25f);
        columnDefaults(1).minWidth(0);

        // agi
        add(new Label("Agility", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        Agi agiStat = player.stats().agi();
        add(createButton(agiStat));
        agiStat.subscribe(this);
        add(agiPure);
        add(agi);
        row();

        // dex
        add(new Label("Dexterity", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        Dex dexStat = player.stats().dex();
        add(createButton(dexStat));
        dexStat.subscribe(this);
        add(dexPure);
        add(dex);
        row();


        // int
        add(new Label("Intelligence", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        Int intelStat = player.stats().intel();
        add(createButton(intelStat));
        intelStat.subscribe(this);
        add(intelPure);
        add(intel);
        row();


        // luck
        add(new Label("Luck", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        Luck luckStat = player.stats().luck();
        add(createButton(luckStat));
        luckStat.subscribe(this);
        add(luckPure);
        add(luck);
        row();


        // str
        add(new Label("Strength", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        Str strStat = player.stats().str();
        add(createButton(strStat));
        strStat.subscribe(this);
        add(strPure);
        add(str);
        row();

        // vit
        add(new Label("Vitality", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        Vit vitStat = player.stats().vit();
        add(createButton(vitStat));
        vitStat.subscribe(this);
        add(vitPure);
        add(vit);
        row();

        pack();
        player.stats().lvl().statPoints().subscribe(this);
        player.stats().forEach(this::update);
    }

    private Button createButton(Stat stat) {
        final Button button = new TextButton("+", UiData.DEFAULT_SKIN, UiData.YES_SQUARE_BUTTON_STYLE);
        buttons.add(button);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                stat.increase(1);
                player.stats().lvl().statPoints().decrease(1);
            }
        });
        return button;
    }

    private void updateStat(Stat stat, Label pureLabel, Label label) {
        double pure = stat.pureValue();
        double value = stat.value();

        label.setVisible(pure != value);
        label.setText("(" + (int) value + ")");
        pureLabel.setText((int) pure);

        if (value < pure) {
            label.addAction(Actions.color(Color.RED));
        }
        else {
            label.addAction(Actions.color(Color.GREEN));
        }
    }

    @Override
    public void update(Stat stat) {
        if (stat == player.stats().agi()) {
            updateStat(stat, agiPure, agi);
        }
        else if (stat == player.stats().dex()) {
            updateStat(stat, dexPure, dex);
        }
        else if (stat == player.stats().intel()) {
            updateStat(stat, intelPure, intel);
        }
        else if (stat == player.stats().luck()) {
            updateStat(stat, luckPure, luck);
        }
        else if (stat == player.stats().str()) {
            updateStat(stat, strPure, str);
        }
        else if (stat == player.stats().vit()) {
            updateStat(stat, vitPure, vit);
        }
        else if (stat == player.stats().lvl().statPoints()) {
            buttons.forEach(button -> button.setVisible((int) stat.value() > 0));
        }
    }
}
