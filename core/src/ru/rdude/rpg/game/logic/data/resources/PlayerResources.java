package ru.rdude.rpg.game.logic.data.resources;

public class PlayerResources extends Resources {

    public PlayerResources() {
        super(
                new String[]{"face", "cloth", "mouth", "nose", "eyes", "eyePupils", "eyeBrows", "beard", "hair",
                "faceColor", "hairColor", "eyesColor"},
                // sounds (unused)
                new String[]{});
    }

    public long getFaceIndex() {
        return imageResources.get("face").getGuid();
    }

    public void setFaceIndex(long index) {
        imageResources.put("face", new Resource("face", index));
    }

    public long getClothIndex() {
        return imageResources.get("cloth").getGuid();
    }

    public void setClothIndex(long index) {
        imageResources.put("cloth", new Resource("cloth", index));
    }

    public long getMouthIndex() {
        return imageResources.get("mouth").getGuid();
    }

    public void setMouthIndex(long index) {
        imageResources.put("mouth", new Resource("mouth", index));
    }

    public long getNoseIndex() {
        return imageResources.get("nose").getGuid();
    }

    public void setNoseIndex(long index) {
        imageResources.put("nose", new Resource("nose", index));
    }

    public long getEyesIndex() {
        return imageResources.get("eyes").getGuid();
    }

    public void setEyesIndex(long index) {
        imageResources.put("eyes", new Resource("eyes", index));
    }

    public long getEyePupilsIndex() {
        return imageResources.get("eyePupils").getGuid();
    }

    public void setEyePupilsIndex(long index) {
        imageResources.put("eyePupils", new Resource("eyePupils", index));
    }

    public long getEyeBrowsIndex() {
        return imageResources.get("eyeBrows").getGuid();
    }

    public void setEyeBrowsIndex(long index) {
        imageResources.put("eyeBrows", new Resource("eyeBrows", index));
    }

    public long getBeardIndex() {
        return imageResources.get("beard").getGuid();
    }

    public void setBeardIndex(long index) {
        imageResources.put("beard", new Resource("beard", index));
    }

    public long getHairIndex() {
        return imageResources.get("hair").getGuid();
    }

    public void setHairIndex(long index) {
        imageResources.put("hair", new Resource("hair", index));
    }

    public long getFaceColorIndex() {
        return imageResources.get("faceColor").getGuid();
    }

    public void setFaceColorIndex(long index) {
        imageResources.put("faceColor", new Resource("faceColor", index));
    }

    public long getHairColorIndex() {
        return imageResources.get("hairColor").getGuid();
    }

    public void setHairColorIndex(long index) {
        imageResources.put("hairColor", new Resource("hairColor", index));
    }

    public long getEyesColorIndex() {
        return imageResources.get("eyesColor").getGuid();
    }

    public void setEyesColorIndex(long index) {
        imageResources.put("eyesColor", new Resource("eyesColor", index));
    }
}
