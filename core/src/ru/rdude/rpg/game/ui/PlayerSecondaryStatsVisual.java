package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.StatObserver;

@JsonIgnoreType
public class PlayerSecondaryStatsVisual extends Table implements StatObserver {

    private final Player player;

    private final Label block = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label blockPure = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label concentration = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label concentrationPure = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label crit = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label critPure = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label def = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label defPure = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label melee = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label meleePure = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label range = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label rangePure = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label magic = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label magicPure = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label flee = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label fleePure = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label luckyDodge = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label luckyDodgePure = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label hit = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label hitPure = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label parry = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label parryPure = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label magicResistance = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label magicResistancePure = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label physicResistance = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label physicResistancePure = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label hpRecovery = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label hpRecoveryPure = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label stmRecovery = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label stmRecoveryPure = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);

    public PlayerSecondaryStatsVisual(Player player) {
        super(UiData.DEFAULT_SKIN);
        this.player = player;
        defaults().minWidth(100).space(9);
        columnDefaults(0).minWidth(150);

        // block
        add(new Label("Block", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        add(blockPure);
        add(block);
        player.stats().block().subscribe(this);
        row();

        // parry
        add(new Label("Parry", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        add(parryPure);
        add(parry);
        player.stats().parry().subscribe(this);
        row();

        // concentration
        add(new Label("Concentration", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        add(concentrationPure);
        add(concentration);
        player.stats().concentration().subscribe(this);
        row();

        // crit
        add(new Label("Critical chance", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        add(critPure);
        add(crit);
        player.stats().crit().subscribe(this);
        row();

        // lucky dodge
        add(new Label("Lucky dodge", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        add(luckyDodgePure);
        add(luckyDodge);
        player.stats().flee().luckyDodgeChance().subscribe(this);
        row();

        // flee
        add(new Label("Flee", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        add(fleePure);
        add(flee);
        player.stats().flee().subscribe(this);
        row();

        // hit
        add(new Label("Hit", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        add(hitPure);
        add(hit);
        player.stats().hit().subscribe(this);
        row();

        // hp recovery
        add(new Label("Health recovery", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        add(hpRecoveryPure);
        add(hpRecovery);
        player.stats().hp().recovery().subscribe(this);
        row();

        // stm recovery
        add(new Label("Stamina recovery", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        add(stmRecoveryPure);
        add(stmRecovery);
        player.stats().stm().recovery().subscribe(this);
        row();

        // def
        add(new Label("Defence", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        add(defPure);
        add(def);
        player.stats().def().subscribe(this);
        row();

        // magic resistance
        add(new Label("Magic resistance", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        add(magicResistancePure);
        add(magicResistance);
        player.stats().magicResistance().subscribe(this);
        row();

        // Physic resistance
        add(new Label("Physic resistance", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        add(physicResistancePure);
        add(physicResistance);
        player.stats().physicResistance().subscribe(this);
        row();

        // melee damage
        add(new Label("Melee damage", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        add(meleePure);
        add(melee);
        player.stats().dmg().melee().min().subscribe(this);
        player.stats().dmg().melee().max().subscribe(this);
        row();

        // range damage
        add(new Label("Range damage", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        add(rangePure);
        add(range);
        player.stats().dmg().range().min().subscribe(this);
        player.stats().dmg().range().max().subscribe(this);
        row();

        // magic damage
        add(new Label("Magic damage", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        add(magicPure);
        add(magic);
        player.stats().dmg().magic().min().subscribe(this);
        player.stats().dmg().magic().max().subscribe(this);
        row();

        pack();
        player.stats().forEachWithNestedStats(this::update);
    }

    private void updateStat(Stat stat, Label pureLabel, Label label, boolean isDecimal) {
        double pure = stat.pureValue();
        double value = stat.value();
        label.setVisible(pure != value);

        if (isDecimal) {
            label.setText("(" + String.format("%.1f", value) + ")");
            pureLabel.setText(String.format("%.1f", pure) + "%");
        }
        else {
            label.setText("(" + (int) value + ")");
            pureLabel.setText((int) pure);
        }

        if (value < pure) {
            label.addAction(Actions.color(Color.RED));
        }
        else {
            label.addAction(Actions.color(Color.GREEN));
        }
    }

    private void updateDamage(Stat min, Stat max, Label pure, Label value) {
        pure.setText((int) min.pureValue() + " - " + (int) max.pureValue());
        value.setText("(" + (int) min.value() + " - " + (int) max.value() + ")");
        value.setVisible(min.value() != min.pureValue() && max.value() != max.pureValue());
        if (min.pureValue() + max.pureValue() > min.value() + max.value()) {
            value.addAction(Actions.color(Color.RED));
        }
        else {
            value.addAction(Actions.color(Color.GREEN));
        }
    }

    @Override
    public void update(Stat stat) {
        if (stat == player.stats().block()) {
            updateStat(stat, blockPure, block, true);
        }
        else if (stat == player.stats().concentration()) {
            updateStat(stat, concentrationPure, concentration, false);
        }
        else if (stat == player.stats().crit()) {
            updateStat(stat, critPure, crit, true);
        }
        else if (stat == player.stats().def()) {
            updateStat(stat, defPure, def, false);
        }
        else if (stat == player.stats().flee()) {
            updateStat(stat, fleePure, flee, false);
        }
        else if (stat == player.stats().flee().luckyDodgeChance()) {
            updateStat(stat, luckyDodgePure, luckyDodge, true);
        }
        else if (stat == player.stats().hit()) {
            updateStat(stat, hitPure, hit, false);
        }
        else if (stat == player.stats().parry()) {
            updateStat(stat, parryPure, parry, true);
        }
        else if (stat == player.stats().magicResistance()) {
            updateStat(stat, magicResistancePure, magicResistance, true);
        }
        else if (stat == player.stats().physicResistance()) {
            updateStat(stat, physicResistancePure, physicResistance, false);
        }
        else if (stat == player.stats().hp().recovery()) {
            updateStat(stat, hpRecoveryPure, hpRecovery, false);
        }
        else if (stat == player.stats().stm().recovery()) {
            updateStat(stat, stmRecoveryPure, stmRecovery, false);
        }
        else if (stat == player.stats().dmg().melee().min() || stat == player.stats().dmg().melee().max()) {
            updateDamage(player.stats().dmg().melee().min(), player.stats().dmg().melee().max(), meleePure, melee);
        }
        else if (stat == player.stats().dmg().range().min() || stat == player.stats().dmg().range().max()) {
            updateDamage(player.stats().dmg().range().min(), player.stats().dmg().range().max(), rangePure, range);
        }
        else if (stat == player.stats().dmg().magic().min() || stat == player.stats().dmg().magic().max()) {
            updateDamage(player.stats().dmg().magic().min(), player.stats().dmg().magic().max(), magicPure, magic);
        }
    }
}
