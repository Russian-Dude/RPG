package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.PlaceObserver;
import ru.rdude.rpg.game.logic.map.objects.City;
import ru.rdude.rpg.game.logic.map.objects.CityInside;

@JsonIgnoreType
public class CityVisual extends Table implements PlaceObserver {

    private final CityInside cityInside;
    private final ShopVisual shopVisual;

    public CityVisual(CityInside cityInside) {
        super(UiData.DEFAULT_SKIN);
        this.cityInside = cityInside;
        Game.getCurrentGame().getGameMap().subscribe(this);

        defaults().space(10f);
        background(UiData.SEMI_TRANSPARENT_BACKGROUND);

        // name
        add(new Label(cityInside.getName(), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));
        row();

        // shop
        this.shopVisual = new ShopVisual(cityInside.getShop(), () -> this.setVisible(true));
        shopVisual.setPosition(Gdx.graphics.getWidth() / 2f - shopVisual.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - shopVisual.getHeight() / 2f);
        this.shopVisual.setVisible(false);
        Button shopButton = new TextButton("Shop", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        shopButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                CityVisual.this.setVisible(false);
                shopVisual.setVisible(true);
            }
        });
        add(shopButton);
        row();

        // quests

        // exit button
        Button exitButton = new TextButton("Exit", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                CityVisual.this.setVisible(false);
            }
        });
        add(exitButton);
        row();

        pack();
        setPosition(Gdx.graphics.getWidth() / 2f - getWidth() / 2f, Gdx.graphics.getHeight() / 2f - getHeight() / 2f);
    }

    public void addSubWindowsToStage() {
        this.getStage().addActor(shopVisual);
        // TODO: 11.10.2021 add quests visual to stage
    }

    public static CityVisual of(City city) {
        return Game.getStaticReferencesHolders().cityVisuals().get(city);
    }

    @Override
    public void update(Cell oldPosition, Cell newPosition) {
        this.setVisible(false);
    }
}
