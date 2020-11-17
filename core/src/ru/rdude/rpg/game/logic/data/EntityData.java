package ru.rdude.rpg.game.logic.data;

import java.util.HashSet;
import java.util.Set;

public abstract class EntityData {
    private long guid;
    private Set<Long> dependencies;
    private String name;
    private String nameInEditor;
    private String description;

    EntityData() {
    }

    public EntityData(long guid) {
        this.guid = guid;
        this.name = "";
        this.description = "";
        dependencies = new HashSet<>();
    }

    public long getGuid() {
        return guid;
    }

    public void setGuid(long guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNameInEditor() {
        return nameInEditor;
    }

    public void setNameInEditor(String nameInEditor) {
        this.nameInEditor = nameInEditor;
    }

    public Set<Long> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<Long> dependencies) {
        this.dependencies = dependencies;
    }

    public void addDependency(Long guid) {
        this.dependencies.add(guid);
    }

    public void addDependency(EntityData entityData) {
        addDependency(entityData.getGuid());
    }

    public void removeDependency(Long guid) {
        this.dependencies.remove(guid);
    }

    public void removeDependency(EntityData entityData) {
        removeDependency(entityData.getGuid());
    }
}
