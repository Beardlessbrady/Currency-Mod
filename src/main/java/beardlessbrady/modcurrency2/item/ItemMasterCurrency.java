package beardlessbrady.modcurrency2.item;

import beardlessbrady.modcurrency2.ModCurrency;
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

import java.util.UUID;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2020-04-19
 */
public class ItemMasterCurrency extends Item {
    public ItemMasterCurrency(){
        setUnlocalizedName("mastercurrency");
        setRegistryName("mastercurrency");
        setMaxStackSize(1);
        setCreativeTab(ModCurrency.tabCurrency);
    }

    /** Used to initialize the textures and models for the item **/
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    /** Initializes Master Template*/
    public void initializeNBT(ItemStack stack, World worldIn, String systemName){
        NBTTagCompound compound = new NBTTagCompound();
        compound.setUniqueId("UUID", UUID.randomUUID());
        compound.setString("system", systemName);
        stack.setTagCompound(compound);
    }

    /** Set Currency bill/coin NBT compound */
    public void setCurrency(ItemStack stack, World worldIn, int id, String name, int value){
        if(!worldIn.isRemote && stack.hasTagCompound()){ // Server and has NBT
            NBTTagCompound compound = stack.getTagCompound();

            NBTTagCompound currencyCompound = new NBTTagCompound(); // Creates a compound for the specific currency bill/coin
            currencyCompound.setString("name", name); // Adds name
            currencyCompound.setInteger("value", value); // Adds monetary value
            //TODO texture variables


            compound.setTag(Integer.toString(id), currencyCompound); //Add currency to compound
            stack.setTagCompound(compound); //Re add compound to template
        }
    }

    /** Get Currency bill/coin NBT compound */
    public NBTTagCompound getCurrency(ItemStack stack, World worldIn, int id){
        if(!worldIn.isRemote && stack.hasTagCompound()) { // Server and has NBT
            NBTTagCompound compound = stack.getTagCompound();

            if(compound.hasKey(Integer.toString(id))){ // Looks for currency with id
                return compound.getCompoundTag(Integer.toString(id));
            }
        }
        return null;
    }





}
