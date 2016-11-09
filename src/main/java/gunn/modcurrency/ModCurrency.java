package gunn.modcurrency;

import gunn.modcurrency.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency-Mod for Minecraft.
 *
 * The Currency-Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-10-28.
 */

@Mod(modid = ModCurrency.MODID, name = ModCurrency.MODNAME, version = ModCurrency.VERSION)
public class ModCurrency {
    public static CreativeTabs tabCurrency = new TabCurrency(CreativeTabs.getNextID(),"Currency Mod");
    public static final String MODID = "modcurrency";
    public static final String MODNAME = "Currency Mod";
    public static final String VERSION = "1.10.2-0.0.1";

    @SidedProxy(clientSide = "gunn.modcurrency.proxy.ClientProxy", serverSide = "gunn.modcurrency.proxy.CommonProxy")
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
