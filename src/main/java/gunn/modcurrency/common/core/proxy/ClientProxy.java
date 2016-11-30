package gunn.modcurrency.common.core.proxy;

import gunn.modcurrency.ModCurrency;
import gunn.modcurrency.common.blocks.ModBlocks;
import gunn.modcurrency.common.items.ModItems;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency-Mod for Minecraft.
 *
 * The Currency-Mod is open source and distributed under a
 * Custom License: https://github.com/BeardlessBrady/Currency-Mod/blob/master/LICENSE
 *
 * File Created on 2016-10-28.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        ModItems.initItemModels();
        ModBlocks.initInitModels();
        
        OBJLoader.INSTANCE.addDomain(ModCurrency.MODID);
    }

    @Override
    public void Init(FMLInitializationEvent e){
        super.Init(e);
        //ClientRegistry.bindTileEntitySpecialRenderer(TileVendor.class, new RenderTileVendor());
    }
}