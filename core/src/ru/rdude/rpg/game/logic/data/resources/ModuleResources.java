package ru.rdude.rpg.game.logic.data.resources;

public class ModuleResources extends Resources {

    public ModuleResources() {
        super();
    }

    public void addImageResource(Resource resource) {
        imageResources.put(String.valueOf(resource.getGuid()), resource);
    }

    public void addSoundResource(Resource resource) {
        soundResources.put(String.valueOf(resource.getGuid()), resource);
    }

    @Override
    public void remove(Resource resource) {
        String guid = String.valueOf(resource.getGuid());
        imageResources.remove(guid);
        soundResources.remove(guid);
    }
}
