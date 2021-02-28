package com.beardlessbrady.gocurrency.handlers;

import com.beardlessbrady.gocurrency.items.CurrencyItem;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Created by BeardlessBrady on 2021-02-27 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class ClientRegistryHandler {
    private final IEventBus eventBus;

    public ClientRegistryHandler(IEventBus eventBus){
        this.eventBus = eventBus;
    }

    public static void doClientStuff() {
        ItemModelsProperties.registerProperty(CommonRegistryHandler.ITEM_CURRENCY.get(), new ResourceLocation("currency"), CurrencyItem::getPropertyOverride);
    }
}
