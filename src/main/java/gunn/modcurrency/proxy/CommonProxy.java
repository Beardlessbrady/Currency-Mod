package gunn.modcurrency.proxy;

import gunn.modcurrency.items.ModItems;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency Mod for Minecraft.
 *
 * The Currency Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-10-28.
 */
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent e){
        ModItems.preInit();
    }

    public void Init(FMLInitializationEvent e){
    }

    public void postInit(FMLPostInitializationEvent e){
    }





    




}

