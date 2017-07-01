package gunn.modcurrency.mod.item;

import gunn.modcurrency.mod.ModConfig;
import gunn.modcurrency.mod.ModCurrency;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-01-16
 */
public class ItemWallet extends Item{
    public static final int walletLength = 6;
    public static final int WALLET_COLUMN_COUNT = 9;
    public static final int WALLET_ROW_COUNT = ModConfig.walletSize;
    public static final int WALLET_TOTAL_COUNT = WALLET_COLUMN_COUNT * WALLET_ROW_COUNT;

    public ItemWallet(){
        setRegistryName("wallet");
        setCreativeTab(ModCurrency.tabCurrency);
        setUnlocalizedName(getRegistryName().toString());
        setHasSubtypes(true);
        setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    public void initModel(){
        switch (ModConfig.textureType){
            default:
            case 0:
                for (int i = 0; i < walletLength; i++) {
                    ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName() + "_" + i, "inventory"));
                }
                break;
            case 1:
                for (int i = 0; i < walletLength; i++) {
                    if(i == 2) {
                        ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName() + "16" + "_" + 2, "inventory"));
                    }else if(i == 3) {
                        ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName() + "16" + "_" + 3, "inventory"));
                    }else {
                        ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName() + "16" + "_" + i, "inventory"));
                    }
                }
                break;
        }
    }

    public void openGui(EntityPlayer player, World world, BlockPos pos) {
       player.openGui(ModCurrency.instance, 32, world, pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World itemStackIn, EntityPlayer worldIn, EnumHand playerIn) {
        openGui(worldIn, itemStackIn, worldIn.getPosition());
        return super.onItemRightClick(itemStackIn, worldIn, playerIn);
    }

}
