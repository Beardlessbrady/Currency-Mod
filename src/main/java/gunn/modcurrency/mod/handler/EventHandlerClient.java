package gunn.modcurrency.mod.handler;

import gunn.modcurrency.mod.ModCurrency;
import gunn.modcurrency.mod.client.model.BakedModelVending;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-12-24
 */
public class EventHandlerClient {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onModelBake(ModelBakeEvent e){
       //  ModelResourceLocation base = new ModelResourceLocation(ModCurrency.MODID + ":blockvending", "facing=north");
       //     IBakedModel basicVendor = e.getModelRegistry().getObject(base);

           // e.getModelRegistry().putObject(base, new BakedModelVending(basicVendor));
    }
}
