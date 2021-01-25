package ru.rdude.rpg.game.logic.data.resources;

public class SkillResources extends Resources {

    public SkillResources() {
        super(new String[]{"skillIcon"}, new String[]{});
    }

    public Resource getSkillIcon() {
        return imageResources.get("skillIcon");
    }

    public void setSkillIcon(Resource skillIcon) {
        imageResources.put("skillIcon", skillIcon);
    }

}
