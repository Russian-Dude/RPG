package ru.rdude.rpg.game.logic.data.resources;

import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("monsterResources")
public class MonsterResources extends Resources {

    public MonsterResources() {
        super(new String[]{"monsterImage"}, new String[]{"greetingSound", "deathSound", "hitReceivedSound"});
    }

    public Resource getMonsterImage() {
        return imageResources.get("monsterImage");
    }

    public void setMonsterImage(Resource resource) {
        imageResources.put("monsterImage", resource);
    }

    public Resource getGreetingSound() {
        return soundResources.get("greetingSound");
    }

    public void setGreetingSound(Resource resource) {
        soundResources.put("greetingSound", resource);
    }

    public Resource getDeathSound() {
        return soundResources.get("deathSound");
    }

    public void setDeathSound(Resource resource) {
        soundResources.put("deathSound", resource);
    }

    public Resource getHitReceivedSound() {
        return soundResources.get("hitReceivedSound");
    }

    public void setHitReceivedSound(Resource resource) {
        soundResources.put("hitReceivedSound", resource);
    }
}
