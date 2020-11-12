package ru.rdude.rpg.game.logic.data;

public abstract class EntityData {
    private long guid;
    private String name;
    private String nameInEditor;
    private String description;

    EntityData() {
    }

    public EntityData(long guid) {
        this.guid = guid;
        this.name = "";
        this.description = "";
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
}
