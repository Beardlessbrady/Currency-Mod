package com.beardlessbrady.gocurrency.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

/**
 * Created by BeardlessBrady on 2021-02-25 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class ModItemGroup extends ItemGroup {
    private final Supplier<ItemStack> iconSupplier;

    public ModItemGroup(String label, final Supplier<ItemStack> iconSupplier) {
        super(label);
        this.iconSupplier = iconSupplier;
    }

    @Override
    public ItemStack createIcon() {
        return iconSupplier.get();
    }
}
