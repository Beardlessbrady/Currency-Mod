package beardlessbrady.modcurrency.block.vending;

import beardlessbrady.modcurrency.ConfigCurrency;
import beardlessbrady.modcurrency.ModCurrency;
import beardlessbrady.modcurrency.block.TileEconomyBase;
import beardlessbrady.modcurrency.handler.StateHandler;
import beardlessbrady.modcurrency.item.ModItems;
import beardlessbrady.modcurrency.utilities.UtilMethods;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import javax.annotation.Nullable;

import static beardlessbrady.modcurrency.block.vending.TileVending.*;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-10
 */

public class TileVending extends TileEconomyBase implements ICapabilityProvider, ITickable {
    //Variables that determine the slot group sizes for the machine.
    public final short TE_INPUT_SLOT_COUNT = 1;
    public final short TE_INVENTORY_SLOT_COUNT = 25;
    public final short TE_OUTPUT_SLOT_COUNT = 5;

    //Machine inventorys
    private ItemStackHandler inputStackHandler = new ItemStackHandler(TE_INPUT_SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    };
    private ItemVendorHandler inventoryStackHandler = new ItemVendorHandler(TE_INVENTORY_SLOT_COUNT);
    private ItemStackHandler outputStackHandler = new ItemStackHandler(TE_OUTPUT_SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    };

    //Used for Warning messages
    private String message= "";
    private byte messageTime = 0;

    //Used for Creative auto refill of item slots
    private long serverTime;

    private String selectedName;
    private boolean creative, finite;
    private int selectedSlot;

    //Color of machine
    private EnumDyeColor color;

    public TileVending(){
        serverTime = 0;
        selectedName = "No Item Selected";
        color = EnumDyeColor.GRAY;
        creative = false;
        finite = true;
    }

    @Override
    public void update() {
        if(playerUsing != EMPTYID) {
            //If item in INPUT slot is currency then calculate its worth and add to money total in machine.
            if (!inputStackHandler.getStackInSlot(0).isEmpty()) {
                if (inputStackHandler.getStackInSlot(0).getItem().equals(ModItems.itemCurrency)) {
                    ItemStack itemStack = inputStackHandler.getStackInSlot(0);

                    float tempAmount = Float.valueOf(ConfigCurrency.currencyValues[itemStack.getItemDamage()]) * 100;
                    int amount = (int) tempAmount;
                    amount = amount * inputStackHandler.getStackInSlot(0).getCount();

                    if(amount + cashReserve <= 999999999) {
                        inputStackHandler.setStackInSlot(0, ItemStack.EMPTY);
                        cashReserve += amount;
                    }else{
                        setMessage("CAN'T FIT ANYMORE CURRENCY!", (byte) 40);
                        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 3.0F, false);
                    }
                }
            }

            //Timer for warning messages
            if(messageTime > 0) {
                messageTime--;
            }else{
                message = "";
            }
        }
    }

    public void openGui(EntityPlayer player, World world, BlockPos pos){
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
        compound.setLong("serverTime", serverTime);

        compound.setTag("inventory", inventoryStackHandler.serializeNBT());
        compound.setTag("input", inputStackHandler.serializeNBT());
        compound.setTag("output", outputStackHandler.serializeNBT());
        compound.setBoolean("creative", creative);
        compound.setBoolean("finite", finite);

        compound.setString("selectedName", selectedName);
        compound.setInteger("color", color.getDyeDamage());
        compound.setInteger("selectedSlot", selectedSlot);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if(compound.hasKey("inventory")) inventoryStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("inventory"));
        if(compound.hasKey("input")) inputStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("input"));
        if(compound.hasKey("output")) outputStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("output"));

        if(compound.hasKey("serverTime")) serverTime = compound.getLong("serverTime");
        if(compound.hasKey("creative")) creative = compound.getBoolean("creative");
        if(compound.hasKey("finite")) finite = compound.getBoolean("finite");

        if(compound.hasKey("selectedName")) selectedName = compound.getString("selectedName");
        if(compound.hasKey("color")) color = EnumDyeColor.byDyeDamage(compound.getInteger("color"));
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

        compound.setLong("serverTime", serverTime);
        compound.setTag("inventory", inventoryStackHandler.serializeNBT());
        compound.setTag("input", inputStackHandler.serializeNBT());
        compound.setTag("output", outputStackHandler.serializeNBT());

        compound.setBoolean("creative", creative);
        compound.setBoolean("finite", finite);

        compound.setString("selectedName", selectedName);
        compound.setInteger("color", color.getDyeDamage());
        compound.setInteger("selectedSlot", selectedSlot);

        return new SPacketUpdateTileEntity(pos, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound compound = pkt.getNbtCompound();

       if(compound.hasKey("inventory")) inventoryStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("inventory"));
       if(compound.hasKey("input")) inputStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("input"));
       if(compound.hasKey("output")) outputStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("output"));

        if(compound.hasKey("serverTime")) serverTime = compound.getLong("serverTime");
        if(compound.hasKey("creative")) creative = compound.getBoolean("creative");
        if(compound.hasKey("finite")) finite = compound.getBoolean("finite");

        if(compound.hasKey("selectedName")) selectedName = compound.getString("selectedName");
        if(compound.hasKey("color")) color = EnumDyeColor.byDyeDamage(compound.getInteger("color"));
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

    //<editor-fold desc="fields">
    public static final byte FIELD_CREATIVE = 4;
    public static final byte FIELD_FINITE = 5;
    public static final byte FIELD_SELECTED = 6;

    @Override
    public int getFieldCount(){
        return super.getFieldCount() + 3;
    }

    @Override
    public void setField(int id, int value){
        switch(id){
            case FIELD_MODE:
                mode = (value == 1);
                break;
            case FIELD_CREATIVE:
                creative = (value == 1);
                break;
            case FIELD_FINITE:
                finite = (value == 1);
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
            case FIELD_CREATIVE:
                return (creative)? 1 : 0;
            case FIELD_FINITE:
                return (finite)? 1 : 0;
            case FIELD_SELECTED:
                return selectedSlot;
            default:
                return super.getField(id);
        }
    }
    //</editor-fold>

    public String getSelectedName(){
        if(selectedName.equals("Air")) return "No Item";
        return selectedName;
    }

    public void setSelectedName(String name){
        selectedName = name;
    }

    public boolean canAfford(int slot, int amount){
        if(inventoryStackHandler.getItemVendor(slot).getCost() * (amount / inventoryStackHandler.getItemVendor(slot).getAmount()) <= getField(TileEconomyBase.FIELD_CASHRESERVE))
            return true;

        return false;
    }

    public int outputSlotCheck(ItemStack itemStack, int amount){
        //Checks if any stacks in output are equal to the itemStack and can handle it being added in
        for(int i = 0; i < TE_OUTPUT_SLOT_COUNT; i++){
            if(UtilMethods.equalStacks(itemStack, outputStackHandler.getStackInSlot(i))){
                if(outputStackHandler.getStackInSlot(i).getCount() +  amount <= outputStackHandler.getStackInSlot(i).getMaxStackSize()){
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

    public boolean bundleOutSlotCheck(int[] bundleSlots){
        int ignoreSlots = 0;

        for(int i = 0; i < bundleSlots.length; i++){
            for(int j = 0; j < outputStackHandler.getSlots(); j++) {
                if (UtilMethods.equalStacks(inventoryStackHandler.getItemVendor(bundleSlots[i]).getStack(), outputStackHandler.getStackInSlot(j))) {
                    if (outputStackHandler.getStackInSlot(j).getCount() + inventoryStackHandler.getItemVendor(bundleSlots[i]).getAmount() <= outputStackHandler.getStackInSlot(j).getMaxStackSize()) {
                        ignoreSlots++;
                    }
                }
            }
        }

        int emptySlots = 0;
        for(int i = 0; i < outputStackHandler.getSlots(); i++)
            if(outputStackHandler.getStackInSlot(i).isEmpty())
                emptySlots++;
        if(bundleSlots.length - ignoreSlots <= emptySlots)
            return true;

        return false;
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
            if((bank / (Float.parseFloat(ConfigCurrency.currencyValues[i])) * 100) > 0){ //Divisible by currency value
                int amount = (bank /((int)((Float.parseFloat(ConfigCurrency.currencyValues[i])) * 100)));

                if(amount > 64){
                    amount = 64;
                    repeat = true;
                }


                ItemStack outChange = new ItemStack(ModItems.itemCurrency, amount, i);

                if(blockBreak){ //If Block Breaking spawn item
                    world.spawnEntity(new EntityItem(world, getPos().getX(), getPos().getY(), getPos().getZ(), outChange));
                    if (mode) {
                        cashRegister -= ((Float.parseFloat(ConfigCurrency.currencyValues[i]) * 100) * amount);
                    } else {
                        cashReserve -= ((Float.parseFloat(ConfigCurrency.currencyValues[i]) * 100) * amount);
                    }
                }else {
                    int outputSlot = outputSlotCheck(outChange, amount);
                    if (outputSlot != -1) {
                        if (growOutItemSize(outChange, outputSlot).equals(ItemStack.EMPTY)) {
                            if (mode) {
                                cashRegister -= ((Float.parseFloat(ConfigCurrency.currencyValues[i]) * 100) * amount);
                            } else {
                                cashReserve -= ((Float.parseFloat(ConfigCurrency.currencyValues[i]) * 100) * amount);
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
                item.setCount(inventoryStackHandler.getItemVendor(i).getSize());
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

    public EnumDyeColor getColor(){
        return color;
    }

    public void setColor(EnumDyeColor color){
        this.color = color;
    }

    //If Sneak button held down, show a full stack (or as close to it)
    public int sneakFullStack(int index, int num) {
        int newNum = num;
        if(finite) {
            if (inventoryStackHandler.getItemVendor(index).getSize() < inventoryStackHandler.getItemVendor(index).getStack().getMaxStackSize()) {
                newNum = inventoryStackHandler.getItemVendor(index).getSize();
            } else newNum = inventoryStackHandler.getItemVendor(index).getStack().getMaxStackSize();

            while (newNum % inventoryStackHandler.getItemVendor(index).getAmount() != 0)
                newNum--;

        }else{
            newNum = inventoryStackHandler.getItemVendor(index).getStack().getMaxStackSize();
        }
        return newNum;
    }

    //If Jump button held down, show half a stack (or as close to it)
    public int jumpHalfStack(int index, int num) {
        int newNum = num;
        if(finite) {
            if (inventoryStackHandler.getItemVendor(index).getSize() < inventoryStackHandler.getItemVendor(index).getStack().getMaxStackSize() / 2) {
                newNum = inventoryStackHandler.getItemVendor(index).getSize();
            } else newNum = inventoryStackHandler.getItemVendor(index).getStack().getMaxStackSize() / 2;

            if (newNum < 1) newNum = 1;

            while (newNum % inventoryStackHandler.getItemVendor(index).getAmount() != 0)
                newNum--;
        }else{
            newNum = inventoryStackHandler.getItemVendor(index).getStack().getMaxStackSize() / 2;
        }

        if(newNum == 0) newNum = 1;

        return newNum;
    }

    public void setMessage(String newMessage, byte time){
        message = newMessage;
        messageTime = time;
    }

    public String getMessage(){
        return message;
    }

    public void restock(){
        //If Creative vending machine and finite, restock items
        if(creative && finite) {
            //Time since last opened in seconds
            long deltaTime = ((world.getTotalWorldTime() - serverTime) / 20);

            //Traverses through inventory Size to restock
            for (int i = 0; i < inventoryStackHandler.getSlots(); i++) {
                ItemVendor item = inventoryStackHandler.getItemVendor(i);
                if (item.getItemMax() != 0 && item.getTimeRaise() != 0 && !inventoryStackHandler.getStackInSlot(i).isEmpty()) {
                    if (item.getSize() < item.getItemMax()) {
                        if (item.getTimeRaise() < deltaTime + item.getTimeElapsed()) {
                            int restock = Math.toIntExact((deltaTime + item.getTimeElapsed()) / item.getTimeRaise());
                            item.setTimeElapsed(Math.toIntExact((deltaTime + item.getTimeElapsed()) % item.getTimeRaise()));

                            if (restock + item.getSize() > item.getItemMax()) {
                                item.setSize(item.getItemMax());
                            } else {
                                item.setSize(item.getSize() + restock);
                            }
                        }else{
                            item.setTimeElapsed(item.getTimeElapsed() + (int)deltaTime);
                        }
                    }
                }

                inventoryStackHandler.setItemVendor(i, item);
            }
            serverTime = world.getTotalWorldTime();
        }
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

    public ItemVendor getItemVendor(int i){
        return inventoryStackHandler.getItemVendor(i);
    }

    public void setItemVendor(int i, ItemVendor item){
        inventoryStackHandler.setItemVendor(i, item);
    }

    public void voidItem(int i){
        inventoryStackHandler.voidSlot(i);
    }
}
