package com.beardlessbrady.gocurrency.init;

import com.beardlessbrady.gocurrency.GOCurrency;
import com.beardlessbrady.gocurrency.blocks.vending.VendingContainerScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Created by BeardlessBrady on 2021-03-02 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
@Mod.EventBusSubscriber(modid = GOCurrency.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventSubscriber {

    @SubscribeEvent
    public static void onFMLClientSetupEvent(final FMLClientSetupEvent event){
        // Register Container Screens
        DeferredWorkQueue.runLater(() -> {
            ScreenManager.registerFactory(CommonRegistry.CONTAINER_VENDING.get(), VendingContainerScreen::new);
        });
    }
}
