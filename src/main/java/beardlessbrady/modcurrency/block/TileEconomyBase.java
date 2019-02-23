package beardlessbrady.modcurrency.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-10
 */

public class TileEconomyBase extends TileEntity {
    int cashReserve;
    boolean mode;

    public TileEconomyBase(){
        cashReserve = 0;
        mode = false; //true == admin
    }

    //<editor-fold desc="NBT Stuff">
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("cashReserve", cashReserve);
        compound.setBoolean("mode", mode);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("cashReserve")) cashReserve = compound.getInteger("cashReserve");
        if (compound.hasKey("mode")) mode = compound.getBoolean("mode");
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean("mode", mode);

        return new SPacketUpdateTileEntity(pos, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound compound = pkt.getNbtCompound();

        mode = compound.getBoolean("mode");
    }
    //</editor-fold>


    //Field Id's
    public static final int FIELD_LONG_CASHRESERVE = 0;

    public static final int FIELD_MODE = 0;


    public int getIntFieldCount(){
        return 1;
    }

    public void setIntField(int id, int value){
        switch(id){
            case FIELD_MODE:
                mode = (value == 1);
                break;
        }
    }

    public int getIntField(int id){
        switch(id){
            case FIELD_MODE:
                return (mode)? 1 : 0;
        }
        return 0;
    }

    public int getLongFieldCount(){
        return 1;
    }

    public void setLongField(int id, int value){
        switch(id){
            case FIELD_LONG_CASHRESERVE:
                cashReserve = value;
                break;
        }
    }

    public long getLongField(int id){
        switch(id){
            case FIELD_LONG_CASHRESERVE:
                return cashReserve;
        }
        return -1;
    }



}