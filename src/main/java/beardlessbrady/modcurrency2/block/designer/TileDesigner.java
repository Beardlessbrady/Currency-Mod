package beardlessbrady.modcurrency2.block.designer;

import beardlessbrady.modcurrency2.ModCurrency;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2020-03-20
 */
public class TileDesigner extends TileEntity implements ICapabilityProvider {
    private boolean templateIn;

    // customCurrency Itemhandler
    private ItemStackHandler customHandler = new ItemStackHandler(21) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    };

    // inventory Itemhandler
    private ItemStackHandler inventoryHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    };

    public TileDesigner(){
        templateIn = false;
    }

    /** To open the GUI **/
    public void openGui(EntityPlayer player, World world, BlockPos pos){
        player.openGui(ModCurrency.instance, 32, world, pos.getX(), pos.getY(), pos.getZ());
    }

    /** Capability Methods **/
    //<editor-fold desc="Capabilities">
    //TODO For when you want hopper interaction
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return facing == null;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            if (facing == null) return (T) new CombinedInvWrapper(inventoryHandler, customHandler);
        }
        return super.getCapability(capability, facing);
    }
    //</editor-fold>

    //Field Id's
    public static final byte FIELD_TEMPLATE = 0;

    public int getFieldCount(){
        return 1;
    }

    public void setField(int id, int value){
        switch(id){
            case FIELD_TEMPLATE:
                templateIn = (value == 1);
                break;
        }
    }

    public int getField(int id){
        switch(id){
            case FIELD_TEMPLATE:
                return (templateIn)? 1 : 0;
        }
        return 0;
    }
}
