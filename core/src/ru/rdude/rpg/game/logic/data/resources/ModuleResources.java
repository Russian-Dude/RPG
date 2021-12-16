package ru.rdude.rpg.game.logic.data.resources;

import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.ArrayList;
import java.util.List;

@JsonPolymorphicSubType("moduleResources")
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

    public void addParticleResource(Resource resource) {
        List<Resource> list = new ArrayList<>();
        list.add(resource);
        particleResources.put(String.valueOf(resource.getGuid()), list);
    }

    @Override
    public void remove(Resource resource) {
        String guid = String.valueOf(resource.getGuid());
        imageResources.remove(guid);
        soundResources.remove(guid);
        particleResources.remove(guid);
    }
}
