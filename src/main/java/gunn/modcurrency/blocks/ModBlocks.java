package gunn.modcurrency.blocks;

import gunn.modcurrency.items.ModItems;
import net.minecraft.block.Block;
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
        ItemStack stack = new ItemStack(Item.getItemFromBlock(ModBlocks.blockvendor));
        stack.setItemDamage(15);

        GameRegistry.addRecipe(stack,
                "ABA",
                         "ACA",
                         "ADA",
                'A', Items.IRON_INGOT,
                'B', Items.REPEATER,
                'C', Item.getItemFromBlock(Blocks.CHEST),
                'D', Items.IRON_DOOR);
    }
}
