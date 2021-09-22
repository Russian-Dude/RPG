package ru.rdude.rpg.game.logic.data.resources;

import ru.rdude.rpg.game.logic.playerClass.ClassAbilitiesDataTree;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("playerClassResources")
public class PlayerClassResources extends Resources {

    private ClassAbilitiesDataTree cells = new ClassAbilitiesDataTree();

    public PlayerClassResources() {
        super(new String[]{ "classIcon" }, new String[] {}, new String[]{});
    }

    public Resource getClassIcon() {
        return imageResources.get("classIcon");
    }

    public void setClassIcon(Resource iconResource) {
        imageResources.put("classIcon", iconResource);
    }

    public ClassAbilitiesDataTree getCells() {
        return cells;
    }
}
