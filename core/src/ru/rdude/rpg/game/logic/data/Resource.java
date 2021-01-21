package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.utils.Functions;

public class Resource {

    private String name;
    private Long guid;

    public Resource(String name) {
        this(name, Functions.generateGuid());
    }

    public Resource(String name, long guid) {
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

    public void setGuid(Long guid) {
        this.guid = guid;
    }
}
