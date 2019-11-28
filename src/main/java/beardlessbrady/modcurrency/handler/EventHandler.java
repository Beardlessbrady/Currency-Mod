package beardlessbrady.modcurrency.handler;

import beardlessbrady.modcurrency.block.ModBlocks;
import beardlessbrady.modcurrency.block.TileEconomyBase;
import beardlessbrady.modcurrency.block.vending.BlockVending;
import beardlessbrady.modcurrency.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

        if (brokeBlock == ModBlocks.blockVending) {
            TileEconomyBase tile = (TileEconomyBase) e.getWorld().getTileEntity(e.getPos());
            if (e.getWorld().getBlockState(e.getPos()).getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP)
                tile =  (TileEconomyBase) e.getWorld().getTileEntity(e.getPos().down());

            if (!((e.getEntityPlayer().getUniqueID().equals(tile.getOwner())) || e.getEntityPlayer().isCreative())) {     //If not Owner (and not in creative) Can't Break
                e.setCanceled(true);
            }
        }
    }
}