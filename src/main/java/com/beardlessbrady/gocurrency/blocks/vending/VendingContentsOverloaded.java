package com.beardlessbrady.gocurrency.blocks.vending;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IntArray;
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
    protected final SaleItemStackHandler vendingComponentContents;
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
        this.vendingComponentContents = new SaleItemStackHandler(size);
        this.canPlayerAccess = canPlayerAccess;
        this.markDirtyNotification = notify;
    }

    // Client side initialization
    VendingContentsOverloaded(int size) {
        this.vendingComponentContents = new SaleItemStackHandler(size);
    }

    // ----- Setters and Getters -----
    public void setPlayerAccess(Predicate<PlayerEntity> canPlayerAccess){
        this.canPlayerAccess = canPlayerAccess;
    }

    public int[] getPriceInt(int index){
        return vendingComponentContents.getPriceInt(index);
    }

    public String getPriceString(int index){
        return vendingComponentContents.getPriceString(index);
    }

    public void setPrice(int index, int[] price){
        vendingComponentContents.setPrice(index, price);
    }

    public void setPrice(int index, String price){
        String[] prices = price.split("[.]");

        int priceM;
        int priceT;

        if(prices[0].length() > 4) {
            priceM = Integer.parseInt(prices[0].substring(0, prices[0].length() - 4));
            priceT = Integer.parseInt(prices[0].substring(prices[0].length() - 4));
        } else {
            priceM = 0;
            priceT = Integer.parseInt(prices[0]);
        }

        setPrice(index, new int[] {priceM, priceT, Integer.parseInt(prices[1])});
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

    public int getStackSize(int slot){
        return vendingComponentContents.getSizeInSlot(slot);
    }

    public IntArray getStackSizeIntArray(){
        return vendingComponentContents.getStackSizeIntArray();
    }

    public IntArray getIntArrayMillion(){
        return vendingComponentContents.getPriceMillionArray();
    }

    public IntArray getIntArrayThousand(){
        return vendingComponentContents.getPriceThousandArray();
    }

    public IntArray getIntArrayCent(){
        return vendingComponentContents.getPriceCentArray();
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
        // Vanilla, try to use the SetStackAndSize when possible!!!
        vendingComponentContents.setStackInSlot(index, stack);
    }

    public void setStackAndSize(int index, ItemStack stack, int size) {
        stack.setCount(1);
        vendingComponentContents.setStackInSlot(index, stack);
        vendingComponentContents.setSizeInSlot(index, size);
    }

    public void setStackAndSize(int index, ItemStack stack) {
        int count = stack.getCount();
        stack.setCount(1);
        setStackAndSize(index, stack, count);
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

    public int getStackLimit(int index){
        return vendingComponentContents.getStackLimit(index, null);
    }

    @Override
    public void clear() {
        for(int i = 0; i < vendingComponentContents.getSlots(); i++){
            vendingComponentContents.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public boolean isDirty(){
       return vendingComponentContents.isDirty();
    }
}
