package beardlessbrady.modcurrency.handler;

import beardlessbrady.modcurrency.block.TileEconomyBase;
import beardlessbrady.modcurrency.block.vending.BlockVending;
import net.minecraft.block.Block;
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

        if (brokeBlock == new BlockVending()) {
            TileEconomyBase tile = (TileEconomyBase) e.getWorld().getTileEntity(e.getPos());
            if (e.getWorld().getBlockState(e.getPos()).getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP)
                tile =  (TileEconomyBase) e.getWorld().getTileEntity(e.getPos().down());
            if ((!e.getEntityPlayer().getUniqueID().toString().equals(tile.getOwner())) && !e.getEntityPlayer().isCreative()) {     //If not Owner (and not in creative) Can't Break
                e.setCanceled(true);
            }


        }
    }
}