package gunn.modcurrency;

import gunn.modcurrency.common.core.TabCurrency;
import gunn.modcurrency.common.core.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

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

@Mod(modid = ModCurrency.MODID, name = ModCurrency.MODNAME, version = ModCurrency.VERSION)
public class ModCurrency {
    public static CreativeTabs tabCurrency = new TabCurrency(CreativeTabs.getNextID(),"Currency Mod");
    public static final String MODID = "modcurrency";
    public static final String MODNAME = "Currency Mod";
    public static final String VERSION = "1.10.2-0.0.1";

    @SidedProxy(clientSide = "gunn.modcurrency.common.core.proxy.ClientProxy", serverSide = "gunn.modcurrency.common.core.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static ModCurrency instance;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        logger = event.getModLog();
        proxy.preInit(event);
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        proxy.Init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        proxy.postInit(event);
    }
}
