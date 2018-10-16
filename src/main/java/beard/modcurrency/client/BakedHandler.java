package beard.modcurrency.client;

import beard.modcurrency.ModCurrency;
import beard.modcurrency.item.EnumCurrencyPrime;
import beard.modcurrency.item.EnumCurrencyShape;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
        ModelResourceLocation mrl = new ModelResourceLocation(ModCurrency.itemCurrency.getRegistryName(), "inventory");
        IBakedModel main = event.getModelRegistry().getObject(new ModelResourceLocation(ModCurrency.itemCurrency.getRegistryName(), "inventory"));

        IBakedModel shapeModel[] = new IBakedModel[EnumCurrencyShape.values().length];
        IBakedModel primeModel[] = new IBakedModel[EnumCurrencyPrime.values().length];

        for (int i = 0; i < EnumCurrencyShape.values().length; i++) {
            shapeModel[i] = event.getModelRegistry().getObject(new ModelResourceLocation(ModCurrency.itemCurrency.getRegistryName() + "shape/" + EnumCurrencyShape.values()[i].getName(), "inventory"));
        }

        for (int j = 0; j < EnumCurrencyPrime.values().length; j++) {
            primeModel[j] = event.getModelRegistry().getObject(new ModelResourceLocation(ModCurrency.itemCurrency.getRegistryName() + "primedetail/" + EnumCurrencyPrime.values()[j].getName(), "inventory"));

            event.getModelRegistry().putObject(mrl, new BakedModelCurrency(main, shapeModel, primeModel));
        }











    }
}
