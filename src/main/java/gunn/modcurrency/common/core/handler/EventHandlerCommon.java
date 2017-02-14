package gunn.modcurrency.common.core.handler;

import gunn.modcurrency.ModConfig;
import gunn.modcurrency.ModCurrency;
import gunn.modcurrency.api.ModTile;
import gunn.modcurrency.client.model.BakedModelVendor;
import gunn.modcurrency.common.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.SidedProxy;
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
            if (brokeBlock == ModBlocks.blockSeller || brokeBlock == ModBlocks.blockVendor || brokeBlock == ModBlocks.blockTop) {
                ModTile tile = (ModTile) e.getWorld().getTileEntity(e.getPos());
                if ((brokeBlock == ModBlocks.blockTop && (e.getWorld().getBlockState(e.getPos()).getBlock() != ModBlocks.blockVendor ||
                        e.getWorld().getBlockState(e.getPos()).getBlock() != ModBlocks.blockSeller)))
                    break cancelEventChange;
                if (brokeBlock == ModBlocks.blockTop) tile = (ModTile) e.getWorld().getTileEntity(e.getPos().down());
                if ((!e.getEntityPlayer().getUniqueID().toString().equals(tile.getOwner())) && !e.getEntityPlayer().isCreative()) {     //If not Owner (and not in creative) Can't Break
                    e.setCanceled(true);
                }
            }
        }
    }

        /* MAKE A CLIENT ONLY EVENT HANLDER, THROW IN THERE
        @SubscribeEvent
        public void onModelBake(ModelBakeEvent e){
            ModelResourceLocation base = new ModelResourceLocation(ModCurrency.MODID + ":blockvendor","color=blue,facing=north,item=false,open=true");
            IBakedModel basicVendor = e.getModelRegistry().getObject(base);

            e.getModelRegistry().putObject(base, new BakedModelVendor(basicVendor));
        }
        */
}
