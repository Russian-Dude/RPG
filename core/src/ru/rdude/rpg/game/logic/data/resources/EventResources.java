package ru.rdude.rpg.game.logic.data.resources;

import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("eventResources")
public class EventResources extends Resources {

    public EventResources() {
        super(new String[]{"eventImage"}, new String[]{"eventSound"});
    }

    public Resource getEventImage() {
        return imageResources.get("eventImage");
    }

    public void setEventImage(Resource resource) {
        imageResources.put("eventImage", resource);
    }

    public Resource getEventSound() {
        return soundResources.get("eventSound");
    }

    public void setEventSound(Resource resource) {
        soundResources.put("eventSound", resource);
    }
}
