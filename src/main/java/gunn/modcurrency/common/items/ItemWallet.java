package gunn.modcurrency.common.items;

import gunn.modcurrency.ModCurrency;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * File Created on 2017-01-16
 */
public class ItemWallet extends Item{
    public static final int walletLength = 4;
    public static final int WALLET_COLUMN_COUNT = 9;
    public static final int WALLET_ROW_COUNT = 1; //TODO Modularize
    public static final int WALLET_TOTAL_INV = WALLET_COLUMN_COUNT * WALLET_ROW_COUNT;

    ItemStackHandler walletStackHandler = new ItemStackHandler(WALLET_TOTAL_INV);

    public ItemWallet(){
        setRegistryName("wallet");
        setCreativeTab(ModCurrency.tabCurrency);
        setUnlocalizedName(getRegistryName().toString());
        GameRegistry.register(this);
    }

    @SideOnly(Side.CLIENT)
    public void initModel(){
        for(int i =0; i < walletLength; i++){
            ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName() + "_" + i, "inventory"));
        }
    }

    public void openGui(EntityPlayer player, World world, BlockPos pos) {
       player.openGui(ModCurrency.instance, 32, world, pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        //If new adds the inventory tags
        if(stack.getTagCompound() == null) createTags(stack);

        openGui(playerIn, worldIn, playerIn.getPosition());
        return super.onItemRightClick(stack, worldIn, playerIn, hand);
    }

    public void createTags(ItemStack stack){
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound inventoryNBT = new NBTTagCompound();

        for (int i = 0; i < WALLET_TOTAL_INV; i++) inventoryNBT.setTag(Integer.toString(i), new ItemStack(Blocks.AIR).writeToNBT(new NBTTagCompound()));
        compound.setTag("inventory", inventoryNBT);

        stack.setTagCompound(compound);
    }

    public void setInventoryTag(ItemStack stack, ItemStack[] newInventory){
        if(newInventory.length == WALLET_TOTAL_INV){
            NBTTagCompound compound = stack.getTagCompound();
            NBTTagCompound inventoryNBT;

            inventoryNBT = compound.getCompoundTag("inventory");
        }else System.out.println("ERROR: ModCurrency, new Inventory for wallet has an invalid size");
    }
}
