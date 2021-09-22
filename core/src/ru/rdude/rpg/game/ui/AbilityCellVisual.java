package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.playerClass.Ability;
import ru.rdude.rpg.game.logic.playerClass.AbilityObserver;

@JsonIgnoreType
public class AbilityCellVisual extends Group implements AbilityObserver {

    private final Ability ability;
    private final Player player;

    private final Image abilityImage;
    private final Label lvlLabel;

    private AbilityInfoTooltip tooltip;

    public AbilityCellVisual(Ability ability, Player player) {
        super();
        this.ability = ability;
        this.player = player;
        this.ability.subscribe(this);

        // image
        Image background = new Image(UiData.DEFAULT_SKIN.getDrawable("Slot_Empty"));
        abilityImage = new Image(Game.getImageFactory().getRegion(ability.getAbilityData().getResources().getAbilityIcon().getGuid()));
        abilityImage.setSize(background.getWidth(), background.getHeight());
        if (!ability.isOpen() || ability.getLvl() < 1) {
            abilityImage.addAction(Actions.alpha(0.5f));
        }

        // button
        Button button = new Button(UiData.DEFAULT_SKIN, UiData.TRANSPARENT_BUTTON_STYLE);
        button.setSize(background.getWidth(), background.getHeight());
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (ability.isOpen()) {
                    ability.getAbilityData().getLvl(ability.getLvl() + 1).ifPresent(abilityLevel -> {
                        boolean enoughLvl = player.stats().lvl().value() >= abilityLevel.getLvlRequirement();
                        boolean enoughClassLvl = player.getCurrentClass().getLvl().value() >= abilityLevel.getClassLvlRequirement();
                        boolean enoughPoints = player.getCurrentClass().getLvl().statPoints().value() > 0;
                        if (enoughLvl && enoughClassLvl && enoughPoints) {
                            player.getCurrentClass().getLvl().statPoints().decrease(1.0);
                            ability.increaseLvl();
                        }
                    });
                }
            }
        });

        // lvl
        String stringLvl = ability.getLvl() > 1 ? String.valueOf(ability.getLvl()) : "";
        lvlLabel = new Label(stringLvl, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        lvlLabel.setWidth(background.getWidth());
        lvlLabel.setAlignment(Align.bottomRight);

        addActor(background);
        addActor(abilityImage);
        addActor(lvlLabel);
        addActor(button);
        setSize(background.getWidth(), background.getHeight());
        setBounds(getX(), getY(), getWidth(), getHeight());
    }

    public void setTooltip(AbilityInfoTooltip tooltip) {
        if (this.tooltip != null) {
            removeListener(this.tooltip);
            this.tooltip.hide();
        }
        this.tooltip = tooltip;
        addListener(tooltip);
    }

    public Ability getAbility() {
        return ability;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void updateAbility(Ability ability, boolean open, int oldLvl, int newLvl) {
        if (open && newLvl > 0) {
            abilityImage.addAction(Actions.alpha(1f));
        }
        else {
            abilityImage.addAction(Actions.alpha(0.5f));
        }
        String stringLvl = newLvl > 1 ? String.valueOf(newLvl) : "";
        lvlLabel.setText(stringLvl);
    }
}
