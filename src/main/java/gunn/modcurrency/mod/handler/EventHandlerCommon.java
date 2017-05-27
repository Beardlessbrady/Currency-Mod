package gunn.modcurrency.mod.handler;

import gunn.modcurrency.mod.ModConfig;
import gunn.modcurrency.mod.block.ModBlocks;
import gunn.modcurrency.mod.tileentity.IOwnable;
import net.minecraft.block.Block;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-12-24
 */
public class EventHandlerCommon {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void protectedBreak(PlayerInteractEvent.LeftClickBlock e) {
        Block brokeBlock = e.getWorld().getBlockState(e.getPos()).getBlock();

        //Invincible Machines
        if (ModConfig.invincibleVendSell) {
            cancelEventChange:
            if (brokeBlock == ModBlocks.blockVending || brokeBlock == ModBlocks.blockATM) {
                IOwnable tile = (IOwnable) e.getWorld().getTileEntity(e.getPos());
                if ((!e.getEntityPlayer().getUniqueID().toString().equals(tile.getOwner())) && !e.getEntityPlayer().isCreative()) {     //If not Owner (and not in creative) Can't Break
                    e.setCanceled(true);
                }
            }
        }
    }
}
