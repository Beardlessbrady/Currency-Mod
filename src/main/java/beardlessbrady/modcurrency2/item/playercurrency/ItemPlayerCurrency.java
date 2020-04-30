package beardlessbrady.modcurrency2.item.playercurrency;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2020-03-18
 */
public class ItemPlayerCurrency extends Item {

    public ItemPlayerCurrency(){
        setUnlocalizedName("playercurrency");
        setRegistryName("playercurrency");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);


        if(!itemStack.hasTagCompound()){
            itemStack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
        nbtTagCompound.setInteger("shape", 2);
        nbtTagCompound.setInteger("prime", 3);

        itemStack.setTagCompound(nbtTagCompound);


        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName() + "/playercurrency", "inventory"));
    }

    // Register Player Currency Details
    public void registerCurrencyVariants(){
        //Register Currency Shapes
        for(int i = 0; i < EnumCurrencyShape.values().length; i++){
            ModelBakery.registerItemVariants(this, new ModelResourceLocation(this.getRegistryName() + "/shape/" + EnumCurrencyShape.values()[i].getName(), "inventory"));
        }

        //Register Currency Prime Details
        for(int i = 0; i < EnumCurrencyPrime.values().length; i++){
            ModelBakery.registerItemVariants(this, new ModelResourceLocation(this.getRegistryName() + "/primedetail/" + EnumCurrencyPrime.values()[i].getName(), "inventory"));
        }
    }
}
