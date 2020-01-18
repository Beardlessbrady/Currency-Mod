package beardlessbrady.modcurrency.item;

import beardlessbrady.modcurrency.utilities.UtilMethods;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Stack;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2020-01-17
 */
public class ItemMoneyBag extends Item {

    public ItemMoneyBag(){
        setUnlocalizedName("moneybag");
        setRegistryName("moneybag");
        maxStackSize = 1;
    }

    /** Method activated on item right click **/
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
            if(openBag(playerIn, playerIn.getHeldItemMainhand())){
                playerIn.setHeldItem(handIn, ItemStack.EMPTY);
            }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    /** Used to initialize the textures and models for the item **/
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    /** Add information to item tooltip **/
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if(stack.hasTagCompound()){
            NBTTagCompound compound = stack.getTagCompound();
            if(compound.hasKey("cash")){
                tooltip.add("$" + UtilMethods.translateMoney(compound.getInteger("cash")));
            }
        }
    }

    /** Given an amount of currency to save to NBT **/
    public static void CurrencyToNBT(ItemStack itemStack, int currencyTotal){
        NBTTagCompound compound;

        // If tagCompound exists use that otherwise make one
        if(itemStack.hasTagCompound()) {
            compound = itemStack.getTagCompound();
        } else {
            compound = new NBTTagCompound();
        }

        // If the 'cash' tag does not exist add cash to tag
        if(!compound.hasKey("cash")) {
            compound.setInteger("cash", currencyTotal);
            itemStack.setTagCompound(compound);
        }
    }

    /** Attempts to open bag by placing currency into player inventory **/
    private boolean openBag(EntityPlayer playerIn, ItemStack itemStack) {
        // If itemStack has tagCompound and has the tag 'cash'
        if (itemStack.hasTagCompound()) {
            NBTTagCompound compound = itemStack.getTagCompound();
            if (compound.hasKey("cash")) {
                Stack<ItemStack> billStack = UtilMethods.stackBills(compound.getInteger("cash")); //Uses #UtilMethods.StackBills to create a STACK of itemstacks with the currency amount from NBT
                int billStackSize = billStack.size(); //Save bill Stack size for later use

                //Loops through player inventory to count how many free spaces there are
                int emptySlotCount = 0;
                for (int i = 0; i <= 35; i++) { //35 is the Player Hotbar and inventory, we dont want items in armor slots
                    if (playerIn.inventory.getStackInSlot(i).isEmpty())
                        emptySlotCount++;
                }

                // If enough room for currency then loop through billstack.size and insert each itemstack
                if (emptySlotCount >= billStackSize) {
                    while(billStack.size() != 0) {
                        ItemStack item = billStack.pop();
                        playerIn.inventory.addItemStackToInventory(item);
                    }
                    return true;
                } else { //Not enough room in Inventory
                    playerIn.sendMessage(new TextComponentString("Empty " + Integer.toString(billStackSize - emptySlotCount) + " or more slots to open this money bag."));
                    return false;
                }
            }
        }
        return false;
    }
}
