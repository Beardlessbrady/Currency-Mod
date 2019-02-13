package beardlessbrady.modcurrency.block.vending;

import beardlessbrady.modcurrency.ModCurrency;
import beardlessbrady.modcurrency.block.TileEconomyBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-10
 */

public class TileVending extends TileEconomyBase implements ICapabilityProvider, ITickable {

    public TileVending(){
    }

    @Override
    public void update() {

    }

    public void openGui(EntityPlayer player, World world, BlockPos pos){
        player.openGui(ModCurrency.instance, 30, world, pos.getX(), pos.getY(), pos.getZ());
    }

    //<editor-fold desc="NBT Stuff">
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return super.getUpdateTag();
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return super.getUpdatePacket();
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return super.getCapability(capability, facing);
    }
    //</editor-fold>
}
