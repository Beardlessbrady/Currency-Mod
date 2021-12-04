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
    private IntArray priceMillion; // 9999
    private IntArray priceThousand; // 9999
    private IntArray priceCent; // .99
    // 99'999'999.99

    public SaleItemStackHandler() {
        super();
    }

    public SaleItemStackHandler(int size) {
        super(size);
        priceMillion = new IntArray(size);
        priceThousand = new IntArray(size);
        priceCent = new IntArray(size);
    }

    public SaleItemStackHandler(NonNullList<ItemStack> add) {
        priceMillion = new IntArray(add.size());
        priceThousand = new IntArray(add.size());
        priceCent = new IntArray(add.size());
    }

    @Override
    public void setSize(int size) {
        super.setSize(size);
        priceMillion = new IntArray(size);
        priceThousand = new IntArray(size);
        priceCent = new IntArray(size);
    }

    public int[] getPriceInt(int index){
        return new int[] {priceMillion.get(index), priceThousand.get(index), priceCent.get(index)};
    }

    public String getPriceString(int index){
        return String.valueOf(priceMillion.get(index)) + String.valueOf(priceThousand.get(index)) + "." + String.valueOf(priceCent.get(index));
    }

    public void setPrice(int index, int[] price){
        this.priceMillion.set(index, price[0]);
        this.priceThousand.set(index, price[1]);
        this.priceCent.set(index, price[2]);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();

        int[] tempMillion = new int[priceMillion.size()];
        int[] tempThousand = new int[priceThousand.size()];
        int[] tempCent = new int[priceCent.size()];
        for (int i = 0; i < tempMillion.length; i++) {
            tempMillion[i] = priceMillion.get(i);
            tempThousand[i] = priceThousand.get(i);
            tempCent[i] = priceCent.get(i);
        }

        nbt.putIntArray("priceMillion", tempMillion);
        nbt.putIntArray("priceThousand", tempThousand);
        nbt.putIntArray("priceCent", tempCent);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);

        int[] tempMillion = nbt.getIntArray("priceMillion");
        int[] tempThousand = nbt.getIntArray("priceThousand");
        int[] tempCent = nbt.getIntArray("priceCent");
        priceMillion = new IntArray(tempMillion.length);
        for (int i = 0; i < tempMillion.length; i++) {
            priceMillion.set(i, tempMillion[i]);
            priceThousand.set(i, tempThousand[i]);
            priceCent.set(i, tempCent[i]);
        }
    }

    public IntArray getPriceMillionArray(){
        return priceMillion;
    }

    public IntArray getPriceThousandArray(){
        return priceThousand;
    }

    public IntArray getPriceCentArray(){
        return priceCent;
    }
}
