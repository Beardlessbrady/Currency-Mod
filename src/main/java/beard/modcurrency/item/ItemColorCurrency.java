package beard.modcurrency.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2018-02-25
 */
public class ItemColorCurrency implements IItemColor{

    @Override
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        return 0x848920;
    }
}
