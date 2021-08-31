package com.beardlessbrady.gocurrency;

import com.beardlessbrady.gocurrency.blocks.vending.VendingBlock;
import com.beardlessbrady.gocurrency.blocks.vending.VendingTile;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Created by BeardlessBrady on 2021-08-31 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class EventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void protectedBreak(PlayerInteractEvent.LeftClickBlock e) {
        Block brokeBlock = e.getWorld().getBlockState(e.getPos()).getBlock();

        if (brokeBlock instanceof VendingBlock) {
            TileEntity tile = e.getWorld().getTileEntity(e.getPos());
            if(tile instanceof VendingTile) {
                if (!((e.getPlayer().getUniqueID().equals(((VendingTile)tile).getOwner())) || e.getPlayer().isCreative())) {     //If not Owner (and not in creative) Can't Break
                    e.setCanceled(true);
                }
            }
        }
    }
}
