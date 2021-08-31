package com.beardlessbrady.gocurrency.blocks.vending;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IntArray;
import net.minecraft.util.NonNullList;

import java.util.List;

/**
 * Created by BeardlessBrady on 2021-08-26 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class SaleItemStackHandler extends OverloadedItemStackHandler{
    private IntArray priceDollar;
    private IntArray priceCent;

    public SaleItemStackHandler() {
        super();
    }

    public SaleItemStackHandler(int size) {
        super(size);
        priceDollar = new IntArray(size);
        priceCent = new IntArray(size);
    }

    public SaleItemStackHandler(NonNullList<ItemStack> add) {
        super(add);
        priceDollar = new IntArray(add.size());
        priceCent = new IntArray(add.size());
    }

    @Override
    public void setSize(int size) {
        super.setSize(size);
        priceDollar = new IntArray(size);
        priceCent = new IntArray(size);
    }

    public int[] getPriceInSlot(int index){
        return new int[] {priceDollar.get(index), priceCent.get(index)};
    }

    public void setPriceInSlot(int index, int[] price){
        this.priceDollar.set(index, price[0]);
        this.priceCent.set(index, price[1]);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();

        int[] tempDollar = new int[priceDollar.size()];
        int[] tempCent = new int[priceCent.size()];
        for (int i = 0; i < tempDollar.length; i++) {
            tempDollar[i] = priceDollar.get(i);
            tempCent[i] = priceCent.get(i);
        }

        nbt.putIntArray("priceDollar", tempDollar);
        nbt.putIntArray("priceCent", tempCent);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);

        int[] tempDollar = nbt.getIntArray("priceDollar");
        int[] tempCent = nbt.getIntArray("priceCent");
        priceDollar = new IntArray(tempDollar.length);
        for (int i = 0; i < tempDollar.length; i++) {
            priceDollar.set(i, tempDollar[i]);
            priceCent.set(i, tempCent[i]);
        }
    }

    public IntArray getPriceDollarArray(){
        return priceDollar;
    }

    public IntArray getPriceCentArray(){
        return priceCent;
    }
}
