package beardlessbrady.modcurrency2.handler;

import beardlessbrady.modcurrency2.item.ModItems;
import beardlessbrady.modcurrency2.item.playercurrency.BakedPlayerCurrency;
import beardlessbrady.modcurrency2.item.playercurrency.EnumCurrencyPrime;
import beardlessbrady.modcurrency2.item.playercurrency.EnumCurrencyShape;
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
        ModelResourceLocation mrl = new ModelResourceLocation(ModItems.itemPlayerCurrency.getRegistryName() + "/playercurrency", "inventory");
        IBakedModel main = event.getModelRegistry().getObject(mrl);

        // PLAYER CURRENCY SHAPE ASPECT
        IBakedModel shapeModel[] = new IBakedModel[EnumCurrencyShape.values().length];
        for (int i = 0; i < EnumCurrencyShape.values().length; i++) {
            shapeModel[i] = event.getModelRegistry().getObject(new ModelResourceLocation(ModItems.itemPlayerCurrency.getRegistryName() + "/shape/" + EnumCurrencyShape.values()[i].getName(), "inventory"));
        }

        // PLAYER CURRENCY PRIMEDETAIL ASPECT
        IBakedModel primeModel[] = new IBakedModel[EnumCurrencyPrime.values().length];
        for (int j = 0; j < EnumCurrencyPrime.values().length; j++) {
            primeModel[j] = event.getModelRegistry().getObject(new ModelResourceLocation(ModItems.itemPlayerCurrency.getRegistryName() + "/primedetail/" + EnumCurrencyPrime.values()[j].getName(), "inventory"));

            event.getModelRegistry().putObject(mrl, new BakedPlayerCurrency(main, shapeModel, primeModel));
        }
    }
}
