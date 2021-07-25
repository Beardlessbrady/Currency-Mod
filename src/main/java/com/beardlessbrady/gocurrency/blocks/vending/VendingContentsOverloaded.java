package com.beardlessbrady.gocurrency.blocks.vending;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Predicate;

/**
 * Created by BeardlessBrady on 2021-03-01 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class VendingContentsOverloaded implements IInventory {
    protected final OverloadedItemStackHandler vendingComponentContents;
    protected Predicate<PlayerEntity> canPlayerAccess = x-> true;
    protected Notify markDirtyNotification = ()->{};
    protected final Notify openInventoryNotificationLambda = ()->{};
    protected final Notify closeInventoryNotificationLambda = ()->{};

    @FunctionalInterface
    public interface Notify {
        void invoke();
    }

    // Server side initialization
    VendingContentsOverloaded(int size, Predicate<PlayerEntity> canPlayerAccess, Notify notify) {
        this.vendingComponentContents = new OverloadedItemStackHandler(size);
        this.canPlayerAccess = canPlayerAccess;
        this.markDirtyNotification = notify;
    }

    // Client side initialization
    VendingContentsOverloaded(int size) {
        this.vendingComponentContents = new OverloadedItemStackHandler(size);
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

    public int getSizeInSlot(int index){
        return vendingComponentContents.getSizeInSlot(index);
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

    public ItemStack growInventorySlotSize(int index, ItemStack stack){
        if(ItemHandlerHelper.canItemStacksStack(stack, vendingComponentContents.getStackInSlot(index))){
            int leftover = vendingComponentContents.growStackSize(index, stack.getCount());
            if(leftover == 0){ // No leftover
                return ItemStack.EMPTY;
            } else {
                ItemStack copy = stack.copy();
                copy.setCount(leftover);
                return copy;
            }
        } else {
            return stack;
        }
    }

    @Override
    public void clear() {
        for(int i = 0; i < vendingComponentContents.getSlots(); i++){
            vendingComponentContents.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public int getStackSize(int slot){
        return vendingComponentContents.getSizeInSlot(slot);
    }

    public StackSizeIntArray getStackSizeIntArray(){
        return vendingComponentContents.getStackSizeIntArray();
    }

    public void setStackSizeIntArray(StackSizeIntArray i){
        vendingComponentContents.setStackSizeIntArray(i);
    }

    // ---- MY NEW MANIPULATION METHODS
}
