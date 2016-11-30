package gunn.modcurrency.common.blocks;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency-Mod for Minecraft.
 *
 * The Currency-Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-10-30.
 */
public class ModBlocks {
    public static BlockVendor blockvendor;
    public static BlockTopVendor blocktopvendor;

    public static void preInit(){
        setupBlocks();
    }

    private static void setupBlocks(){
        blockvendor = new BlockVendor();
        blocktopvendor = new BlockTopVendor();
        
    }

    public static void initInitModels(){
        blockvendor.initModel();
    }
    
    public static void addRecipes(){
        blockvendor.recipe();
    }
}
