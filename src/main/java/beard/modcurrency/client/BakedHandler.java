package beard.modcurrency.client;

import beard.modcurrency.ModCurrency;
import beard.modcurrency.item.ItemCurrency;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2018-09-27
 */

public class BakedHandler {

    @SubscribeEvent
    public void modelBake(ModelBakeEvent event){
        IBakedModel models;
            models = event.getModelRegistry().getObject(new ModelResourceLocation(ModCurrency.MODID + ":" + "currencyext1", "inventory"));

            ModelResourceLocation mrl = new ModelResourceLocation(ModCurrency.MODID + ":" + "currency", "inventory");

            IBakedModel main = event.getModelRegistry().getObject(mrl);

            event.getModelRegistry().putObject(mrl, new BakedModelCurrency(main, models));
    }
}
