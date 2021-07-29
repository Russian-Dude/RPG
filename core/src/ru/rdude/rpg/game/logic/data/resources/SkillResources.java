package ru.rdude.rpg.game.logic.data.resources;

import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;
import ru.rdude.rpg.game.visual.SkillAnimation;

import java.util.List;

@JsonPolymorphicSubType("skillResources")
public class SkillResources extends Resources {

    private SkillAnimation skillAnimation = new SkillAnimation();

    public SkillResources() {
        super(new String[]{"skillIcon"}, new String[]{"skillSound"}, new String[]{"skillAnimation"});
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

    public SkillAnimation getSkillAnimation() {
        return skillAnimation;
    }

    public void setSkillAnimation(SkillAnimation skillAnimation) {
        this.skillAnimation = skillAnimation;
    }

    public List<Resource> getSkillAnimationParticles() {
        return particleResources.get("skillAnimation");
    }

    public void setSkillAnimationParticles(List<Resource> resources) {
        particleResources.put("skillAnimation", resources);
    }

}
