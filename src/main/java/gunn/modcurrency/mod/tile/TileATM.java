package gunn.modcurrency.mod.tile;

import gunn.modcurrency.mod.ModCurrency;
import gunn.modcurrency.mod.core.data.BankAccount;
import gunn.modcurrency.mod.core.data.BankAccountSavedData;
import gunn.modcurrency.mod.core.network.PacketHandler;
import gunn.modcurrency.mod.core.network.PacketSyncBankDataToClient;
import gunn.modcurrency.mod.item.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
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

        syncBankAccountData(account);
    }

    public void withdraw(int amount) {
        if (moneySlot.getStackInSlot(0) == ItemStack.EMPTY) {
            BankAccountSavedData bankData = BankAccountSavedData.getData(getWorld());
            BankAccount account = bankData.getBankAccount(playerUsing.getUniqueID().toString());
            if (amount <= account.getBalance() && amount <= 6400) {
                ItemStack cash = new ItemStack(ModItems.itemBanknote);

                if (amount % 100 == 0) {
                    cash.setCount(amount / 100);
                    cash.setItemDamage(5);
                    moneySlot.setStackInSlot(0, cash);
                    account.setBalance(account.getBalance() - amount);
                }else if (amount % 50 == 0) {
                    cash.setCount(amount / 50);
                    cash.setItemDamage(4);
                    moneySlot.setStackInSlot(0, cash);
                    account.setBalance(account.getBalance() - amount);
                }else if (amount % 20 == 0) {
                    cash.setCount(amount / 20);
                    cash.setItemDamage(3);
                    moneySlot.setStackInSlot(0, cash);
                    account.setBalance(account.getBalance() - amount);
                }else if (amount % 10 == 0) {
                    cash.setCount(amount / 10);
                    cash.setItemDamage(2);
                    moneySlot.setStackInSlot(0, cash);
                    account.setBalance(account.getBalance() - amount);
                }else if (amount % 5 == 0) {
                    cash.setCount(amount / 5);
                    cash.setItemDamage(1);
                    moneySlot.setStackInSlot(0, cash);
                    account.setBalance(account.getBalance() - amount);
                }else{
                    cash.setCount(amount);
                    cash.setItemDamage(0);
                    moneySlot.setStackInSlot(0, cash);
                    account.setBalance(account.getBalance() - amount);
                }
            }
            syncBankAccountData(account);
        }
    }

    public void deposit() {
        if (!world.isRemote) {
            if (moneySlot.getStackInSlot(0) != ItemStack.EMPTY) {
                if (moneySlot.getStackInSlot(0).getItem() == ModItems.itemBanknote) {
                    int amount;
                    switch (moneySlot.getStackInSlot(0).getItemDamage()) {
                        case 0:
                            amount = 1;
                            break;
                        case 1:
                            amount = 5;
                            break;
                        case 2:
                            amount = 10;
                            break;
                        case 3:
                            amount = 20;
                            break;
                        case 4:
                            amount = 50;
                            break;
                        case 5:
                            amount = 100;
                            break;
                        default:
                            amount = -1;
                            break;
                    }
                    amount = amount * moneySlot.getStackInSlot(0).getCount();
                    moneySlot.setStackInSlot(0, ItemStack.EMPTY);

                    BankAccountSavedData bankData = BankAccountSavedData.getData(world);
                    BankAccount account = bankData.getBankAccount(playerUsing.getUniqueID().toString());

                    account.setBalance(account.getBalance() + amount);
                    syncBankAccountData(account);
                }
            }
        }
    }

    public void syncBankAccountData(BankAccount account){
        if(!world.isRemote){
            BankAccountSavedData bankData = BankAccountSavedData.getData(world);
            bankData.setBankAccount(account);
            PacketSyncBankDataToClient pack = new PacketSyncBankDataToClient();
            pack.setData(account);
            PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) playerUsing);
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
