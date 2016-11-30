package gunn.modcurrency.common.core.proxy;

import gunn.modcurrency.ModCurrency;
import gunn.modcurrency.common.blocks.ModBlocks;
import gunn.modcurrency.common.core.handler.GuiHandler;
import gunn.modcurrency.common.core.handler.PacketHandler;
import gunn.modcurrency.common.items.ModItems;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency-Mod for Minecraft.
 *
 * The Currency-Mod is open source and distributed under a
 * Custom License: https://github.com/BeardlessBrady/Currency-Mod/blob/master/LICENSE
 *
 * File Created on 2016-10-28.
 */
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent e){
        ModItems.preInit();
        ModBlocks.preInit();

        NetworkRegistry.INSTANCE.registerGuiHandler(ModCurrency.instance, new GuiHandler());
        PacketHandler.registerMessages("modcurrency");
    }

    public void Init(FMLInitializationEvent e){
        ModBlocks.addRecipes();
    }

    public void postInit(FMLPostInitializationEvent e){
    }
}

