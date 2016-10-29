package gunn.modcurrency.items;

import gunn.modcurrency.ModCurrency;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency Mod for Minecraft.
 *
 * The Currency Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-10-28.
 */
public class BaseItem extends Item{
    private String name;

    public BaseItem(String name){
        setRegistryName(name);
        setCreativeTab(ModCurrency.tabCurrency);
        setUnlocalizedName(this.getRegistryName().toString());
        GameRegistry.register(this);
    }


    public void initModel(){
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
