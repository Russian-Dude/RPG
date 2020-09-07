package ru.rdude.rpg.game.logic.data.io;

import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.enums.ItemRarity;

import java.io.*;
import java.util.List;
import java.util.Set;

public class ItemDataSerializableObject implements Externalizable {

    private ItemData itemData;

    public ItemDataSerializableObject(ItemData itemData) {
        this.itemData = itemData;
    }

    public ItemData getItemData() {
        return itemData;
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeLong(itemData.getGuid());
        objectOutput.writeObject(itemData.getName());
        objectOutput.writeObject(itemData.getNameInEditor());
        objectOutput.writeObject(itemData.getDescription());

        objectOutput.writeObject(itemData.getItemType());
        objectOutput.writeObject(itemData.getType());
        objectOutput.writeBoolean(itemData.isStackable());
        objectOutput.writeObject(itemData.getElements());
        objectOutput.writeObject(itemData.getSkillsOnUse());
        objectOutput.writeObject(itemData.getSkillsEquip());
        objectOutput.writeDouble(itemData.getDurability());
        objectOutput.writeDouble(itemData.getPrice());
        objectOutput.writeObject(itemData.getAttackType());
        objectOutput.writeDouble(itemData.getMinDmg());
        objectOutput.writeDouble(itemData.getMaxDmg());
        objectOutput.writeObject(itemData.getRarity());
        // stats:
        objectOutput.writeObject(new StatsSerializable(itemData.getRequirements()));
        objectOutput.writeObject(new StatsSerializable(itemData.getStats()));
        //coefficients:
        objectOutput.writeObject(new CoefficientsSerializable(itemData.getCoefficients()));
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        itemData = new ItemData(objectInput.readLong());
        itemData.setName((String) objectInput.readObject());
        itemData.setNameInEditor((String) objectInput.readObject());
        itemData.setDescription((String) objectInput.readObject());

        itemData.setItemType((Class<? extends Item>) objectInput.readObject());
        itemData.setType((String) objectInput.readObject());
        itemData.setStackable(objectInput.readBoolean());
        itemData.setElements((Set<Element>) objectInput.readObject());
        itemData.setSkillsOnUse((List<Long>) objectInput.readObject());
        itemData.setSkillsEquip((List<Long>) objectInput.readObject());
        itemData.setDurability(objectInput.readDouble());
        itemData.setPrice(objectInput.readDouble());
        itemData.setAttackType((AttackType) objectInput.readObject());
        itemData.setMinDmg(objectInput.readDouble());
        itemData.setMaxDmg(objectInput.readDouble());
        itemData.setRarity((ItemRarity) objectInput.readObject());
        //stats:
        itemData.setRequirements(((StatsSerializable) objectInput.readObject()).getStats());
        itemData.setStats(((StatsSerializable) objectInput.readObject()).getStats());
        //coefficients:
        itemData.setCoefficients(((CoefficientsSerializable) objectInput.readObject()).getCoefficients());
    }
}
