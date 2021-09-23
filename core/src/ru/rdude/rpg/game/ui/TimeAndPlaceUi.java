package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.enums.Biom;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.PlaceObserver;
import ru.rdude.rpg.game.logic.map.objects.City;
import ru.rdude.rpg.game.logic.time.TimeManager;
import ru.rdude.rpg.game.logic.time.TimeObserver;

@JsonIgnoreType
public class TimeAndPlaceUi extends HorizontalGroup implements TimeObserver, PlaceObserver {

    private final Label year = new Label("Year 7777", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label month = new Label("Month 12" , UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label day = new Label("Day 27", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label time = new Label("12:55", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label biom = new Label("Volcanic", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label relief = new Label("Mountains", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);

    public TimeAndPlaceUi() {
        Game.getCurrentGame().getTimeManager().subscribe(this);
        Game.getCurrentGame().getGameMap().subscribe(this);

        year.setAlignment(Align.center);
        month.setAlignment(Align.center);
        day.setAlignment(Align.center);
        time.setAlignment(Align.center);
        Table timeTable = new Table();
        timeTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
        timeTable.add(year).fill(false).width(80);
        timeTable.add(month).fill(false).width(80);
        timeTable.add(day).fill(false).width(80);
        timeTable.add(time).fill(false).width(50);
        timeTable.columnDefaults(0).expand(false, false).fill(false).align(Align.center);
        timeTable.pack();

        biom.setAlignment(Align.center);
        relief.setAlignment(Align.center);
        Table placeTable = new Table();
        placeTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
        placeTable.add(biom).fill(false).width(100);
        placeTable.add(relief).fill(false).width(100);
        placeTable.pack();

        space(5);
        addActor(timeTable);
        addActor(placeTable);
        setSize(timeTable.getWidth() + placeTable.getWidth(), timeTable.getHeight());
        update(Game.getCurrentGame().getTimeManager());
    }

    @Override
    public void update(TimeManager timeManager) {
        year.setText("Year " + timeManager.year());
        month.setText("Month " + timeManager.month());
        day.setText("Day " + timeManager.day());
        time.setText(String.format("%02d:%02d", timeManager.hour(), timeManager.minute()));
    }

    @Override
    public void update(Cell oldPosition, Cell newPosition) {
        if (newPosition.getObject() instanceof City) {
            biom.setText("CITY");
            relief.setText("");
        }
        else {
            biom.setText(newPosition.getBiom().toString());
            if (newPosition.getBiom().equals(Biom.WATER)) {
                relief.setText(newPosition.getWaterDepth().name);
            }
            else {
                relief.setText(newPosition.getRelief().toString());
            }
        }
    }
}
