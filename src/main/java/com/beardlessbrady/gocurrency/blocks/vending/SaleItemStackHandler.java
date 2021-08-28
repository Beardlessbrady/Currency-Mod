package com.beardlessbrady.gocurrency.blocks.vending;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

/**
 * Created by BeardlessBrady on 2021-08-26 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class SaleItemStackHandler extends OverloadedItemStackHandler{
    private NonNullList<String> prices;

    public SaleItemStackHandler() {
        super();
    }

    public SaleItemStackHandler(int size) {
        super(size);
        prices = NonNullList.withSize(size, "0.00");
    }

    public SaleItemStackHandler(NonNullList<ItemStack> add) {
        super(add);
        prices = NonNullList.withSize(add.size(), "0.00");
    }

    @Override
    public void setSize(int size) {
        super.setSize(size);
        prices = NonNullList.withSize(size, "0.00");
    }

    public String getPriceInSlot(int index){
        return prices.get(index);
    }

    public void setPriceInSlot(int index, String price){
        prices.set(index, price);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();

        CompoundNBT priceNBT = new CompoundNBT();
        for (int i = 0; i < prices.size(); i++) {
            priceNBT.putString(Integer.toString(i), prices.get(i));
        }
        nbt.put ("prices", priceNBT);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        CompoundNBT priceNBT = nbt.getCompound("prices");
        for (int i = 0; i < priceNBT.size(); i++) {
            prices.set(i, priceNBT.getString(Integer.toString(i)));
        }
        onLoad();
    }
}
