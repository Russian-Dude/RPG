package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.StatObserver;

@JsonIgnoreType
public class PlayerStatsVisual extends Table implements StatObserver {

    private final Player player;
    private final PlayerPrimaryStatsVisual primaryStats;
    private final PlayerSecondaryStatsVisual secondaryStats;
    private final Label lvlLabel;
    private final Label statPointsLabel;
    private final ExpBar expBar;

    public PlayerStatsVisual(Player player) {
        super(UiData.DEFAULT_SKIN);
        this.player = player;
        this.primaryStats = new PlayerPrimaryStatsVisual(player);
        this.secondaryStats = new PlayerSecondaryStatsVisual(player);
        this.lvlLabel = new Label("Level " + ((int) player.stats().lvl().value()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        this.statPointsLabel = new Label("Stat points " + (int) player.stats().lvl().statPoints().value(), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        this.expBar = new ExpBar(player.stats().lvl().exp());
        this.expBar.setWidth(390f);

        player.stats().lvl().subscribe(this);
        player.stats().lvl().statPoints().subscribe(this);

        defaults().space(10f).padTop(5f);

        add(lvlLabel);
        add(statPointsLabel);
        row();
        add(expBar).colspan(3);
        row().padTop(20f);
        add(primaryStats);
        add(secondaryStats);

        pack();
    }

    @Override
    public void update(Stat stat) {
        if (stat == player.stats().lvl()) {
            lvlLabel.setText("Level " + (int) stat.value());
        }
        else if (stat == player.stats().lvl().statPoints()) {
            statPointsLabel.setText("Stat points " + (int) stat.value());
        }
    }
}
