package beardlessbrady.modcurrency.block.vending;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-10
 */

public class ContainerVending extends Container {

    public ContainerVending(EntityPlayer player, TileVending tile){

    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}
