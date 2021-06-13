package ru.rdude.rpg.game.logic.data.resources;

public class PlayerClassResources extends Resources {

    public PlayerClassResources() {
        super(new String[]{ "classIcon" }, new String[] {});
    }

    public Resource getClassIcon() {
        return imageResources.get("classIcon");
    }

    public void setClassIcon(Resource iconResource) {
        imageResources.put("classIcon", iconResource);
    }
}
