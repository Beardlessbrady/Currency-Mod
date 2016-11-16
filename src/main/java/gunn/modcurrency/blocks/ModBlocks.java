package gunn.modcurrency.blocks;

import net.minecraft.init.Items;
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

    public static void preInit(){
        setupBlocks();
    }

    private static void setupBlocks(){
        blockvendor = new BlockVendor();
    }

    public static void initInitModels(){
        blockvendor.initModel();
    }
    
    public static void addRecipes(){
        
        
        
        for(int i = 0; i < 16; i++) {
            if(i != 15) {
                ItemStack stack = new ItemStack(blockvendor);
                ItemStack color = new ItemStack(Items.DYE);
                ItemStack basic = new ItemStack(blockvendor);
                stack.setItemDamage(i);
                color.setItemDamage(15 - i);
                basic.setItemDamage(15);

                GameRegistry.addShapelessRecipe(stack, color, basic);
                GameRegistry.addShapelessRecipe(basic, stack);
            }
        }
    }
}
