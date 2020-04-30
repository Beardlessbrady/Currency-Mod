package beardlessbrady.modcurrency2.handler;

import beardlessbrady.modcurrency2.block.BlockBase;
import beardlessbrady.modcurrency2.block.economyblocks.TileEconomyBase;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-06-03
 */

public class EventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void protectedBreak(PlayerInteractEvent.LeftClickBlock e) {
        Block brokeBlock = e.getWorld().getBlockState(e.getPos()).getBlock();

        if (brokeBlock instanceof BlockBase) {
            TileEntity tile = e.getWorld().getTileEntity(e.getPos());
            if(tile instanceof TileEconomyBase) {
                if (e.getWorld().getBlockState(e.getPos()).getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP)
                    tile = e.getWorld().getTileEntity(e.getPos().down());

                if (!((e.getEntityPlayer().getUniqueID().equals(((TileEconomyBase)tile).getOwner())) || e.getEntityPlayer().isCreative())) {     //If not Owner (and not in creative) Can't Break
                    e.setCanceled(true);
                }
            }
        }
    }
}