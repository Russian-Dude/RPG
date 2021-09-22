package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.playerClass.PlayerClass;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.StatObserver;

@JsonIgnoreType
public class PlayerClassVisual extends Table implements StatObserver {

    private final Player player;
    private final PlayerClass playerClass;
    private final Label classNameLabel;
    private final Label lvlLabel;
    private final Label pointsLabel;
    private final ExpBar expBar;
    private final ClassAbilitiesDataTreeVisual abilitiesTree;


    public PlayerClassVisual(PlayerClass playerClass, Player player) {
        super(UiData.DEFAULT_SKIN);
        this.player = player;
        this.playerClass = playerClass;
        this.classNameLabel = new Label(playerClass.getClassData().getName(), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        this.lvlLabel = new Label("Level " + (int) playerClass.getLvl().value(), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        this.pointsLabel = new Label("Class points " + (int) playerClass.getLvl().statPoints().value(), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        this.abilitiesTree = new ClassAbilitiesDataTreeVisual(playerClass, player);
        this.expBar = new ExpBar(player.getCurrentClass().getLvl().exp());
        this.expBar.setWidth(abilitiesTree.getWidth());

        playerClass.getLvl().subscribe(this);
        playerClass.getLvl().statPoints().subscribe(this);

        defaults().space(10f).padTop(5f);

        add(classNameLabel);
        add(lvlLabel);
        add(pointsLabel);
        row();
        add(expBar).colspan(3);
        row();
        add(abilitiesTree).colspan(3);
        pack();
    }

    @Override
    public void update(Stat stat) {
        if (stat == playerClass.getLvl()) {
            lvlLabel.setText("Level " + (int) playerClass.getLvl().value());
        }
        else if (stat == playerClass.getLvl().statPoints()) {
            pointsLabel.setText("Class points " + (int) playerClass.getLvl().statPoints().value());
        }
    }
}
