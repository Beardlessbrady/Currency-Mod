package gunn.modcurrency.mod.tile;

import gunn.modcurrency.mod.ModCurrency;
import gunn.modcurrency.mod.core.data.BankAccount;
import gunn.modcurrency.mod.core.data.BankAccountSavedData;
import gunn.modcurrency.mod.core.network.PacketHandler;
import gunn.modcurrency.mod.core.network.PacketSetItemCostToServer;
import gunn.modcurrency.mod.core.network.PacketSyncBankDataToClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-03-15
 */
public class TileATM extends TileEntity implements ICapabilityProvider{
    private ItemStackHandler moneySlot;
    private EntityPlayer playerUsing = null;

    public TileATM() {
        moneySlot = new ItemStackHandler(1);
    }

    public void openGui(EntityPlayer player, World world, BlockPos pos) {
        player.openGui(ModCurrency.instance, 33, world, pos.getX(), pos.getY(), pos.getZ());
        playerUsing = player;

        BankAccountSavedData bankData = BankAccountSavedData.getData(world);
        BankAccount account = bankData.getBankAccount(playerUsing.getUniqueID().toString());

        PacketSyncBankDataToClient pack = new PacketSyncBankDataToClient();
        pack.setData(account);
        PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP)playerUsing);
    }

    public void withdraw(){

    }

    public void deposit(){
        if(!world.isRemote) {
            BankAccountSavedData bankData = BankAccountSavedData.getData(world);
            BankAccount account = bankData.getBankAccount(playerUsing.getUniqueID().toString());

            account.setBalance(account.getBalance() + 5);
            bankData.setBankAccount(account);

            PacketSyncBankDataToClient pack = new PacketSyncBankDataToClient();
            pack.setData(account);
            PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP)playerUsing);
        }
    }

    //<editor-fold desc="NBT & Packet Stoof--------------------------------------------------------------------------------------------------">
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("moneySlot", moneySlot.serializeNBT());

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("moneySlot")) moneySlot.deserializeNBT((NBTTagCompound) compound.getTag("moneySlot"));
    }
    //</editor-fold>

    //<editor-fold desc="ItemStackHandler Methods--------------------------------------------------------------------------------------------">
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            if(facing == null) return true;
            return false;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            if(facing == null) return (T) moneySlot;
        }
        return super.getCapability(capability, facing);
    }
    //</editor-fold>

    public EntityPlayer getPlayerUsing(){
        return playerUsing;
    }

    public void voidPlayerUsing(){
        playerUsing = null;
    }
}
