package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.states.StateObserver;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.game.Game;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonIgnoreType
public class ItemInfoTooltip extends Tooltip<Table> implements StateObserver<Element> {

    private final Label elements;

    public ItemInfoTooltip(Item item) {
        super(new Table());
        setInstant(true);
        ItemData itemData = item.getEntityData();
        Table mainTable = getActor();
        mainTable.columnDefaults(0).space(10f);

        TooltipInfoHolder<Label> infoHolder = new TooltipInfoHolder<>(itemData);
        final List<Actor> actors = Game.getTooltipInfoFactory().get(itemData, itemData.getEntityInfo(), infoHolder);
        elements = infoHolder.getSubscriber();

        actors.forEach(actor -> {
            if (actor instanceof Label) {
                final float width = Math.min (((Label) actor).getPrefWidth(), Gdx.graphics.getWidth() / 3f);
                ((Label) actor).setWrap(true);
                ((Label) actor).setAlignment(Align.center);
                mainTable.add(actor).width(width).center().row();
            }
            else {
                mainTable.add(actor).center().row();
            }
        });
        mainTable.pack();
        mainTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
    }

    @Override
    public void update(Set<Element> current) {
        if (elements != null) {
            String elementsString = current.stream()
                    .map(Element::name)
                    .collect(Collectors.joining(" "));
            elements.setText(elementsString);
        }
    }
}
