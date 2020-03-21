package beardlessbrady.modcurrency.block.designer;

import beardlessbrady.modcurrency.ModCurrency;
import beardlessbrady.modcurrency.block.economyblocks.TileEconomyBase;
import beardlessbrady.modcurrency.block.economyblocks.tradein.TileTradein;
import beardlessbrady.modcurrency.handler.StateHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2020-03-20
 */
public class TileDesigner extends TileEntity {

    /** To open the GUI **/
    public void openGui(EntityPlayer player, World world, BlockPos pos){
        player.openGui(ModCurrency.instance, 32, world, pos.getX(), pos.getY(), pos.getZ());
    }
}
