package beardlessbrady.modcurrency.block.vending;

import beardlessbrady.modcurrency.ConfigCurrency;
import beardlessbrady.modcurrency.ModCurrency;
import beardlessbrady.modcurrency.UtilMethods;
import beardlessbrady.modcurrency.block.TileEconomyBase;
import beardlessbrady.modcurrency.handler.StateHandler;
import beardlessbrady.modcurrency.item.ModItems;
import net.minecraft.entity.item.EntityItem;
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

    private ItemStackHandler inputStackHandler = new ItemStackHandler(TE_INPUT_SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    };
    private ItemStackHandler inventoryStackHandler = new ItemStackHandler(TE_INVENTORY_SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    };
    private ItemStackHandler outputStackHandler = new ItemStackHandler(TE_OUTPUT_SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    };

    private String selectedName;
    private int inventoryLimit, selectedSlot;
    private int[] inventorySize = new int[TE_INVENTORY_SLOT_COUNT];
    private int[] inventoryCost = new int[TE_INVENTORY_SLOT_COUNT];

    public TileVending(){
        for(int i = 0; i < inventorySize.length; i++){
            inventorySize[i] = 0;
        }
        inventoryLimit = 256;
        selectedName = "No Item Selected";
    }

    @Override
    public void update() {
        if(playerUsing != null) {
            if (!inputStackHandler.getStackInSlot(0).isEmpty()) {
                if (inputStackHandler.getStackInSlot(0).getItem().equals(ModItems.itemCurrency)) {
                    ItemStack itemStack = inputStackHandler.getStackInSlot(0);

                    float tempAmount = Float.valueOf(ConfigCurrency.currencyValues[itemStack.getItemDamage()]) * 100;
                    int amount = (int) tempAmount;
                    amount = amount * inputStackHandler.getStackInSlot(0).getCount();

                    if(amount + cashReserve <= 999999999) {
                        inputStackHandler.setStackInSlot(0, ItemStack.EMPTY);
                        cashReserve += amount;

                        //TODO inform of machine cap
                    }
                }
            }
        }
    }

    public void openGui(EntityPlayer player, World world, BlockPos pos){
        mode = false;
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
        compound.setTag("input", inputStackHandler.serializeNBT());
        compound.setTag("output", outputStackHandler.serializeNBT());

        compound.setString("selectedName", selectedName);
        compound.setInteger("inventoryLimit", inventoryLimit);
        compound.setInteger("selectedSlot", selectedSlot);

        NBTTagCompound inventorySizeNBT = new NBTTagCompound();
        NBTTagCompound inventoryCostNBT = new NBTTagCompound();
        for(int i = 0; i < TE_INVENTORY_SLOT_COUNT; i++){
            inventorySizeNBT.setInteger("size" + i, inventorySize[i]);
            inventoryCostNBT.setInteger("cost" + i, inventoryCost[i]);
        }
        compound.setTag("inventorySizeNBT", inventorySizeNBT);
        compound.setTag("inventoryCostNBT", inventoryCostNBT);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if(compound.hasKey("inventory")) inventoryStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("inventory"));
        if(compound.hasKey("input")) inputStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("input"));
        if(compound.hasKey("output")) outputStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("output"));

        if(compound.hasKey("selectedName")) selectedName = compound.getString("selectedName");
        if(compound.hasKey("inventoryLimit")) inventoryLimit = compound.getInteger("inventoryLimit");
        if(compound.hasKey("selectedSlot")) selectedSlot = compound.getInteger("selectedSlot");

        if(compound.hasKey("inventorySizeNBT")){
            NBTTagCompound inventorySizeNBT = compound.getCompoundTag("inventorySizeNBT");
            for(int i = 0; i < TE_INVENTORY_SLOT_COUNT; i++) inventorySize[i] = inventorySizeNBT.getInteger("size" + i);
        }

        if(compound.hasKey("inventoryCostNBT")){
            NBTTagCompound inventoryCostNBT = compound.getCompoundTag("inventoryCostNBT");
            for(int i = 0; i < TE_INVENTORY_SLOT_COUNT; i++) inventoryCost[i] = inventoryCostNBT.getInteger("cost" + i);
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        super.getUpdatePacket();
        NBTTagCompound compound = new NBTTagCompound();

        compound.setString("selectedName", selectedName);
        compound.setInteger("inventoryLimit", inventoryLimit);
        compound.setInteger("selectedSlot", selectedSlot);

        NBTTagCompound inventorySizeNBT = new NBTTagCompound();
        NBTTagCompound inventoryCostNBT = new NBTTagCompound();
        for(int i = 0; i < TE_INVENTORY_SLOT_COUNT; i++){
            inventorySizeNBT.setInteger("size" + i, inventorySize[i]);
            inventoryCostNBT.setInteger("cost" + i, inventoryCost[i]);
        }
        compound.setTag("inventorySizeNBT", inventorySizeNBT);
        compound.setTag("inventoryCostNBT", inventoryCostNBT);


        return new SPacketUpdateTileEntity(pos, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound compound = pkt.getNbtCompound();

        if(compound.hasKey("selectedName")) selectedName = compound.getString("selectedName");
        if(compound.hasKey("inventoryLimit")) inventoryLimit = compound.getInteger("inventoryLimit");
        if(compound.hasKey("selectedSlot")) selectedSlot = compound.getInteger("selectedSlot");

        if(compound.hasKey("inventorySizeNBT")){
            NBTTagCompound inventorySizeNBT = compound.getCompoundTag("inventorySizeNBT");
            for(int i = 0; i < TE_INVENTORY_SLOT_COUNT; i++) inventorySize[i] = inventorySizeNBT.getInteger("size" + i);
        }

        if(compound.hasKey("inventoryCostNBT")){
            NBTTagCompound inventoryCostNBT = compound.getCompoundTag("inventoryCostNBT");
            for(int i = 0; i < TE_INVENTORY_SLOT_COUNT; i++) inventoryCost[i] = inventoryCostNBT.getInteger("cost" + i);
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

    public static final int FIELD_INVLIMIT = 3;
    public static final int FIELD_SELECTED = 4;

    @Override
    public int getFieldCount(){
        return super.getFieldCount() + 2;
    }

    @Override
    public void setField(int id, int value){
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
            default:
                super.setField(id, value);
        }
    }

    @Override
    public int getField(int id){
        switch(id){
            case FIELD_MODE:
                return (mode)? 1 : 0;
            case FIELD_CASHRESERVE:
                return cashReserve;
            case FIELD_CASHREGISTER:
                return cashRegister;
            case FIELD_INVLIMIT:
                return inventoryLimit;
            case FIELD_SELECTED:
                return selectedSlot;
            default:
                return super.getField(id);
        }
    }

    public boolean isSlotEmpty(int index){
        return inventoryStackHandler.getStackInSlot(index).isEmpty();
    }

    public int getItemSize(int index){
        return inventorySize[index];
    }

    public ItemStack getInvItemStack(int index){
        return inventoryStackHandler.getStackInSlot(index);
    }

    public ItemStack setInvItem(ItemStack stack, int index, int addonSize){
        if(stack == ItemStack.EMPTY) {
            inventoryStackHandler.setStackInSlot(index, ItemStack.EMPTY);
            return ItemStack.EMPTY;
        }

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
        }else {
            copyStack= ItemStack.EMPTY;
            inventorySize[index] = inventoryLimit + output;
        }

        return copyStack;
    }

    public ItemStack setInvItemAndSize(ItemStack stack, int index, int amount){
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

        int leftovers = setInvItem(machineCopy2, index, 0).getCount();
        if (leftovers > 0) userCopy.grow(leftovers);

        return userCopy;
    }

    public ItemStack growInvItemSize(ItemStack stack, int index) {
            if (UtilMethods.equalStacks(stack, inventoryStackHandler.getStackInSlot(index))) {
                return setInvItemAndSize(stack, index, stack.getCount());
            }
            return stack;
    }

    public ItemStack shrinkInvItemSize(int num, int index){
        ItemStack outputStack = inventoryStackHandler.getStackInSlot(index).copy();
        int output = inventorySize[index] - num;

        if(output > 0){
            inventorySize[index] = inventorySize[index] - num;
            outputStack.setCount(num);
        }else {
            inventorySize[index] = 0;
            outputStack.setCount(num + output);
        }

        return outputStack;
    }

    public ItemStack growOutItemSize(ItemStack stack, int index){
        if (UtilMethods.equalStacks(stack, outputStackHandler.getStackInSlot(index))) {
            if(outputStackHandler.getStackInSlot(index).getCount() + stack.getCount() <= outputStackHandler.getStackInSlot(index).getMaxStackSize()){
                outputStackHandler.getStackInSlot(index).grow(stack.getCount());
                return ItemStack.EMPTY;
            }
        }else if(outputStackHandler.getStackInSlot(index).isEmpty()){
            outputStackHandler.setStackInSlot(index, stack);
            return ItemStack.EMPTY;
        }
        return stack;
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

    public int getItemCost(int i){
        return inventoryCost[i];
    }

    public boolean canAfford(int slot, int amount){
        if(getItemCost(slot) * amount <= getField(TileEconomyBase.FIELD_CASHRESERVE)) return true;
        return false;
    }

    public int outputSlotCheck(ItemStack itemStack){
        //Checks if any stacks in output are equal to the itemStack and can handle it being added in
        for(int i = 0; i < TE_OUTPUT_SLOT_COUNT; i++){
            if(UtilMethods.equalStacks(itemStack, outputStackHandler.getStackInSlot(i))){
                if(outputStackHandler.getStackInSlot(i).getCount() + 1 <= outputStackHandler.getStackInSlot(i).getMaxStackSize()){
                    return i;
                }
            }
        }

        //Checks for empty slots
        for(int i = 0; i < TE_OUTPUT_SLOT_COUNT; i++) {
            if(outputStackHandler.getStackInSlot(i).isEmpty()){
                return i;
            }
        }
        return -1;
    }

    public void outChange(boolean blockBreak){
        int bank;
        OUTER_LOOP: for(int i = ConfigCurrency.currencyValues.length-1; i >=0; i--){
            if(mode == true){
                bank = cashRegister;
            }else{
                bank = cashReserve;
            }

            boolean repeat = false;
            if((bank / (Float.valueOf(ConfigCurrency.currencyValues[i])) * 100) > 0){ //Divisible by currency value
                int amount = (bank /((int)((Float.valueOf(ConfigCurrency.currencyValues[i])) * 100)));

                if(amount > 64){
                    amount = 64;
                    repeat = true;
                }


                ItemStack outChange = new ItemStack(ModItems.itemCurrency, amount, i);

                if(blockBreak){ //If Block Breaking spawn item
                    world.spawnEntity(new EntityItem(world, getPos().getX(), getPos().getY(), getPos().getZ(), outChange));
                    if (mode == true) {
                        cashRegister -= ((Float.valueOf(ConfigCurrency.currencyValues[i]) * 100) * amount);
                    } else {
                        cashReserve -= ((Float.valueOf(ConfigCurrency.currencyValues[i]) * 100) * amount);
                    }
                }else {
                    int outputSlot = outputSlotCheck(outChange);
                    if (outputSlot != -1) {
                        if (growOutItemSize(outChange, outputSlot).equals(ItemStack.EMPTY)) {
                            if (mode == true) {
                                cashRegister -= ((Float.valueOf(ConfigCurrency.currencyValues[i]) * 100) * amount);
                            } else {
                                cashReserve -= ((Float.valueOf(ConfigCurrency.currencyValues[i]) * 100) * amount);
                            }
                        }
                    } else {
                        break OUTER_LOOP;
                    }
                }
            }

            if (repeat) i++;
            if(bank == 0) break OUTER_LOOP;
        }
    }

    public void dropInventory(){
        for (int i = 0; i < inventoryStackHandler.getSlots(); i++) {
            ItemStack item = inventoryStackHandler.getStackInSlot(i);
            if (!item.isEmpty()) {
                item.setCount(getItemSize(i));
                world.spawnEntity(new EntityItem(world, getPos().getX(), getPos().getY(), getPos().getZ(), item));
                inventoryStackHandler.setStackInSlot(i, ItemStack.EMPTY);   //Just in case
            }
        }
        for (int i = 0; i < outputStackHandler.getSlots(); i++){
            ItemStack item = outputStackHandler.getStackInSlot(i);
            if (item != ItemStack.EMPTY) {
                world.spawnEntity(new EntityItem(world, getPos().getX(), getPos().getY(), getPos().getZ(), item));
                outputStackHandler.setStackInSlot(i, ItemStack.EMPTY);   //Just in case
            }
        }
        for (int i = 0; i < inputStackHandler.getSlots(); i++){
            ItemStack item = inputStackHandler.getStackInSlot(i);
            if (item != ItemStack.EMPTY) {
                world.spawnEntity(new EntityItem(world, getPos().getX(), getPos().getY(), getPos().getZ(), item));
                inputStackHandler.setStackInSlot(i, ItemStack.EMPTY);   //Just in case
            }
        }

    }
}
