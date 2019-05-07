package beardlessbrady.modcurrency.block.vending;

import beardlessbrady.modcurrency.ModCurrency;
import beardlessbrady.modcurrency.UtilMethods;
import beardlessbrady.modcurrency.block.TileEconomyBase;
import beardlessbrady.modcurrency.handler.StateHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
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
 * File Created 2019-02-10
 */

public class TileVending extends TileEconomyBase implements ICapabilityProvider, ITickable {
    public final int TE_INPUT_SLOT_COUNT = 1;
    public final int TE_INVENTORY_SLOT_COUNT = 25;
    public final int TE_OUTPUT_SLOT_COUNT = 5;

    private ItemStackHandler inputStackHandler = new ItemStackHandler(TE_INPUT_SLOT_COUNT);
    private ItemStackHandler inventoryStackHandler = new ItemStackHandler(TE_INVENTORY_SLOT_COUNT);
    private ItemStackHandler outputStackHandler = new ItemStackHandler(TE_OUTPUT_SLOT_COUNT);

    private String selectedName;
    private int inventoryLimit, selectedSlot;
    private int[] inventorySize = new int[TE_INVENTORY_SLOT_COUNT];
    private int[] inventoryCost = new int[TE_INVENTORY_SLOT_COUNT];

    public TileVending(){
        for(int i = 0; i < inventorySize.length; i++){
            inventorySize[i] = 0;
        }
        inventoryLimit = 320;
        selectedName = "No Item Selected";
    }

    @Override
    public void update() {

    }

    public void openGui(EntityPlayer player, World world, BlockPos pos){
        //TODO Sync with client playerunsing/owner?
        playerUsing= player.getUniqueID();
        if(world.getBlockState(pos).getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP) {
            player.openGui(ModCurrency.instance, 30, world, pos.getX(), pos.down().getY(), pos.getZ());
        }else {
            player.openGui(ModCurrency.instance, 30, world, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    //<editor-fold desc="NBT Stuff">
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("inventory", inventoryStackHandler.serializeNBT());
        compound.setTag("inputinv", inputStackHandler.serializeNBT());
        compound.setTag("outputinv", outputStackHandler.serializeNBT());
        compound.setInteger("inventoryLimit", inventoryLimit);
        compound.setString("selectedName", selectedName);

        NBTTagCompound inventorySizeNBT = new NBTTagCompound();
        NBTTagCompound inventoryCostNBT = new NBTTagCompound();
        for(int i = 0; i < TE_INVENTORY_SLOT_COUNT; i++){
            inventorySizeNBT.setInteger("inventory" + i, inventorySize[i]);
            inventoryCostNBT.setInteger("cost" + i, inventoryCost[i]);
        }
        compound.setTag("inventorySize", inventorySizeNBT);
        compound.setTag("inventoryCost", inventoryCostNBT);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("inventory")) inventoryStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("inventory"));
        if (compound.hasKey("inputinv")) inputStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("inputinv"));
        if (compound.hasKey("outputinv")) outputStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("outputinv"));
        if (compound.hasKey("inventoryLimit")) inventoryLimit = compound.getInteger("inventoryLimit");
        if (compound.hasKey("selectedName")) selectedName = compound.getString("selectedName");

        if(compound.hasKey("inventorySize")){
            NBTTagCompound inventoryLimitNBT = compound.getCompoundTag("inventorySize");
            NBTTagCompound inventoryCostNBT = compound.getCompoundTag("inventoryCost");
            for(int i = 0; i < TE_INVENTORY_SLOT_COUNT; i++) {
                inventorySize[i] = inventoryLimitNBT.getInteger("inventory" + i);
                inventoryCost[i] = inventoryCostNBT.getInteger("inventoryCost" + i);
            }
        }
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setBoolean("mode", mode);
        compound.setInteger("inventoryLimit", inventoryLimit);
        compound.setString("selectedName", selectedName);

        NBTTagCompound inventorySizeNBT = new NBTTagCompound();
        NBTTagCompound inventoryCostNBT = new NBTTagCompound();
        for(int i = 0; i < TE_INVENTORY_SLOT_COUNT; i++){
            inventorySizeNBT.setInteger("inventory" + i, inventorySize[i]);
            inventoryCostNBT.setInteger("cost" + i, inventoryCost[i]);
        }
        compound.setTag("inventorySize", inventorySizeNBT);
        compound.setTag("inventoryCost", inventoryCostNBT);

        return new SPacketUpdateTileEntity(pos, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound compound = pkt.getNbtCompound();

        inventoryLimit = compound.getInteger("inventoryLimit");
        selectedName = compound.getString("selectedName");

        NBTTagCompound inventoryLimitNBT = compound.getCompoundTag("inventorySize");
        NBTTagCompound inventoryCostNBT = compound.getCompoundTag("inventoryCost");
        for(int i = 0; i < TE_INVENTORY_SLOT_COUNT; i++){
            inventorySize[i] = inventoryLimitNBT.getInteger("inventory" + i);
            inventoryCost[i] = inventoryCostNBT.getInteger("inventory" + i);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Capabilities">
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
            if (facing == null) return (T) new CombinedInvWrapper(inputStackHandler, inventoryStackHandler, outputStackHandler);
        }
        return super.getCapability(capability, facing);
    }
    //</editor-fold>

    public static final int FIELD_INVLIMIT = 1;
    public static final int FIELD_SELECTED = 2;

    @Override
    public int getIntFieldCount(){
        return 2;
    }

    @Override
    public void setIntField(int id, int value){
        switch(id){
            case FIELD_MODE:
                mode = (value == 1);
                break;
            case FIELD_INVLIMIT:
                inventoryLimit = value;
                break;
            case FIELD_SELECTED:
                selectedSlot = value;
                break;
        }
    }

    @Override
    public int getIntField(int id){
        switch(id){
            case FIELD_MODE:
                return (mode)? 1 : 0;
            case FIELD_INVLIMIT:
                return inventoryLimit;
            case FIELD_SELECTED:
                return selectedSlot;
        }
        return 0;
    }

    public boolean isSlotEmpty(int index){
        return inventoryStackHandler.getStackInSlot(index).isEmpty();
    }

    public int getItemSize(int index){
        return inventorySize[index];
    }

    public ItemStack getItemStack(int index){
        return inventoryStackHandler.getStackInSlot(index);
    }

    public ItemStack setItem(ItemStack stack, int index, int addonSize){
        ItemStack copyStack = stack.copy();
        int output;

        stack.setCount(1);
        inventoryStackHandler.setStackInSlot(index, stack);

        if(addonSize == 0) {
            output = copyStack.getCount() - inventoryLimit;
        }else{
            output = (inventorySize[index] + addonSize) - inventoryLimit;
        }

        if(output > 0){
            copyStack.setCount(output);
            inventorySize[index] = inventoryLimit;
        }else{
            copyStack= ItemStack.EMPTY;
            inventorySize[index] = inventoryLimit + output;
        }

        return copyStack;
    }

    public ItemStack setItemAndSize(ItemStack stack, int index, int amount){
        ItemStack userCopy = stack.copy();
        ItemStack machineCopy2 = stack.copy();

        if(!isSlotEmpty(index)) {
            if(UtilMethods.equalStacks(stack, inventoryStackHandler.getStackInSlot(index))) {
                machineCopy2.setCount(amount + getItemSize(index));
            }else
                return stack;
        }else
            machineCopy2.setCount(amount);

        userCopy.shrink(amount);

        int leftovers = setItem(machineCopy2, index, 0).getCount();
        if (leftovers > 0) userCopy.grow(leftovers);

        return userCopy;
    }

    public ItemStack growItemSize(ItemStack stack, int index) {
            if (UtilMethods.equalStacks(stack, inventoryStackHandler.getStackInSlot(index))) {
                return setItemAndSize(stack, index, stack.getCount());
            }
            return stack;
    }

    public ItemStack shrinkItemSize(int num, int index){
        ItemStack outputStack = inventoryStackHandler.getStackInSlot(index).copy();
        int output = inventorySize[index] - num;

        if(output > 0){
            inventorySize[index] = inventorySize[index] - num;
            outputStack.setCount(num);
        }else{
            inventorySize[index] = 0;
            inventoryStackHandler.setStackInSlot(index, ItemStack.EMPTY);
            outputStack.setCount(num + output);
        }

        return outputStack;
    }

    public String getSelectedName(){
        if(selectedName.equals("Air")) return "No Item";
        return selectedName;
    }

    public void setSelectedName(String name){
        selectedName = name;
    }

    public void setItemCost(int cost){
        inventoryCost[selectedSlot] = cost;
    }

    public int getItemCost(){
        return inventoryCost[selectedSlot];
    }
}
