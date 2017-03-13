package gunn.modcurrency.mod.handler;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-02-26
 */
public class EventHandlerClient extends EventHandlerCommon{

        //@SubscribeEvent
        //public void onModelBake(ModelBakeEvent e){
       //     ModelResourceLocation base = new ModelResourceLocation(ModCurrency.MODID + ":blockvendor","color=blue,facing=north,item=false,open=true");
        //    IBakedModel basicVendor = e.getModelRegistry().getObject(base);

       //     e.getModelRegistry().putObject(base, new BakedModelVendor(basicVendor));
       // }

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent e){


    }

}
