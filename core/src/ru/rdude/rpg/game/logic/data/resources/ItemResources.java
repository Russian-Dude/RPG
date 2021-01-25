package ru.rdude.rpg.game.logic.data.resources;

public class ItemResources extends Resources {

    public ItemResources() {
        super(new String[] {"mainImage"}, new String[]{});
    }

    public Resource getMainImage() {
        return imageResources.get("mainImage");
    }

    public void setMainImage(Resource mainImage) {
        imageResources.put("mainImage", mainImage);
    }
}
