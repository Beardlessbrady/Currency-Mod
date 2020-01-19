package beardlessbrady.modcurrency.block.tradein;

import beardlessbrady.modcurrency.ConfigCurrency;
import beardlessbrady.modcurrency.ModCurrency;
import beardlessbrady.modcurrency.block.TileEconomyBase;
import beardlessbrady.modcurrency.handler.StateHandler;
import beardlessbrady.modcurrency.item.ItemMoneyBag;
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

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-07-09
 */

public class TileTradein extends TileEconomyBase implements ICapabilityProvider{
    public final int TE_INPUT_SLOT_COUNT = 1;
    public final int TE_INVENTORY_SLOT_COUNT = 25;
    public final int TE_OUTPUT_SLOT_COUNT = 1;

    // Inventory handlers */
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

    public TileTradein(){
        inventoryLimit = 256;
        selectedName = "No Item Selected";
        creative = false;;
    }

    /** Runs every tick**/
    @Override
    public void update() {
        if (playerUsing != EMPTYID) { // If a player is NOT using the machine */
            if (mode) { // STOCK MODE & INPUT SLOT has currency in it*/
                if (inputStackHandler.getStackInSlot(0).getItem().equals(ModItems.itemCurrency)) {
                    ItemStack itemStack = inputStackHandler.getStackInSlot(0); // Input slot Item */

                    int amount = (int) (Float.parseFloat(ConfigCurrency.currencyValues[itemStack.getItemDamage()]) * 100); // Converts currency Value to float then multiples by 100 to covert to money system */
                    amount = amount * inputStackHandler.getStackInSlot(0).getCount(); // Multiply currency value by amount of items */

                    // If cash is not an obscene amount delete items in INPUT and add value to cashRegister */
                    if (amount + cashRegister <= 999999999) {
                        inputStackHandler.setStackInSlot(0, ItemStack.EMPTY);
                        cashRegister += amount;
                    } else { // Error message if player tries to put more than $9,999,999.99 in the machine */
                        setMessage("CAN'T FIT ANYMORE CURRENCY!", (byte) 40);
                        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 3.0F, false);
                    }
                }

            } else { // TRADE MODE & INPUT is not empty */
                if (!inputStackHandler.getStackInSlot(0).isEmpty()) {
                    byte flag = 0; // Flag to signify error, 1= Slot Limit Reached, 2=Not Enough Funds */

                    // Loops through inventoryStackHandler to try and find an item matching whats in the INPUT*/
                    searchLoop:
                    for (int i = 0; i < inventoryStackHandler.getSlots(); i++) {
                        if (!inventoryStackHandler.getStackInSlot(i).isEmpty()) {
                            if (UtilMethods.equalStacks(inputStackHandler.getStackInSlot(0), inventoryStackHandler.getStackInSlot(i)) &&
                                    inputStackHandler.getStackInSlot(0).getItemDamage() == inventoryStackHandler.getStackInSlot(i).getItemDamage()) {

                                // Collect items cost and the amount being placed in the INPUT*/
                                int cost = inventoryStackHandler.getItemTradein(i).getCost();
                                int inputAmount = inputStackHandler.getStackInSlot(0).getCount();

                                if (cashRegister < cost * inputAmount) { // If there isn't enough cash in the machine set input Amount to highest amount machine can afford */
                                    inputAmount = cashRegister / cost;

                                    if(inputAmount == 0) flag = 2; // Flags for not enough funds error */
                                }

                                if((inputAmount + inventoryStackHandler.getItemTradein(i).getSize()) > inventoryStackHandler.getSlotLimit(i)) { // Only allow up to the stack size limit*/
                                    inputAmount = inputAmount - (inventoryStackHandler.getItemTradein(i).getSize() + inputAmount - inventoryStackHandler.getSlotLimit(i));

                                    if(inputAmount == 0) flag = 1; // Flags for size limit reached */
                                }

                                // If there is enough money in machine and the machine has enough room for the item */
                                if (cashRegister >= cost * inputAmount && (inputAmount + inventoryStackHandler.getItemTradein(i).getSize()) <= inventoryStackHandler.getSlotLimit(i)) {
                                    cashReserve = cashReserve + cost * inputAmount; // Add money to players cash */
                                    cashRegister = cashRegister - cost * inputAmount; // Remove price from machine cash */

                                    inputStackHandler.getStackInSlot(0).shrink(inputAmount); // Remove item from input */
                                    inventoryStackHandler.getItemTradein(i).growSize(inputAmount); // Add item to machine */
                                }

                                // Error Message, flag determines what error is occuring */
                                if(flag != 0) {
                                    switch (flag) {
                                        case 1:
                                            setMessage("SLOT FULL", (byte) 40);
                                            break;
                                        case 2:
                                            setMessage("NOT ENOUGH FUNDS IN MACHINE", (byte) 40);
                                            break;
                                    }
                                    world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 3.0F, false);
                                }
                                break searchLoop;
                            }
                        }
                    }
                }
            }
            super.update();
        }
    }

    /** To open the GUI **/
    public void openGui(EntityPlayer player, World world, BlockPos pos){
        // If top open GUI from bottom as there is where all info is held */
        if(world.getBlockState(pos).getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP) {
            player.openGui(ModCurrency.instance, 31, world, pos.getX(), pos.down().getY(), pos.getZ());
        }else {
            player.openGui(ModCurrency.instance, 31, world, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    /** NBT Methods **/
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

        return new SPacketUpdateTileEntity(pos, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound compound = pkt.getNbtCompound();

        if(compound.hasKey("selectedName")) selectedName = compound.getString("selectedName");
        if(compound.hasKey("inventoryLimit")) inventoryLimit = compound.getInteger("inventoryLimit");
        if(compound.hasKey("selectedSlot")) selectedSlot = compound.getInteger("selectedSlot");

    }
    //</editor-fold>

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
            if (facing == null) return (T) new CombinedInvWrapper(inputStackHandler, inventoryStackHandler, outputStackHandler);
        }
        return super.getCapability(capability, facing);
    }
    //</editor-fold>

    /** ItemTradein Getter **/
    public ItemTradein getItemTradein(int i){
        return inventoryStackHandler.getItemTradein(i);
    }

    /** ItemTradein Setter **/
    public void setItemTradein(int i, ItemTradein item){
        inventoryStackHandler.setItemTradein(i, item);
    }

    /** Void item in InventoryStackHandler **/
    public void voidItem(int i){
        inventoryStackHandler.voidSlot(i);
    }

    /** Outputs Currency in Machine **/
    public void outChange(boolean blockBreak){
        // If Block  being broken spawn on ground */
        if(blockBreak) {
            int bank;

            // Loops through both cash register and reserve and outputs them */
            OUTER_LOOP: for(int i = ConfigCurrency.currencyValues.length-1; i >=0; i--){
                if(mode){
                    bank = cashRegister;
                }else{
                    bank = cashReserve;
                }

                boolean repeat = false;
                if((bank / (Float.parseFloat(ConfigCurrency.currencyValues[i])) * 100) > 0){ //Divisible by currency value
                    int amount = (bank /((int)((Float.parseFloat(ConfigCurrency.currencyValues[i])) * 100)));

                    if(amount > 64){ // If more then a stack repeat and create another stack of the same bill*/
                        amount = 64;
                        repeat = true;
                    }
                    ItemStack outChange = new ItemStack(ModItems.itemCurrency, amount, i);

                    // Spawn bill and subtract amount from cash amount in machine */
                    world.spawnEntity(new EntityItem(world, getPos().getX(), getPos().getY(), getPos().getZ(), outChange));
                    if (mode) {
                        cashRegister -= ((Float.parseFloat(ConfigCurrency.currencyValues[i]) * 100) * amount);
                    } else {
                        cashReserve -= ((Float.parseFloat(ConfigCurrency.currencyValues[i]) * 100) * amount);
                    }
                }

                if (repeat) i++;
                if(bank == 0) break OUTER_LOOP; // If bank is 0 stop loop */
            }
        } else { // If machine not being broken place money into money bag item (if OUTPUT is empty)*/
            if(outputStackHandler.getStackInSlot(0).isEmpty()) {
                if(mode && cashRegister != 0){ // STOCK MODE: CashRegister */
                    outputStackHandler.setStackInSlot(0, new ItemStack(ModItems.itemMoneyBag));
                    ItemMoneyBag.CurrencyToNBT(outputStackHandler.getStackInSlot(0), cashRegister);
                    cashRegister = 0;
                }else if(!mode && cashReserve != 0){ // TRADE MODE: CashReserve */
                    outputStackHandler.setStackInSlot(0, new ItemStack(ModItems.itemMoneyBag));
                    ItemMoneyBag.CurrencyToNBT(outputStackHandler.getStackInSlot(0), cashReserve);
                    cashReserve = 0;
                }
            }
        }
    }

    /** Drops inventory on block break **/
    public void dropInventory(){
        // Looks through TE inventory drops them and deletes them from the machine just in case*/
        for (int i = 0; i < inventoryStackHandler.getSlots(); i++) {
            ItemStack item = inventoryStackHandler.getStackInSlot(i);
            if (!item.isEmpty()) {
                item.setCount(inventoryStackHandler.getItemTradein(i).getSize());
                world.spawnEntity(new EntityItem(world, getPos().getX(), getPos().getY(), getPos().getZ(), item));
                inventoryStackHandler.setStackInSlot(i, ItemStack.EMPTY);
            }
        }

        // Looks at Output drops the inventory and deletes it from the machine just in case */
        for (int i = 0; i < outputStackHandler.getSlots(); i++){
            ItemStack item = outputStackHandler.getStackInSlot(i);
            if (item != ItemStack.EMPTY) {
                world.spawnEntity(new EntityItem(world, getPos().getX(), getPos().getY(), getPos().getZ(), item));
                outputStackHandler.setStackInSlot(i, ItemStack.EMPTY);
            }
        }

        // Looks at Input drops the inventory and deletes it from the machine just incase */
        for (int i = 0; i < inputStackHandler.getSlots(); i++){
            ItemStack item = inputStackHandler.getStackInSlot(i);
            if (item != ItemStack.EMPTY) {
                world.spawnEntity(new EntityItem(world, getPos().getX(), getPos().getY(), getPos().getZ(), item));
                inputStackHandler.setStackInSlot(i, ItemStack.EMPTY);   //Just in case
            }
        }
    }
}
