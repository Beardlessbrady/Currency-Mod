package beardlessbrady.modcurrency2.block.economyblocks.tradein;

import beardlessbrady.modcurrency2.ConfigCurrency;
import beardlessbrady.modcurrency2.ModCurrency;
import beardlessbrady.modcurrency2.block.economyblocks.TileEconomyBase;
import beardlessbrady.modcurrency2.handler.StateHandler;
import beardlessbrady.modcurrency2.item.ModItems;
import beardlessbrady.modcurrency2.utilities.UtilMethods;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
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

    private int renderCounter = 0;

    // Inventory handlers */
    private ItemStackHandler inputStackHandler = new ItemStackHandler(TE_INPUT_SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    };
    private ItemTradeinHandler inventoryStackHandler = new ItemTradeinHandler(TE_INVENTORY_SLOT_COUNT);

    private String selectedName;
    private int selectedSlot;

    public TileTradein(){
        selectedName = "No Item Selected";
    }

    /** Runs every tick**/
    @Override
    public void update() {
        if (playerUsing != EMPTYID) { // If a player is using the machine */
            if (mode) { // STOCK MODE & INPUT SLOT has currency in it*/
                if (inputStackHandler.getStackInSlot(0).getItem().equals(ModItems.itemCurrency)) {
                    ItemStack itemStack = inputStackHandler.getStackInSlot(0); // Input slot Item */

                    int amount = (int) (Float.parseFloat(ConfigCurrency.currencyValues[itemStack.getItemDamage()]) * 100); // Converts currency Value to float then multiples by 100 to covert to money system */
                    amount = amount * inputStackHandler.getStackInSlot(0).getCount(); // Multiply currency value by amount of items */

                    // If cash is not an obscene amount delete items in INPUT and add value to cashRegister */
                    if (amount + cashRegister <= 999999999) {
                        inputStackHandler.setStackInSlot(0, ItemStack.EMPTY);
                        cashRegister += amount;
                        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, 30.0F, false);

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
                        ItemTradein item = getItemTradein(i);
                        if (!inventoryStackHandler.getStackInSlot(i).isEmpty()) {
                            if (UtilMethods.equalStacks(inputStackHandler.getStackInSlot(0), item.getStack(), false)) {
                                // Collect items cost and the amount being placed in the INPUT*/
                                int cost = item.getCost();
                                int bulk = item.getAmount(); //Bulk Sell

                                long inputAmount = inputStackHandler.getStackInSlot(0).getCount() / bulk; //Divide by bulk size

                                if(inputAmount != 0) { //If input has high enough count to sell to specified bulk amount

                                    if (cashRegister < cost * inputAmount) { // If there isn't enough cash in the machine set input Amount to highest amount machine can afford */
                                        inputAmount = cashRegister / cost;

                                        if (inputAmount == 0) flag = 2; // Flags for not enough funds error */
                                    }

                                    // Only allow up to the stack size limit
                                    if (((inputAmount*bulk) + inventoryStackHandler.getItemTradein(i).getSize()) > inventoryStackHandler.getSlotLimit(i)) {
                                        inputAmount = ((inputAmount*bulk) - (inventoryStackHandler.getItemTradein(i).getSize() +
                                                (inputAmount*bulk) - inventoryStackHandler.getSlotLimit(i))) /bulk;

                                        if (inputAmount == 0) flag = 1; // Flags for size limit reached */
                                    }

                                    //Only allow up to the BUY UNTIL value of item (BUY UNTIL must be set higher then 0)
                                    if (((inputAmount*bulk) + inventoryStackHandler.getItemTradein(i).getSize()) > item.getUntil() && item.getUntil() > 0){
                                        inputAmount = ((inputAmount*bulk) - (inventoryStackHandler.getItemTradein(i).getSize() +
                                                (inputAmount*bulk) - item.getUntil())) /bulk;

                                        if (inputAmount == 0) flag = 3; // Flags for size limit reached */
                                    }

                                    // If there is enough money in machine and the machine has enough room for the item */
                                    if (cashRegister >= cost * inputAmount && ((inputAmount*bulk) + inventoryStackHandler.getItemTradein(i).getSize()) <= inventoryStackHandler.getSlotLimit(i)) {
                                        cashReserve = cashReserve + cost * inputAmount; // Add money to players cash */
                                        cashRegister = cashRegister - cost * inputAmount; // Remove price from machine cash */

                                        inputStackHandler.getStackInSlot(0).shrink((int)inputAmount*bulk); // Remove item from input */
                                        inventoryStackHandler.getItemTradein(i).growSize((int)inputAmount*bulk); // Add item to machine */

                                        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.05F, 5.0F, false);
                                    }
                                }

                                // Error Message, flag determines what error is occuring */
                                if (flag != 0) {
                                    switch (flag) {
                                        case 1:
                                            setMessage("SLOT FULL", (byte) 40);
                                            break;
                                        case 2:
                                            setMessage("NOT ENOUGH FUNDS IN MACHINE", (byte) 40);
                                            break;
                                        case 3:
                                            setMessage("NO LONGER ACCEPTING THIS ITEM", (byte) 40);
                                            break;
                                    }
                                    world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 3.0F, false);
                                }
                                break;
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

        compound.setString("selectedName", selectedName);
        compound.setInteger("selectedSlot", selectedSlot);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if(compound.hasKey("inventory")) inventoryStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("inventory"));
        if(compound.hasKey("input")) inputStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("input"));

        if(compound.hasKey("selectedName")) selectedName = compound.getString("selectedName");
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
        compound.setInteger("selectedSlot", selectedSlot);

        return new SPacketUpdateTileEntity(pos, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound compound = pkt.getNbtCompound();

        if(compound.hasKey("selectedName")) selectedName = compound.getString("selectedName");
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
            if (facing == null) return (T) new CombinedInvWrapper(inputStackHandler, inventoryStackHandler);
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

    public int getItemTradeinMax() {
        return inventoryStackHandler.getSlotLimit(0);
    }

    /** Void item in InventoryStackHandler **/
    public void voidItem(int i){
        inventoryStackHandler.voidSlot(i);
    }

    /** Outputs Currency in Machine **/
    public void outChange(boolean blockBreak) {
        long bank;

        OUTER_LOOP:
        for (int i = ConfigCurrency.currencyValues.length - 1; i >= 0; i--) {
            if (mode) {
                bank = cashRegister;
            } else {
                bank = cashReserve;
            }

            boolean repeat = false;
            if ((bank / (Float.parseFloat(ConfigCurrency.currencyValues[i])) * 100) > 0) { //Divisible by currency value
                long amount = (bank / ((long) ((Float.parseFloat(ConfigCurrency.currencyValues[i])) * 100)));

                if (amount > 64) {
                    amount = 64;
                    repeat = true;
                }

                if(amount != 0) {
                    ItemStack outChange = new ItemStack(ModItems.itemCurrency, (int)amount, i);

                    if (blockBreak) { //If Block is being broken spit to the ground
                        world.spawnEntity(new EntityItem(world, getPos().getX(), getPos().getY(), getPos().getZ(), outChange));
                        if (mode) {
                            cashRegister -= ((Float.parseFloat(ConfigCurrency.currencyValues[i]) * 100) * amount);
                        } else {
                            cashReserve -= ((Float.parseFloat(ConfigCurrency.currencyValues[i]) * 100) * amount);
                        }
                    } else { //If player is pulling cash from in GUI
                        if (world.getPlayerEntityByUUID(playerUsing) != null) { //Checks if a player is using machine
                            EntityPlayer player = world.getPlayerEntityByUUID(playerUsing);

                            if (player.addItemStackToInventory(outChange)) { //If successful in putting stack in players inventory remove amount from machine
                                if (mode) {
                                    cashRegister -= ((Float.parseFloat(ConfigCurrency.currencyValues[i]) * 100) * amount);
                                } else {
                                    cashReserve -= ((Float.parseFloat(ConfigCurrency.currencyValues[i]) * 100) * amount);
                                }
                            } else { //If cant fit stack error message
                                setMessage("CAN'T FIT IN INVENTORY!!", (byte) 40);
                                world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 3.0F, false);
                                break OUTER_LOOP;
                            }
                        } else { //If Player is Null somehow and using GUI then exit
                            break OUTER_LOOP;
                        }
                    }
                }
            }

            if (repeat) i++;
            if (bank == 0) break OUTER_LOOP;
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

        // Looks at Input drops the inventory and deletes it from the machine just incase */
        for (int i = 0; i < inputStackHandler.getSlots(); i++){
            ItemStack item = inputStackHandler.getStackInSlot(i);
            if (item != ItemStack.EMPTY) {
                world.spawnEntity(new EntityItem(world, getPos().getX(), getPos().getY(), getPos().getZ(), item));
                inputStackHandler.setStackInSlot(i, ItemStack.EMPTY);   //Just in case
            }
        }
    }

    /** Counter for Model Renderer **/
    public double getRenderCounter(){
        renderCounter++;
        if(renderCounter >= 700) //Fail safe if for some reason the renderCounter tries to go really high
            renderCounter = 0;
        return renderCounter -1; //Returns count before being incremented
    }

    /** Model Renderer Counter **/
    public void setRenderCounter(int num){
        renderCounter = num;
    }
}
