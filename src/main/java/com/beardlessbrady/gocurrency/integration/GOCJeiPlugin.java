package com.beardlessbrady.gocurrency.integration;

import com.beardlessbrady.gocurrency.GOCurrency;
import com.beardlessbrady.gocurrency.init.CommonRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.util.ResourceLocation;

/**
 * Created by BeardlessBrady on 2021-12-06 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
@JeiPlugin
public class GOCJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(GOCurrency.MODID, "jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(CommonRegistry.ITEM_CURRENCY.get());
        IModPlugin.super.registerItemSubtypes(registration);
    }
}
