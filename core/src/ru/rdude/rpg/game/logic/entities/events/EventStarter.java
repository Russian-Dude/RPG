package ru.rdude.rpg.game.logic.entities.events;

import ru.rdude.rpg.game.logic.data.EventData;

public class EventStarter {

    public void start(long guid) {
        start(EventData.getEventByGuid(guid));
    }

    public void start(EventData eventData) {}

}
