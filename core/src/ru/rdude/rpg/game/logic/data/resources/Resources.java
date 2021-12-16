package ru.rdude.rpg.game.logic.data.resources;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public abstract class Resources {

    protected Map<String, Resource> imageResources = new HashMap<>();
    protected Map<String, Resource> soundResources = new HashMap<>();
    protected Map<String, List<Resource>> particleResources = new HashMap<>();

    Resources() {
    }

    protected Resources(String[] imageResources, String[] soundResources, String[] particleResources) {
        for (String imageResource : imageResources) {
            this.imageResources.put(imageResource, null);
        }
        for (String soundResource : soundResources) {
            this.soundResources.put(soundResource, null);
        }
        for (String particleResource : particleResources) {
            this.particleResources.put(particleResource, new ArrayList<>());
        }
    }

    public Set<Resource> getImageResources() {
        return this.imageResources.values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public void setImageResources(Map<String, Resource> imageResources) {
        this.imageResources = imageResources;
    }

    public Set<Resource> getSoundResources() {
        return this.soundResources.values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public void setSoundResources(Map<String, Resource> soundResources) {
        this.soundResources = soundResources;
    }

    public Set<Resource> getParticleResources() {
        return particleResources.values().stream()
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public void setParticleResources(Map<String, List<Resource>> particleResources) {
        this.particleResources = particleResources;
    }

    public boolean swap(Resource oldV, Resource newV) {
        return swapImage(oldV, newV) | swapSound(oldV, newV) | swapParticle(oldV, newV);
    }

    public boolean swapImage(Resource oldV, Resource newV) {
        boolean res = false;
        List<String> toChange = imageResources.entrySet().stream()
                .filter(entry -> oldV.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        for (String s : toChange) {
            res = true;
            imageResources.put(s, newV);
        }
        return res;
    }

    public boolean swapSound(Resource oldV, Resource newV) {
        boolean res = false;
        List<String> toChange = soundResources.entrySet().stream()
                .filter(entry -> oldV.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        for (String s : toChange) {
            res = true;
            soundResources.put(s, newV);
        }
        return res;
    }

    public boolean swapParticle(Resource oldV, Resource newV) {
        boolean res = false;
        for (Map.Entry<String, List<Resource>> entry : particleResources.entrySet()) {
            List<Resource> list = entry.getValue();
            if (list != null && list.remove(oldV)) {
                list.add(newV);
                res = true;
            }
        }
        return res;
    }

    public void remove(Resource resource) {
        removeFrom(resource, imageResources);
        removeFrom(resource, soundResources);
        particleResources.values().forEach(list -> list.remove(resource));
    }

    private void removeFrom(Resource resource, Map<String, Resource> resourceMap) {
        resourceMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(resource))
                .findAny()
                .map(Map.Entry::getKey)
                .ifPresent(s -> resourceMap.put(s, null));
    }

    public Map<String, Resource> getImageResourcesMap() {
        return imageResources;
    }

    public void setImageResourcesMap(Map<String, Resource> value) {
        this.imageResources = value;
    }

    public Map<String, Resource> getSoundResourcesMap() {
        return soundResources;
    }

    public void setSoundResourcesMap(Map<String, Resource> value) {
        this.soundResources = value;
    }
}
