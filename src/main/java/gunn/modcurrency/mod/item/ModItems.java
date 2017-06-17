package gunn.modcurrency.mod.item;

import gunn.modcurrency.mod.ModConfig;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-10-28.
 */
public class ModItems {
    public static ItemBanknote itemBanknote;
    public static ItemWallet itemWallet;
    public static ItemGuideBook itemGuide;


    public static void preInit(){
        setupItems();
    }

    private static void setupItems(){
        itemBanknote = new ItemBanknote();
      //  itemGuide = new ItemGuideBook();
        if(ModConfig.enableWallet) itemWallet = new ItemWallet();
    }

    @SideOnly(Side.CLIENT)
    public static void ItemModels(){
        itemBanknote.initModel();
       // itemGuide.initModel();;
        if(ModConfig.enableWallet) itemWallet.initModel();
    }
}
