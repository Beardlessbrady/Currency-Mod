package gunn.modcurrency.common.core.proxy;

import gunn.modcurrency.ModConfig;
import gunn.modcurrency.ModCurrency;
import gunn.modcurrency.common.blocks.ModBlocks;
import gunn.modcurrency.common.core.handler.EventHandlerCommon;
import gunn.modcurrency.common.core.handler.GuiHandler;
import gunn.modcurrency.common.core.handler.PacketHandler;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Configuration;
import gunn.modcurrency.common.items.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.io.File;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * File Created on 2016-10-28.
 */
public class CommonProxy {
    public static Configuration config;

    public void preInit(FMLPreInitializationEvent e){
        File directory = e.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "GoodOlCurrency.cfg"));
        ModConfig.readConfig();

        ModItems.preInit();
        ModBlocks.preInit();

        NetworkRegistry.INSTANCE.registerGuiHandler(ModCurrency.instance, new GuiHandler());
        PacketHandler.registerMessages("modcurrency");
    }

    public void Init(FMLInitializationEvent e){
        ModBlocks.addRecipes();
        ModItems.addRecipes();
        MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
    }


    public void postInit(FMLPostInitializationEvent e){
    }
}

