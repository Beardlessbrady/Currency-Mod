package com.beardlessbrady.gocurrency.blocks.vending;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Predicate;

/**
 * Created by BeardlessBrady on 2021-05-24 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class VendingContentsBuffer extends VendingContents {
    private int[] buffer;
    private int maxBufferSize = 128;


    VendingContentsBuffer(int size, Predicate<PlayerEntity> canPlayerAccess, Notify notify) {
        super(size, canPlayerAccess, notify);
        buffer = new int[size];
    }

    // Client side initialization
    VendingContentsBuffer(int size) {
        super(size);
        buffer = new int[size];
    }

    public int getTotalMaxSize(int index) {
        return maxBufferSize + vendingComponentContents.getSlotLimit(index);
    }
    
    public int getBuffer(int index){
        return buffer[index];
    }
    
    public void setBuffer(int index, int amnt){
        buffer[index] = amnt;
    }

    public boolean canGrow(int index, int amnt){
        return buffer[index] + amnt <= maxBufferSize;
    }

    public int growthAmount(int index){
        return maxBufferSize - buffer[index];
    }

    public boolean growBuffer(int index, int amnt){
        if(amnt + buffer[index] <= maxBufferSize){
            buffer[index] = buffer[index] + amnt;
            return true;
        } else {
            return false;
        }
    }

    // Moves stack count to the buffer, reducing stack to 1
    public boolean shrinkBuffer(int index, int amnt){
        if(buffer[index] - amnt >= 0){
            buffer[index] = buffer[index] - amnt;
            return true;
        } else {
            return false;
        }
    }

    public void countToBuffer(int index){
        if(index >= 0 && index <= buffer.length) {
            int count = vendingComponentContents.getStackInSlot(index).getCount();
            vendingComponentContents.getStackInSlot(index).setCount(1);
            buffer[index] = buffer[index] + (count - 1);
        }
    }

    // ---- NBT STUFF -----
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putIntArray("buffer", buffer);
        nbt.putInt("max", maxBufferSize);
        nbt.put("stack",  vendingComponentContents.serializeNBT());

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt){
        if(nbt.contains("buffer"))
            buffer = nbt.getIntArray("buffer");

        if(nbt.contains("max"))
            maxBufferSize = nbt.getInt("max");

        if(nbt.contains("stack"))
            vendingComponentContents.deserializeNBT(nbt.getCompound("stack"));
    }
}
