package gunn.modcurrency.mod.tileentity;

import gunn.modcurrency.mod.container.util.INBTInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-02-23
 */
public class TileBlockEntityMarket extends TileEntity implements INBTInventory{
    private String owner;

    public TileBlockEntityMarket(){
        owner = "";
    }


    //<editor-fold desc="NBT & Packet Stoof-------------------------------------------">
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("owner", owner);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if(compound.hasKey("owner")) owner = compound.getString("owner");
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("owner", owner);

        return new SPacketUpdateTileEntity(pos, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        owner = pkt.getNbtCompound().getString("owner");
    }
    //</editor-fold>

    public void setOwner(String owner){ this.owner = owner; }

    public String getOwner(){ return owner; }
}

