package gunn.modcurrency.mod.tileentity;

import gunn.modcurrency.mod.ModCurrency;
import gunn.modcurrency.mod.client.gui.util.INBTInventory;
import gunn.modcurrency.mod.worldsaveddata.bank.BankAccount;
import gunn.modcurrency.mod.worldsaveddata.bank.BankAccountSavedData;
import gunn.modcurrency.mod.network.PacketHandler;
import gunn.modcurrency.mod.network.PacketSyncBankDataToClient;
import gunn.modcurrency.mod.item.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
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
public class TileATM extends TileEntity implements ICapabilityProvider, INBTInventory, IOwnable {
    private ItemStackHandler moneySlot;
    private EntityPlayer playerUsing = null;
    private String owner;
    boolean isOwner, gearExtended;
    private int fee;

    public TileATM() {
        moneySlot = new ItemStackHandler(1);
        owner = "";
        fee = 0;
        isOwner = true;
        gearExtended = false;
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
            if (amount <= account.getBalance() - this.fee && amount <= 6400) {
                ItemStack cash = new ItemStack(ModItems.itemBanknote);

                if (amount % 100 == 0) {
                    cash.setCount(amount / 100);
                    cash.setItemDamage(5);
                    moneySlot.setStackInSlot(0, cash);
                }else if (amount % 50 == 0) {
                    cash.setCount(amount / 50);
                    cash.setItemDamage(4);
                    moneySlot.setStackInSlot(0, cash);
                }else if (amount % 20 == 0) {
                    cash.setCount(amount / 20);
                    cash.setItemDamage(3);
                    moneySlot.setStackInSlot(0, cash);
                }else if (amount % 10 == 0) {
                    cash.setCount(amount / 10);
                    cash.setItemDamage(2);
                    moneySlot.setStackInSlot(0, cash);
                }else if (amount % 5 == 0) {
                    cash.setCount(amount / 5);
                    cash.setItemDamage(1);
                    moneySlot.setStackInSlot(0, cash);
                }else{
                    cash.setCount(amount);
                    cash.setItemDamage(0);
                    moneySlot.setStackInSlot(0, cash);
                }
                account.setBalance(account.getBalance() - amount - this.fee);
                payOwner(this.fee);
                syncBankAccountData(account);
            }
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
                    if(amount - this.fee >= 1 || isOwner) {
                        moneySlot.setStackInSlot(0, ItemStack.EMPTY);

                        BankAccountSavedData bankData = BankAccountSavedData.getData(world);
                        BankAccount account = bankData.getBankAccount(playerUsing.getUniqueID().toString());

                        account.setBalance(account.getBalance() + amount - this.fee);
                        payOwner(this.fee);

                        syncBankAccountData(account);
                    }
                }
            }
        }
    }

    public void payOwner(int amount){
        BankAccountSavedData bankData = BankAccountSavedData.getData(getWorld());
        BankAccount account = bankData.getBankAccount(owner);
        account.setBalance(account.getBalance() + amount);
        syncBankAccountData(account);
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
        compound.setInteger("fee", fee);
        compound.setString("owner", owner);
        compound.setBoolean("gearExtended", gearExtended);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("moneySlot")) moneySlot.deserializeNBT((NBTTagCompound) compound.getTag("moneySlot"));
        if (compound.hasKey("fee")) fee = compound.getInteger("fee");
        if (compound.hasKey("owner")) owner = compound.getString("owner");
        if (compound.hasKey("gearExtended")) gearExtended = compound.getBoolean("gearExtended");
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
        tag.setInteger("fee", fee);
        tag.setBoolean("gearExtended", gearExtended);

        return new SPacketUpdateTileEntity(pos, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        owner = pkt.getNbtCompound().getString("owner");
        fee = pkt.getNbtCompound().getInteger("fee");
        gearExtended = pkt.getNbtCompound().getBoolean("gearExtended");
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

    //<editor-fold desc="Getter & Setter Methods---------------------------------------------------------------------------------------------">
    public int getFieldCount(){
        return 3;
    }

    public void setField(int id, int value){
        switch(id){
            case 0:
                isOwner = value == 1;
                break;
            case 1:
                gearExtended = (value == 1);
                break;
            case 2:
                fee = value;
                break;
        }
    }

    public int getField(int id){
        switch(id){
            case 0:
                world.markBlockRangeForRenderUpdate(pos, pos);
                world.scheduleBlockUpdate(pos,this.getBlockType(),0,0);
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                markDirty();
                return (isOwner) ? 1 : 0;
            case 1:
                return (gearExtended) ? 1 : 0;
            case 2:
                return fee;
        }
        return -1;
    }

    public EntityPlayer getPlayerUsing(){
        return playerUsing;
    }

    public void voidPlayerUsing(){
        playerUsing = null;
    }

    public void setOwner(String player){
        owner = player;
    }

    public String getOwner(){
        return owner;
    }
    //</editor-fold>


}
