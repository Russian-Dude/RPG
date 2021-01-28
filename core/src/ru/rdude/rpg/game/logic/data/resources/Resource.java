package ru.rdude.rpg.game.logic.data.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.utils.Functions;

import java.util.Objects;

public class Resource {

    private String name;
    private final Long guid;

    public Resource(String name) {
        this(name, Functions.generateGuid());
    }

    @JsonCreator
    public Resource(@JsonProperty("name") String name, @JsonProperty("guid") long guid) {
        this.name = name;
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getGuid() {
        return guid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;
        Resource resource = (Resource) o;
        return Objects.equals(getGuid(), resource.getGuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGuid());
    }
}
