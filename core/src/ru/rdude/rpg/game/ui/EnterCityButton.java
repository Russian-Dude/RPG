package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.PlaceObserver;
import ru.rdude.rpg.game.logic.map.objects.City;
import ru.rdude.rpg.game.logic.map.objects.CityInside;

@JsonIgnoreType
public class EnterCityButton extends TextButton implements PlaceObserver {

    public EnterCityButton() {
        super("Enter city", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        final Map map = Game.getCurrentGame().getGameMap();
        map.subscribe(this);
        setVisible(map.getPlayerPosition().getObject() instanceof City);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Cell playerPosition = map.getPlayerPosition();
                if (!(playerPosition.getObject() instanceof City)) {
                    return;
                }
                CityInside cityInside = Game.getCurrentGame().getCitiesHolder().getCityById(playerPosition.getObject().getId());
                CityVisual cityVisual = CityVisual.of((City) playerPosition.getObject());
                if (cityVisual == null) {
                    cityVisual = new CityVisual(cityInside);
                    EnterCityButton.this.getStage().addActor(cityVisual);
                    cityVisual.addSubWindowsToStage();
                    Game.getStaticReferencesHolders().cityVisuals().put(((City) playerPosition.getObject()), cityVisual);
                }
                EnterCityButton.this.setVisible(false);
                cityVisual.setVisible(true);
            }
        });
    }

    @Override
    public void update(Cell oldPosition, Cell newPosition) {
        setVisible(newPosition.getObject() instanceof City);
    }
}
