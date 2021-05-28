package ru.rdude.rpg.game.logic.time;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.entities.skills.SkillDuration;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "Implementation")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Buff.class, name = "Buff"),
        @JsonSubTypes.Type(value = Duration.class, name = "Duration"),
        @JsonSubTypes.Type(value = SkillDuration.class, name = "SkillDuration")
})
public interface TimeChangeObserver {
    void timeUpdate(int minutes);
}
