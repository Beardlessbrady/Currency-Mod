package com.beardlessbrady.gocurrency.init;

import com.beardlessbrady.gocurrency.GOCurrency;
import com.beardlessbrady.gocurrency.items.CurrencyItem;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Created by BeardlessBrady on 2021-02-27 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class ClientRegistry {
    private final IEventBus eventBus;

    public ClientRegistry(IEventBus eventBus) {
        this.eventBus = eventBus;
    }

    public static void doClientStuff() {
        ItemModelsProperties.registerProperty(CommonRegistry.ITEM_CURRENCY.get(), new ResourceLocation("currency"), CurrencyItem::getPropertyOverride);
    }

    public void registerClientOnlyEvents() {
        eventBus.register(ClientEventSubscriber.class);
    }
}
