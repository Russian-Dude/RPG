package ru.rdude.rpg.game.logic.game;

import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.holders.Slot;
import ru.rdude.rpg.game.logic.map.objects.City;
import ru.rdude.rpg.game.ui.CityVisual;
import ru.rdude.rpg.game.ui.ItemVisual;

public final class StaticReferencesHolders {

    private final StaticReferencesHolder<Item, ItemVisual> itemToVisuals = new StaticReferencesHolder<>();
    private final StaticReferencesHolder<? super Entity<?>, Slot<? extends Entity<?>>> entitiesInSlots = new StaticReferencesHolder<>();
    private final StaticReferencesHolder<City, CityVisual> cityVisuals = new StaticReferencesHolder<>();

    public StaticReferencesHolder<Item, ItemVisual> itemsVisuals() {
        return itemToVisuals;
    }

    public StaticReferencesHolder<? super Entity<?>, Slot<? extends Entity<?>>> entitiesInSlots() {
        return entitiesInSlots;
    }

    public StaticReferencesHolder<City, CityVisual> cityVisuals() {
        return cityVisuals;
    }
}
