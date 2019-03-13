package beardlessbrady.modcurrency.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-10
 */

public class TileEconomyBase extends TileEntity {
    protected int cashReserve;
    protected boolean mode;
    protected UUID owner, playerUsing;

    public static UUID EMPTYID = new UUID(0L, 0L);

    public TileEconomyBase(){
        cashReserve = 0;
        mode = false; //true == admin

        owner = new UUID(0L, 0L);
        playerUsing = new UUID(0L, 0L);
    }

    //<editor-fold desc="NBT Stuff">
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("cashReserve", cashReserve);
        compound.setBoolean("mode", mode);
        compound.setUniqueId("playerUsing", playerUsing);
        compound.setUniqueId("owner", owner);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("cashReserve")) cashReserve = compound.getInteger("cashReserve");
        if (compound.hasKey("mode")) mode = compound.getBoolean("mode");
        if (compound.hasKey("playerUsing")) playerUsing = compound.getUniqueId("playerUsing");
        if (compound.hasKey("owner")) owner = compound.getUniqueId("owner");
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("cashReserve", cashReserve);
        compound.setBoolean("mode", mode);
        compound.setUniqueId("playerUsing", playerUsing);
        compound.setUniqueId("owner", owner);

        return new SPacketUpdateTileEntity(pos, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound compound = pkt.getNbtCompound();

        mode = compound.getBoolean("mode");
        cashReserve = compound.getInteger("cashReserve");
        playerUsing = compound.getUniqueId("playerUsing");
        owner = compound.getUniqueId("owner");
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


    public UUID getOwner(){
        return owner;
    }

    public void setOwner(UUID uuid){
        owner = uuid;
    }

    public UUID getPlayerUsing(){
        return playerUsing;
    }

    public void setPlayerUsing(UUID uuid){
        playerUsing = uuid;
    }

    public void voidPlayerUsing(){
        playerUsing = EMPTYID;
    }



}