package ru.rdude.rpg.game.logic.data.resources;

public class AbilityResources extends Resources {

    public AbilityResources() {
        super(new String[] {"abilityIcon"}, new String[]{});
    }

    public Resource getAbilityIcon() {
        return imageResources.get("abilityIcon");
    }

    public void setAbilityIcon(Resource abilityIcon) {
        imageResources.put("abilityIcon", abilityIcon);
    }
}
