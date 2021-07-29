package ru.rdude.rpg.game.logic.data.resources;

import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("itemResources")
public class ItemResources extends Resources {

    public ItemResources() {
        super(new String[] {"mainImage"}, new String[]{}, new String[]{});
    }

    public Resource getMainImage() {
        return imageResources.get("mainImage");
    }

    public void setMainImage(Resource mainImage) {
        imageResources.put("mainImage", mainImage);
    }
}
