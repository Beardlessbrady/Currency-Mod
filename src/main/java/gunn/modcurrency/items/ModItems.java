package gunn.modcurrency.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency Mod for Minecraft.
 *
 * The Currency Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-10-28.
 */
public class ModItems {
    public static ItemBanknote itembanknote;


    public static void preInit(){
        setupItems();
    }

    private static void setupItems(){
        itembanknote = new ItemBanknote();

    }

    @SideOnly(Side.CLIENT)
    public static void initItemModels(){
        itembanknote.initModel();
    }















}
