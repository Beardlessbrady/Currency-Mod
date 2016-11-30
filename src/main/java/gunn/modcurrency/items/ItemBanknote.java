package gunn.modcurrency.items;

import gunn.modcurrency.ModCurrency;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency-Mod for Minecraft.
 *
 * The Currency-Mod is open source and distributed under a
 * Custom License: https://github.com/BeardlessBrady/Currency-Mod/blob/master/LICENSE
 *
 * File Created on 2016-10-28.
 */
public class ItemBanknote extends Item {
    public static final int noteLength = 6;

    public ItemBanknote(){
        setRegistryName("banknote");
        setCreativeTab(ModCurrency.tabCurrency);
        GameRegistry.register(this);
        setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    public void initModel(){
        for(int i =0; i < noteLength; i++){
            ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName() + "_" + i, "inventory"));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item." + this.getRegistryName().toString() + "_" + stack.getItemDamage();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        for(int i = 0; i < noteLength; i++){
            ItemStack stack = new ItemStack(item,1,i);
            subItems.add(stack);
        }
    }
}
