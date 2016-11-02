package gunn.modcurrency.blocks;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency Mod for Minecraft.
 *
 * The Currency Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-10-30.
 */
public class ModBlocks {
    public static BlockVendor blockvendor;

    public static void preInit(){
        setupBlocks();
    }

    public static void setupBlocks(){
        blockvendor = new BlockVendor();

    }

    public static void initInitModels(){
        blockvendor.initModel();
    }












}
