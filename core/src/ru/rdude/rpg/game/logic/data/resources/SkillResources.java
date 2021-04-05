package ru.rdude.rpg.game.logic.data.resources;

public class SkillResources extends Resources {

    public SkillResources() {
        super(new String[]{"skillIcon"}, new String[]{"skillSound"});
    }

    public Resource getSkillIcon() {
        return imageResources.get("skillIcon");
    }

    public void setSkillIcon(Resource skillIcon) {
        imageResources.put("skillIcon", skillIcon);
    }

    public Resource getSkillSound() {
        return soundResources.get("skillSound");
    }

    public void setSkillSound(Resource skillSound) {
        soundResources.put("skillSound", skillSound);
    }

}
