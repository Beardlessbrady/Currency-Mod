package beardlessbrady.modcurrency.block.tradein;

import beardlessbrady.modcurrency.ConfigCurrency;
import beardlessbrady.modcurrency.ModCurrency;
import beardlessbrady.modcurrency.block.TileEconomyBase;
import beardlessbrady.modcurrency.handler.StateHandler;
import beardlessbrady.modcurrency.item.ModItems;
import beardlessbrady.modcurrency.utilities.UtilMethods;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
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
 * File Created 2019-07-09
 */

public class TileTradein extends TileEconomyBase implements ICapabilityProvider, ITickable {
    public final int TE_INPUT_SLOT_COUNT = 1;
    public final int TE_INVENTORY_SLOT_COUNT = 25;
    public final int TE_OUTPUT_SLOT_COUNT = 1;

    private ItemStackHandler inputStackHandler = new ItemStackHandler(TE_INPUT_SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    };
    private ItemTradeinHandler inventoryStackHandler = new ItemTradeinHandler(TE_INVENTORY_SLOT_COUNT);
    private ItemStackHandler outputStackHandler = new ItemStackHandler(TE_OUTPUT_SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    };

    private String selectedName;
    private boolean creative;
    private int inventoryLimit, selectedSlot;

    private EnumDyeColor color;

    public TileTradein(){
        inventoryLimit = 256;
        selectedName = "No Item Selected";
        color = EnumDyeColor.GRAY;
        creative = false;;
    }

    @Override
    public void update() {
        if (playerUsing != EMPTYID) {
            //If item in INPUT slot is currency && EDIT MODE then calculate its worth and add to money total in machine.
            if (mode) {
                if (!inputStackHandler.getStackInSlot(0).isEmpty()) {
                    if (inputStackHandler.getStackInSlot(0).getItem().equals(ModItems.itemCurrency)) {
                        ItemStack itemStack = inputStackHandler.getStackInSlot(0);

                        float tempAmount = Float.parseFloat(ConfigCurrency.currencyValues[itemStack.getItemDamage()]) * 100;
                        int amount = (int) tempAmount;
                        amount = amount * inputStackHandler.getStackInSlot(0).getCount();

                        if (amount + cashReserve <= 999999999) {
                            inputStackHandler.setStackInSlot(0, ItemStack.EMPTY);
                            cashRegister += amount;
                        } else {
                            //TODO ERROR MESSAGES:  setMessage("CAN'T FIT ANYMORE CURRENCY!", (byte) 40);
                            //  world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 3.0F, false);
                        }
                    }
                }
            } else { //IF SELL MODE
                if (!inputStackHandler.getStackInSlot(0).isEmpty()) {
                    searchLoop:
                    for (int i = 0; i < inventoryStackHandler.getSlots(); i++) {
                        if (!inventoryStackHandler.getStackInSlot(i).isEmpty()) {
                            if (UtilMethods.equalStacks(inputStackHandler.getStackInSlot(0), inventoryStackHandler.getStackInSlot(i)) &&
                                    inputStackHandler.getStackInSlot(0).getItemDamage() == inventoryStackHandler.getStackInSlot(i).getItemDamage()) {

                                int cost = inventoryStackHandler.getItemTradein(i).getCost();
                                int inputAmount = inputStackHandler.getStackInSlot(0).getCount();

                                System.out.println(cost * inputAmount);
                                if(cashRegister >= cost * inputAmount) { //TODO fix where if not enough money sell up to what it can

                                    cashReserve = cashReserve + cost * inputAmount;
                                    cashRegister = cashRegister - cost * inputAmount;

                                    inputStackHandler.getStackInSlot(0).shrink(inputAmount);
                                    inventoryStackHandler.getItemTradein(i).growSize(inputAmount);
                                    //TODO take limit into account
                                }
                                break searchLoop;
                            }
                        }
                    }
                }
            }
        }
    }

    public void openGui(EntityPlayer player, World world, BlockPos pos){
        if(world.getBlockState(pos).getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP) {
            player.openGui(ModCurrency.instance, 31, world, pos.getX(), pos.down().getY(), pos.getZ());
        }else {
            player.openGui(ModCurrency.instance, 31, world, pos.getX(), pos.getY(), pos.getZ());
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
        compound.setInteger("color", color.getDyeDamage());
        compound.setInteger("inventoryLimit", inventoryLimit);
        compound.setInteger("selectedSlot", selectedSlot);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if(compound.hasKey("inventory")) inventoryStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("inventory"));
        if(compound.hasKey("input")) inputStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("input"));
        if(compound.hasKey("output")) outputStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("output"));

        if(compound.hasKey("selectedName")) selectedName = compound.getString("selectedName");
        if(compound.hasKey("color")) color = EnumDyeColor.byDyeDamage(compound.getInteger("color"));
        if(compound.hasKey("inventoryLimit")) inventoryLimit = compound.getInteger("inventoryLimit");
        if(compound.hasKey("selectedSlot")) selectedSlot = compound.getInteger("selectedSlot");
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
        compound.setInteger("color", color.getDyeDamage());
        compound.setInteger("inventoryLimit", inventoryLimit);
        compound.setInteger("selectedSlot", selectedSlot);

        return new SPacketUpdateTileEntity(pos, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound compound = pkt.getNbtCompound();

        if(compound.hasKey("selectedName")) selectedName = compound.getString("selectedName");
        if(compound.hasKey("color")) color = EnumDyeColor.byDyeDamage(compound.getInteger("color"));
        if(compound.hasKey("inventoryLimit")) inventoryLimit = compound.getInteger("inventoryLimit");
        if(compound.hasKey("selectedSlot")) selectedSlot = compound.getInteger("selectedSlot");

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

    public ItemTradein getItemTradein(int i){
        return inventoryStackHandler.getItemTradein(i);
    }

    public void setItemTradein(int i, ItemTradein item){
        inventoryStackHandler.setItemTradein(i, item);
    }

    public void voidItem(int i){
        inventoryStackHandler.voidSlot(i);
    }

    public boolean canMachineAfford(int slot){
        return inventoryStackHandler.getItemTradein(slot).getCost() <= getField(TileEconomyBase.FIELD_CASHREGISTER);
    }

    public void outChange(boolean blockBreak){
        //TODO BETTER WAY TO OUTPUT BESIDES SPITTING ON GROUND
        int bank;
        OUTER_LOOP: for(int i = ConfigCurrency.currencyValues.length-1; i >=0; i--){
            if(mode){
                bank = cashRegister;
            }else{
                bank = cashReserve;
            }

            boolean repeat = false;
            if((bank / (Float.parseFloat(ConfigCurrency.currencyValues[i])) * 100) > 0){ //Divisible by currency value
                int amount = (bank /((int)((Float.parseFloat(ConfigCurrency.currencyValues[i])) * 100)));

                if(amount > 64){
                    amount = 64;
                    repeat = true;
                }


                ItemStack outChange = new ItemStack(ModItems.itemCurrency, amount, i);

                world.spawnEntity(new EntityItem(world, getPos().getX(), getPos().getY(), getPos().getZ(), outChange));
                if (mode) {
                    cashRegister -= ((Float.parseFloat(ConfigCurrency.currencyValues[i]) * 100) * amount);
                } else {
                    cashReserve -= ((Float.parseFloat(ConfigCurrency.currencyValues[i]) * 100) * amount);
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
                item.setCount(inventoryStackHandler.getItemTradein(i).getSize());
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
