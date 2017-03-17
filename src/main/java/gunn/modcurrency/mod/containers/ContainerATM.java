package gunn.modcurrency.mod.containers;

import gunn.modcurrency.mod.util.INBTInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-03-16
 */
public class ContainerATM extends Container implements INBTInventory{
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}
