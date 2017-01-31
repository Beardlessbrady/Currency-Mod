package gunn.modcurrency.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016 Brady Gunn
 *
 * File Created on 2016-12-17
 */
public class ModTile extends TileEntity implements ICapabilityProvider {

    public void outChange(){}

    public void dropItems(){}

    public void update(World world, BlockPos pos){
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.scheduleBlockUpdate(pos,this.blockType,0,0);
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        markDirty();
        System.out.println("POOP I UPDATED");
    }

    public boolean canInteractWith(EntityPlayer player){return true;}

    public int getFieldCount() {return 0;}

    public void setField(int id, int value) {

    }

    public int getField(int id) {
        return -1;
    }

    public String getSelectedName() { return null;}

    public void setSelectedName (String name){}

    public int[] getAllItemCosts(){ return null; }

    public void setAllItemCosts(int[] copy){}

    public int getItemCost(int index) { return -1; }

    public void setItemCost(int amount) {}

    public ItemStack getStack(int index){
        return null;
    }

    public void setOwner(String owner) {}

    public String getOwner() {
        return null;
    }

    public ItemStackHandler getInputHandler(){ return null; }

    public ItemStackHandler getBufferHandler(){ return null; }

    public ItemStackHandler getVendHandler(){ return null; }

    public void setStackHandlers(ItemStackHandler inputCopy, ItemStackHandler buffCopy, ItemStackHandler vendCopy){}

    public EntityPlayer getPlayerUsing(){
        return null;
    }

    public void voidPlayerUsing(){
    }

}
