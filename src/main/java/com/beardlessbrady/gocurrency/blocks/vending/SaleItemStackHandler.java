package com.beardlessbrady.gocurrency.blocks.vending;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * Created by BeardlessBrady on 2021-08-26 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class SaleItemStackHandler extends OverloadedItemStackHandler{
    NonNullList<Integer> prices;

    public SaleItemStackHandler() {
        super();
    }

    public SaleItemStackHandler(int size) {
        super(size);
    }

    public SaleItemStackHandler(NonNullList<ItemStack> add) {
        super(add);
    }
}
