package com.beardlessbrady.gocurrency.blocks.vending;

import com.beardlessbrady.gocurrency.other.StockStackHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.function.Predicate;

/**
 * Created by BeardlessBrady on 2021-03-01 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class VendingStockContents implements IInventory {
    private final StockStackHandler vendingComponentContents;
    private Predicate<PlayerEntity> canPlayerAccess = x-> true;
    private Notify markDirtyNotification = ()->{};
    private final Notify openInventoryNotificationLambda = ()->{};
    private final Notify closeInventoryNotificationLambda = ()->{};

    @FunctionalInterface
    public interface Notify {
        void invoke();
    }

    // Server side initialization
    VendingStockContents(int size, Predicate<PlayerEntity> canPlayerAccess, Notify notify) {
        this.vendingComponentContents = new StockStackHandler(size);
        this.canPlayerAccess = canPlayerAccess;
        this.markDirtyNotification = notify;
    }

    // Client side initialization
    VendingStockContents(int size) {
        this.vendingComponentContents = new StockStackHandler(size);
    }

    // ----- Setters and Getters -----
    public void setPlayerAccess(Predicate<PlayerEntity> canPlayerAccess){
        this.canPlayerAccess = canPlayerAccess;
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return canPlayerAccess.test(player); // Only does anything on server side
    }

    public void setMarkDirty(Notify mark){
        this.markDirtyNotification = mark;
    }

    @Override
    public void markDirty() {
        this.markDirtyNotification.invoke();
    }

    @Override
    public void openInventory(PlayerEntity player) {
        openInventoryNotificationLambda.invoke();
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        closeInventoryNotificationLambda.invoke();
    }

    // ---- NBT STUFF -----
    public CompoundNBT serializeNBT() {
        return vendingComponentContents.serializeNBT();
    }

    public void deserializeNBT(CompoundNBT nbt){
        vendingComponentContents.deserializeNBT(nbt);
    }

    // ---- Called by VANILLA to manipulate inventory contents
    @Override
    public int getSizeInventory() {
        return vendingComponentContents.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for(int i = 0; i < vendingComponentContents.getSlots(); i++){
            if (! vendingComponentContents.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return vendingComponentContents.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (count < 0) throw new IllegalArgumentException("count should be >= 0:" + count);
        return vendingComponentContents.extractItem(index, count, false);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        int maxStackSize = vendingComponentContents.getSlotLimit(index);
        return vendingComponentContents.extractItem(index, maxStackSize, false);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        vendingComponentContents.setStackInSlot(index, stack);
    }

    @Override
    public void clear() {
        for(int i = 0; i < vendingComponentContents.getSlots(); i++){
            vendingComponentContents.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public ItemStack increaseStackSize(int index, ItemStack itemStackToInsert) {
        ItemStack leftoverItemStack = vendingComponentContents.insertItem(index, itemStackToInsert, false);
        return leftoverItemStack;
    }

    public boolean doesItemStackFit(int index, ItemStack itemStackToInsert) {
        ItemStack leftoverItemStack = vendingComponentContents.insertItem(index, itemStackToInsert, true);
        return leftoverItemStack.isEmpty();
    }
}
