package com.mrzak34.thunderhack.util;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;

import java.util.UUID;

/* Store real durability inside special NBTTagCompound */

@SideOnly(Side.CLIENT)
public class SpecialTagCompound extends NBTTagCompound {


    private boolean empty;
    private int true_damage;

    public SpecialTagCompound(boolean empty, int true_damage) {
        this.empty = empty;
        this.true_damage = true_damage;
    }

    public SpecialTagCompound(NBTTagCompound old, int true_damage) {
        super();
        if(old == null) this.empty = true;
        else {
            for(String key : old.getKeySet()) {
                super.setTag(key, old.getTag(key));
            }
        }
        this.true_damage = true_damage;
    }

    public int getTrueDamage() {
        return this.true_damage;
    }

    public byte getId() {
        if(this.empty) return 0;
        return super.getId();
    }

    public NBTTagCompound copy() {
        NBTTagCompound copy = new SpecialTagCompound(this.empty, this.true_damage);

        for (String s : this.getKeySet()) {
            ((SpecialTagCompound)copy).setTagLegacy(s, this.getTag(s).copy());
        }

        return copy;
    }


    /*
    public boolean hasNoTags() { // do not clear me иди нахуй
        if(super.hasNoTags()) {
            this.empty = true;
        }
        return false;
    }


     */




    public void setTag(String key, NBTBase value) {
        this.empty = false;
        super.setTag(key, value);
    }

    public void setTagLegacy(String key, NBTBase value) {
        super.setTag(key, value);
    }

    public void setInteger(String key, int value) {
        this.empty = false;
        super.setInteger(key, value);
    }

    public void setByte(String key, byte value) {
        this.empty = false;
        super.setByte(key, value);
    }

    public void setShort(String key, short value) {
        this.empty = false;
        super.setShort(key, value);
    }

    public void setLong(String key, long value) {
        this.empty = false;
        super.setLong(key, value);
    }

    public void setUniqueId(String key, UUID value) {
        this.empty = false;
        super.setUniqueId(key, value);
    }

    public void setFloat(String key, float value) {
        this.empty = false;
        super.setFloat(key, value);
    }

    public void setDouble(String key, double value) {
        this.empty = false;
        super.setDouble(key, value);
    }

    public void setString(String key, String value) {
        this.empty = false;
        super.setString(key, value);
    }

    public void setByteArray(String key, byte[] value) {
        this.empty = false;
        super.setByteArray(key, value);
    }

    public void setIntArray(String key, int[] value) {
        this.empty = false;
        super.setIntArray(key, value);
    }

}